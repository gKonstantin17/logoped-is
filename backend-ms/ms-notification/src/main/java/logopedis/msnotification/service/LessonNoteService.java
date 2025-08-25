package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.lessonNote.LessonNoteChangeDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.msnotification.repository.LessonNoteRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class LessonNoteService {
    private final LessonNoteRepository repository;
    public LessonNoteService(LessonNoteRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<LessonNote>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    public Optional<LessonNote> findById(Long id) {
        return repository.findById(id);
    }

    @Async
    public CompletableFuture<ServiceResult<LessonNote>> create(LessonNoteChangeDto dto) {
        try {
            LessonNote lessonNote = lessonNoteFromDto(dto);
            LessonNote result = repository.save(lessonNote);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<LessonNote>> update(Long id, LessonNoteChangeDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Логопед не найден");

            if (dto.status() != null)     updated.setStatus(dto.status());
            if (dto.startTime() != null)    updated.setStartTime(dto.startTime());

            LessonNote saved = repository.save(updated);

            return AsyncResult.success(repository.save(saved));
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


    private LessonNote lessonNoteFromDto(LessonNoteChangeDto dto) {
        LessonNote lessonNote = new LessonNote();
        lessonNote.setStatus(dto.status());
        lessonNote.setStartTime(dto.startTime());
        return lessonNote;
    }
}
