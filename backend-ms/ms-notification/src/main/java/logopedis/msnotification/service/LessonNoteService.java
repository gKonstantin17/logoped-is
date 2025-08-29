package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.dto.lessonNote.LessonNoteChangeDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.msnotification.repository.LessonNoteRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class LessonNoteService {
    private final LessonNoteRepository repository;
    private final LessonStatusUpdater lessonStatusUpdater;
    public LessonNoteService(LessonNoteRepository repository, LessonStatusUpdater lessonStatusUpdater) {
        this.repository = repository;
        this.lessonStatusUpdater = lessonStatusUpdater;
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
    public CompletableFuture<ServiceResult<LessonNote>> create(LessonNote lessonNote) {
        try {
            LessonNote result = repository.save(lessonNote);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public void createOrUpdateStatus(LessonNote lessonNote) {
        LessonNote existing = repository.findById(lessonNote.getId()).orElse(null);

        // Вычисляем актуальный статус
        LessonStatus newStatus = lessonStatusUpdater.updateStatusLesson(lessonNote);

        if (existing != null) {
            LessonStatus oldStatus = existing.getStatus();
            if (oldStatus != newStatus) {
                existing.setStatus(newStatus);
                repository.save(existing);
            }
            // если статусы совпадают — ничего не делаем
        } else {
            lessonNote.setStatus(newStatus);
            repository.save(lessonNote);
        }
    }
    @Async
    public CompletableFuture<ServiceResult<LessonNote>> update(Long id, LessonNoteChangeDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Занятие не найдено");

            if (dto.status() != null)     updated.setStatus(dto.status());
            if (dto.startTime() != null)    updated.setStartTime(dto.startTime());

            LessonNote saved = repository.save(updated);

            return AsyncResult.success(repository.save(saved));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    public void save(LessonNote changedLessonNote) {
        repository.save(changedLessonNote);
    }
    public List<LessonNote> findByPeriod(Timestamp start, Timestamp end) {
        return repository.findByStartTimeBetween(start,end);
    }
    public List<LessonNote> findByPeriodAndStatuses(Timestamp start, Timestamp end, List<LessonStatus> statuses) {
        return repository.findByStartTimeBetweenAndStatusIn(start, end, statuses);
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
}
