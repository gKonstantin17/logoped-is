package gk17.rsmain.service;

import gk17.rsmain.dto.lesson.LessonDto;
import gk17.rsmain.dto.lesson.LessonReadDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Lesson;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.repository.HomeworkRepository;
import gk17.rsmain.repository.LessonRepository;
import gk17.rsmain.repository.LogopedRepository;
import gk17.rsmain.repository.PatientRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
        List<LessonReadDto> lessons = data.stream().map(lesson -> new LessonReadDto(
                lesson.getId(),
                lesson.getType(),
                lesson.getTopic(),
                lesson.getDescription(),
                lesson.getDateOfLesson(),
                lesson.getLogoped() != null ? lesson.getLogoped().getId() : null,
                lesson.getHomework() != null ? lesson.getHomework().getId() : null,
                lesson.getPatients().stream()
                        .map(Patient::getId)
                        .toList()
        )).toList();

        return AsyncResult.success(lessons);
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
                var logoped = logopedRepository.findById(dto.logopedId());
                if (logoped.isEmpty()) {
                    return AsyncResult.error("Логопед не найден");
                }
                lesson.setLogoped(logoped.get());
            }

            if (dto.homeworkId() != null) {
                var homework = homeworkRepository.findById(dto.homeworkId());
                if (homework.isEmpty()) {
                    return AsyncResult.error("Домашнее задание не найдено");
                }
                lesson.setHomework(homework.get());
            }
            Set<Patient> patients = dto.patientsId() == null
                    ? Set.of()
                    : new HashSet<>(patientRepository.findAllById(dto.patientsId()));

            lesson.setPatients(patients);
            repository.save(lesson);
            LessonReadDto createdDto = new LessonReadDto(
                    lesson.getId(),
                    lesson.getType(),
                    lesson.getTopic(),
                    lesson.getDescription(),
                    lesson.getDateOfLesson(),
                    lesson.getLogoped() != null ? lesson.getLogoped().getId() : null,
                    lesson.getHomework() != null ? lesson.getHomework().getId() : null,
                    lesson.getPatients().stream().map(Patient::getId).toList()
            );
            return AsyncResult.success(createdDto);

        } catch(Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<LessonReadDto>> update(Long id, LessonDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Урок не найден");
            var result = data.get();

            if (dto.type() != null) result.setType(dto.type());
            if (dto.topic() != null) result.setTopic(dto.topic());
            if (dto.description() != null) result.setDescription(dto.description());
            if (dto.dateOfLesson() != null) result.setDateOfLesson(dto.dateOfLesson());

            if (dto.logopedId() != null) {
                var logoped = logopedRepository.findById(dto.logopedId());
                if (logoped.isEmpty())
                    return AsyncResult.error("Логопед не найден");
                result.setLogoped(logoped.get());
            }

            if (dto.homeworkId() != null) {
                var homework = homeworkRepository.findById(dto.homeworkId());
                if (homework.isEmpty())
                    return AsyncResult.error("Домашнее задание не найдено");
                result.setHomework(homework.get());
            }
            if (dto.patientsId() != null) {
                Set<Patient> patients  = new HashSet<>(patientRepository.findAllById(dto.patientsId()));
                if (patients.size() != dto.patientsId().size()) {
                    return AsyncResult.error("Некоторые пациенты не найдены");
                }
                result.setPatients(patients);
            }

            repository.save(result);

            LessonReadDto updatedDto = new LessonReadDto(
                    result.getId(),
                    result.getType(),
                    result.getTopic(),
                    result.getDescription(),
                    result.getDateOfLesson(),
                    result.getLogoped() != null ? result.getLogoped().getId() : null,
                    result.getHomework() != null ? result.getHomework().getId() : null,
                    result.getPatients().stream().map(Patient::getId).toList()
            );
            return AsyncResult.success(updatedDto);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Логопед не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData.getId());
    }
}
