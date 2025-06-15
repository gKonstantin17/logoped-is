package gk17.rsmain.service;

import gk17.rsmain.dto.homework.HomeworkDto;
import gk17.rsmain.dto.lesson.LessonDto;
import gk17.rsmain.dto.lesson.LessonReadDto;
import gk17.rsmain.dto.lesson.LessonWithFKDto;
import gk17.rsmain.dto.lesson.LessonWithHomeworkDto;
import gk17.rsmain.dto.logoped.LogopedDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientWithoutFK;
import gk17.rsmain.dto.patient.PatientWithoutFKDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Homework;
import gk17.rsmain.entity.Lesson;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.repository.*;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class LessonService {
    private final LessonRepository repository;
    private final LogopedRepository logopedRepository;
    private final HomeworkRepository homeworkRepository;
    private final PatientRepository patientRepository;

    public LessonService(LessonRepository repository, LogopedRepository logopedRepository, HomeworkRepository homeworkRepository, PatientRepository patientRepository) {
        this.repository = repository;
        this.logopedRepository = logopedRepository;
        this.homeworkRepository = homeworkRepository;
        this.patientRepository = patientRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<LessonReadDto>>> findall() {
        var data = repository.findAll();
        List<LessonReadDto> lessons = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(lessons);
    }
    @Async
    public CompletableFuture<ServiceResult<List<LessonWithFKDto>>> findByUserId(UUID userId) {
        var patients = patientRepository.findByUserId(userId);

        if (patients == null || patients.isEmpty()) {
            return AsyncResult.error("Пациенты не найдены");
        }

        List<Lesson> allLessons = new ArrayList<>();

        for (Patient patient : patients) {
            List<Lesson> lessons = repository.findByPatientsId(patient.getId());
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
    public CompletableFuture<ServiceResult<LessonReadDto>> create(LessonDto dto) {
        try {
            Lesson lesson = new Lesson();
            lesson.setType(dto.type());
            lesson.setTopic(dto.topic());
            lesson.setDescription(dto.description());
            lesson.setDateOfLesson(dto.dateOfLesson());

            if (dto.logopedId() != null) {
                var logoped = ResponseHelper.findById(logopedRepository,dto.logopedId(),"Логопед не найден");
                lesson.setLogoped(logoped);
            }

            if (dto.homeworkId() != null) {
                var homework = ResponseHelper.findById(homeworkRepository,dto.homeworkId(),"Домашнее задание не найдено");
                lesson.setHomework(homework);
            }
            Set<Patient> patients = dto.patientsId() == null
                    ? Set.of()
                    : new HashSet<>(patientRepository.findAllById(dto.patientsId()));

            lesson.setPatients(patients);
            repository.save(lesson);
            var createdDto = toReadDto(repository.findById(lesson.getId()).get());
            return AsyncResult.success(createdDto);

        } catch(Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> createLessonWithHomework(LessonWithHomeworkDto dto) {
        try {
            Lesson lesson = new Lesson();
            lesson.setType(dto.type());
            lesson.setTopic(dto.topic());
            lesson.setDescription(dto.description());
            lesson.setDateOfLesson(dto.dateOfLesson());

            if (dto.logopedId() != null) {
                var logoped = ResponseHelper.findById(logopedRepository, dto.logopedId(), "Логопед не найден");
                lesson.setLogoped(logoped);
            } else {
                // Получаем всех логопедов
                List<Logoped> logopeds = logopedRepository.findAll();

                // Подгружаем пациентов и группируем по логопеду
                List<Patient> allPatients = patientRepository.findAll();

                Map<UUID, Long> logopedPatientCounts = allPatients.stream()
                        .filter(p -> p.getLogoped() != null)
                        .collect(Collectors.groupingBy(
                                p -> p.getLogoped().getId(),
                                Collectors.counting()
                        ));

                // Выбираем логопеда с наименьшим количеством пациентов
                Logoped selectedLogoped = logopeds.stream()
                        .min(Comparator.comparing(logoped ->
                                logopedPatientCounts.getOrDefault(logoped.getId(), 0L)))
                        .orElseThrow(() -> new IllegalStateException("Нет доступных логопедов"));

                lesson.setLogoped(selectedLogoped);
                // Назначаем логопеда всем пациентам занятия
                if (dto.patientsId() != null) {
                    List<Patient> patientsToUpdate = patientRepository.findAllById(dto.patientsId());
                    for (Patient patient : patientsToUpdate) {
                        patient.setLogoped(selectedLogoped);
                    }
                    patientRepository.saveAll(patientsToUpdate);
                }
            }

            // Создаём Homework
            if (dto.homework() != null && !dto.homework().isBlank()) {
                Homework homework = new Homework();
                homework.setTask(dto.homework());
                homeworkRepository.save(homework);
                lesson.setHomework(homework);
            }

            Set<Patient> patients = dto.patientsId() == null
                    ? Set.of()
                    : new HashSet<>(patientRepository.findAllById(dto.patientsId()));
            lesson.setPatients(patients);

            repository.save(lesson);

            var createdLesson = repository.findById(lesson.getId()).get();
            var readDto = toReadDto(createdLesson);
            return AsyncResult.success(readDto);

        } catch (Exception ex) {
            return AsyncResult.error("Ошибка при создании урока: " + ex.getMessage());
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
                var logoped = ResponseHelper.findById(logopedRepository,dto.logopedId(),"Логопед не найден");
                updated.setLogoped(logoped);
            }

            if (dto.homeworkId() != null) {
                var homework = ResponseHelper.findById(homeworkRepository,dto.homeworkId(),"Домашнее задание не найдено");
                updated.setHomework(homework);
            }
            if (dto.patientsId() != null) {
                Set<Patient> patients  = new HashSet<>(patientRepository.findAllById(dto.patientsId()));
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
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Логопед не найден");
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
                lesson.getLogoped() == null ? null :
                        new LogopedDto(
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
}
