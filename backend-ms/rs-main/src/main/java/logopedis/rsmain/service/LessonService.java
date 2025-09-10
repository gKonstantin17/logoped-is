package logopedis.rsmain.service;

import jakarta.transaction.Transactional;
import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libentities.kafka.LessonStatusDto;
import logopedis.libentities.kafka.LessonsForPeriodDto;
import logopedis.libentities.msnotification.dto.recipient.RecipientDataDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.rsmain.dto.homework.HomeworkDto;
import logopedis.libentities.rsmain.dto.lesson.*;
import logopedis.libentities.rsmain.dto.logoped.LogopedDto;
import logopedis.libentities.rsmain.dto.patient.PatientReadDto;
import logopedis.libentities.rsmain.dto.patient.PatientWithoutFKDto;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Homework;
import logopedis.libentities.rsmain.entity.Lesson;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.libentities.rsmain.entity.Patient;
import logopedis.rsmain.kafka.LessonNoteKafkaProducer;
import logopedis.rsmain.kafka.LessonNoteRecipientKafkaProducer;
import logopedis.rsmain.repository.*;
import logopedis.libutils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LessonService {
    private final LessonRepository repository;
    private final LogopedService logopedService;
    private final HomeworkService homeworkService;
    private final PatientService patientService;
    private final LessonNoteRecipientKafkaProducer lessonNoteRecipientKafkaProducer;
    private final LessonNoteKafkaProducer lessonNoteKafkaProducer;
    private final UserService userService;

    public LessonService(LessonRepository repository, LogopedService logopedService, HomeworkService homeworkService, PatientService patientService, LessonNoteRecipientKafkaProducer lessonNoteKafkaProducer, LessonNoteKafkaProducer lessonNoteKafkaProducer1, UserService userService) {
        this.repository = repository;
        this.logopedService = logopedService;
        this.homeworkService = homeworkService;
        this.patientService = patientService;
        this.lessonNoteRecipientKafkaProducer = lessonNoteKafkaProducer;
        this.lessonNoteKafkaProducer = lessonNoteKafkaProducer1;
        this.userService = userService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<LessonReadDto>>> findall() {
        var data = repository.findAll();
        List<LessonReadDto> lessons = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(lessons);
    }

    public Lesson findById(Long id) {
        return repository.findById(id).get();
    }
    @Async
    public CompletableFuture<ServiceResult<List<LessonWithFKDto>>> findByUserId(UUID userId) {
        List<PatientReadDto> patients = patientService.findByUserId(userId).join().data();
        if (patients == null || patients.isEmpty()) {
            return AsyncResult.error("Пациенты не найдены");
        }

        List<Lesson> allLessons = new ArrayList<>();

        for (PatientReadDto patient : patients) {
            List<Lesson> lessons = repository.findByPatientsId(patient.id());
            allLessons.addAll(lessons);
        }

        List<LessonWithFKDto> lessonDtos = allLessons.stream()
                .map(this::toReadDtoWithFK)
                .toList();

        return AsyncResult.success(lessonDtos);
    }

    @Async
    public CompletableFuture<ServiceResult<List<LessonWithFKDto>>> findByLogopedId(UUID logopedId) {
        var data = repository.findByLogopedId(logopedId);
        List<LessonWithFKDto> lessons = data.stream().map(this::toReadDtoWithFK).toList();
        return AsyncResult.success(lessons);
    }
    @Async
    public CompletableFuture<ServiceResult<LessonWithFKDto>> findByIdWithFK(Long id) {
        var lesson = repository.findById(id).get();
        var result = toReadDtoWithFK(lesson);
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> createLessonWithHomework(LessonWithHomeworkDto dto) {
        try {
            Lesson lesson = new Lesson();
            lesson.setType(dto.type());
            lesson.setTopic(dto.topic());
            lesson.setDescription(dto.description());
            lesson.setDateOfLesson(dto.dateOfLesson());
            lesson.setStatus(LessonStatus.PLANNED);

            if (dto.logopedId() != null) {
                var logoped = logopedService.findById(dto.logopedId()).get();
                lesson.setLogoped(logoped);
            } else {
                // Выбираем логопеда с наименьшим количеством пациентов
                Logoped selectedLogoped = chooseLogoped();

                lesson.setLogoped(selectedLogoped);
                // Назначаем логопеда всем пациентам занятия
                if (dto.patientsId() != null) {
                    List<Patient> patientsToUpdate = patientService.findAllById(dto.patientsId());
                    for (Patient patient : patientsToUpdate) {
                        patient.setLogoped(selectedLogoped);
                    }
                    patientService.createAll(patientsToUpdate);
                }
            }

            if (dto.homework() != null && !dto.homework().isBlank()) {
                Homework homework = homeworkService.create(dto.homework());
                lesson.setHomework(homework);
            }

            Set<Patient> patients = dto.patientsId() == null
                    ? Set.of()
                    : new HashSet<>(patientService.findAllById(dto.patientsId()));
            lesson.setPatients(patients);

            repository.save(lesson);

            var createdLesson = repository.findById(lesson.getId()).get();
            var readDto = toReadDto(createdLesson);

            // отправка по Kafka

            LessonNoteWithRecipientDto lessonNote = lessonToLessonNodeDto(createdLesson);
            lessonNoteRecipientKafkaProducer.sendLessonNoteRecipient(lessonNote);

            return AsyncResult.success(readDto);

        } catch (Exception ex) {
            return AsyncResult.error("Ошибка при создании урока: " + ex.getMessage());
        }
    }

    public CompletableFuture<ServiceResult<AvailableTimeDto>>  checkTime(Long patientId,Timestamp currentDate) {
        try {
            // по пациенту и дате находить логопеда и его занятия на эту дату
            // если диагностика и logoped null?
            List<String> allTimeSlots = IntStream.rangeClosed(10, 19)
                    .mapToObj(hour -> String.format("%02d:00", hour))
                    .collect(Collectors.toList());

            Logoped logoped = patientService.findLogoped(patientId);
            if (logoped == null)
                logoped = chooseLogoped();

            Timestamp targetDate = currentDate; // Timestamp
            LocalDate date = targetDate.toLocalDateTime().toLocalDate();

            // Получаем начало и конец дня
            Timestamp start = Timestamp.valueOf(date.atStartOfDay());
            Timestamp end = Timestamp.valueOf(date.atTime(LocalTime.MAX));
            List<Lesson> lessons = repository.findByLogopedIdAndDateRange(logoped.getId(), start, end);

            Set<String> busySlots = lessons.stream()
                    .map(Lesson::getDateOfLesson)
                    .map(Timestamp::toLocalDateTime)
                    .map(dt -> String.format("%02d:00", dt.getHour()))
                    .collect(Collectors.toSet());

            // Возвращаем только свободные
            var result = new AvailableTimeDto(allTimeSlots.stream()
                    .filter(slot -> !busySlots.contains(slot))
                    .collect(Collectors.toList()));
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error("Ошибка при поиске свободного времени: " + ex.getMessage());
        }


    }
    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> canselLesson(Long id) {
        try {
            // TODO: добавить логику отмену за разных ролей
            var lesson = repository.findById(id).get();
            lesson.setStatus(LessonStatus.CANCELED_BY_CLIENT);
            var changed = repository.save(lesson);

            LessonNote lessonNote = lessonToLessonNode(changed);
            lessonNoteKafkaProducer.sendLessonNote(lessonNote);
            var result = toReadDto(changed);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> changeDate(Long id, Timestamp newDate) {
        try {
            var lesson = repository.findById(id).get();
            if (Objects.equals(lesson.getDateOfLesson(), newDate))
                return AsyncResult.error("Дата не изменена");

            lesson.setDateOfLesson(newDate);
            var changed = repository.save(lesson);

            LessonNote lessonNote = lessonToLessonNode(changed);
            lessonNoteKafkaProducer.sendLessonNote(lessonNote);

            var result = toReadDto(changed);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> update(Long id, LessonDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Урок не найден");

            if (dto.type() != null) updated.setType(dto.type());
            if (dto.topic() != null) updated.setTopic(dto.topic());
            if (dto.description() != null) updated.setDescription(dto.description());
            if (dto.dateOfLesson() != null) updated.setDateOfLesson(dto.dateOfLesson());

            if (dto.logopedId() != null) {
               var logoped = logopedService.findById(dto.logopedId()).get();
                updated.setLogoped(logoped);
            }

            if (dto.homeworkId() != null) {
                var homework = homeworkService.findById(dto.homeworkId()).get();
                updated.setHomework(homework);
            }
            if (dto.patientsId() != null) {
                Set<Patient> patients  = new HashSet<>(patientService.findAllById(dto.patientsId()));
                if (patients.size() != dto.patientsId().size()) {
                    return AsyncResult.error("Некоторые пациенты не найдены");
                }
                updated.setPatients(patients);
            }

            repository.save(updated);

            var updatedDto = toReadDto(repository.findById(updated.getId()).get());
            return AsyncResult.success(updatedDto);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    public void updateStatusFromKafka(LessonStatusDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,dto.id(),"Занятие не найдено");
            updated.setStatus(dto.status());
            repository.save(updated);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public CompletableFuture<ServiceResult<LessonReadDto>> updateStatusFromFE(LessonStatusDto dto) {
        try {
            Lesson updated = ResponseHelper.findById(repository,dto.id(),"Занятие не найдено");
            updated.setStatus(dto.status());
            var result = repository.save(updated);

            LessonNote lessonNote = lessonToLessonNode(result);
            lessonNoteKafkaProducer.sendLessonNote(lessonNote);

            var updatedDto = toReadDto(updated);
            return AsyncResult.success(updatedDto);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    public CompletableFuture<ServiceResult<LessonWithFKDto>> changeLesson(LessonChangeDto dto) {
        try {
            Lesson updated = ResponseHelper.findById(repository,dto.id(),"Занятие не найдено");
            if (dto.type() != null) updated.setType(dto.type());
            if (dto.topic() != null)  updated.setTopic(dto.topic());
            if (dto.description() != null) updated.setDescription(dto.description());

            if (dto.patients() != null) {
                Set<Patient> newPatients = new HashSet<>(patientService.findAllById(dto.patients()));
                updated.setPatients(newPatients);
            }

            if (dto.homework() != null) {
                if (updated.getHomework() == null) {
                    Homework newHw = new Homework();
                    newHw.setTask(dto.homework().task());
                    homeworkService.save(newHw);
                    updated.setHomework(newHw);
                } else {
                    updated.getHomework().setTask(dto.homework().task());
                }
            }

            var result = repository.save(updated);

            LessonNote lessonNote = lessonToLessonNode(result);
            lessonNoteKafkaProducer.sendLessonNote(lessonNote);

            var updatedDto = toReadDtoWithFK(updated);
            return AsyncResult.success(updatedDto);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Занятие не найдено");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private LessonReadDto toReadDto (Lesson entity) {
        return new LessonReadDto(
          entity.getId(),
          entity.getType(),
          entity.getTopic(),
          entity.getDescription(),
          entity.getDateOfLesson(),
          entity.getStatus(),
          entity.getLogoped() != null ? entity.getLogoped().getId() : null,
          entity.getHomework() != null ? entity.getHomework().getId() : null,
          entity.getPatients() != null ? entity.getPatients().stream().map(Patient::getId).toList()
                  : List.of()
        );
    }
    private LessonWithFKDto toReadDtoWithFK (Lesson lesson) {
        return new LessonWithFKDto(
                lesson.getId(),
                lesson.getType(),
                lesson.getTopic(),
                lesson.getDescription(),
                lesson.getDateOfLesson(),
                lesson.getStatus(),
                lesson.getLogoped() == null ? null :
                        new LogopedDto(
                                lesson.getLogoped().getId(),
                                lesson.getLogoped().getFirstName(),
                                lesson.getLogoped().getLastName(),
                                lesson.getLogoped().getPhone(),
                                lesson.getLogoped().getEmail()
                        ),
                lesson.getHomework() == null ? null :
                        new HomeworkDto(lesson.getHomework().getTask()),
                lesson.getPatients().stream()
                        .map(p -> new PatientWithoutFKDto(p.getId(), p.getFirstName(), p.getLastName(),p.getDateOfBirth()))
                        .toList()
        );
    }

    private Logoped chooseLogoped() {
        // Получаем всех логопедов
        List<Logoped> logopeds = logopedService.findall()
                .join()
                .data();

        // Подгружаем пациентов и группируем по логопеду
        List<PatientReadDto> allPatients = patientService.findall().join().data();

        Map<UUID, Long> logopedPatientCounts = allPatients.stream()
                .filter(p -> p.logopedId() != null)
                .collect(Collectors.groupingBy(
                        PatientReadDto::logopedId,
                        Collectors.counting()
                ));

        // Выбираем логопеда с наименьшим количеством пациентов
        Logoped selectedLogoped = logopeds.stream()
                .min(Comparator.comparing(logoped ->
                        logopedPatientCounts.getOrDefault(logoped.getId(), 0L)))
                .orElseThrow(() -> new IllegalStateException("Нет доступных логопедов"));
        return selectedLogoped;
    }

    public List<Lesson> findByPeriod(Timestamp start, Timestamp end) {
        return repository.findByDateOfLessonBetween(start,end)
                .stream()
                .toList();
    }

    @Async
    @Transactional
    public CompletableFuture<LessonsForPeriodDto> createResponseInLessonNote(Timestamp start, Timestamp end) {
        List<Lesson> lessons = findByPeriod(start, end);

        List<LessonNoteWithRecipientDto> list = lessons.stream()
                .map(this::lessonToLessonNodeDto)
                .toList();

        LessonsForPeriodDto dto = new LessonsForPeriodDto(list);

        return CompletableFuture.completedFuture(dto);
    }
    private LessonNoteWithRecipientDto lessonToLessonNodeDto(Lesson lesson) {
        List<RecipientDataDto> recipientDtos = new ArrayList<>();
        List<Long> patientIds = lesson.getPatients()
                .stream()
                .map(Patient::getId)
                .toList();

        for (Long patientId : patientIds) {
            UUID userId = userService.findByPatient(patientId).getId();
            recipientDtos.add(new RecipientDataDto(patientId, userId));
        }

        LessonNoteWithRecipientDto dto = new LessonNoteWithRecipientDto(
                lesson.getId(),
                lesson.getStatus(),
                lesson.getDateOfLesson(),
                lesson.getLogoped().getId(),
                recipientDtos
        );
        return dto;
    }
    private LessonNote lessonToLessonNode(Lesson lesson) {
        LessonNote lessonNote = new LessonNote();
        lessonNote.setId(lesson.getId());
        lessonNote.setStartTime(lesson.getDateOfLesson());
        lessonNote.setStatus(lesson.getStatus());
        lessonNote.setLogopedId(lesson.getLogoped().getId());
        return lessonNote;
    }
}
