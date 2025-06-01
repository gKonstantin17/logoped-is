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
import gk17.rsmain.utils.hibernate.ResponseHelper;
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
        List<LessonReadDto> lessons = data.stream().map(this::toReadDto).toList();
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
}
