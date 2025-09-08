package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.enums.NotificationMsg;
import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libentities.kafka.LessonsForPeriodDto;
import logopedis.libentities.msnotification.dto.lessonNote.LessonNoteChangeDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.msnotification.repository.LessonNoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class LessonNoteService {
    private final LessonNoteRepository repository;
    private final LessonStatusUpdater lessonStatusUpdater;
    private final RecipientService recipientService;
    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(LessonNoteService.class);
    public LessonNoteService(LessonNoteRepository repository, LessonStatusUpdater lessonStatusUpdater, RecipientService recipientService, NotificationService notificationService) {
        this.repository = repository;
        this.lessonStatusUpdater = lessonStatusUpdater;
        this.recipientService = recipientService;
        this.notificationService = notificationService;
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
    public void updateStatus(LessonNote lessonNote) {
        LessonNote existing = repository.findById(lessonNote.getId()).orElse(null);
        if (existing == null) return;

        LessonStatus newStatus = lessonNote.getStatus();
        LessonStatus oldStatus = existing.getStatus();

        boolean statusChanged = oldStatus != newStatus;
        boolean dateChanged = !Objects.equals(existing.getStartTime(), lessonNote.getStartTime());

        if (statusChanged) {
            existing.setStatus(newStatus);
            var result = repository.save(existing);
            notificationService.createFromLessonNote(result);
        }
        // если статусы совпадают — ничего не делаем
        if (dateChanged) {
            existing.setStartTime(lessonNote.getStartTime());
            repository.save(existing);
            notificationService.createForDateChange(existing);
        }
    }

    @Async
    public void createWithRecipient(LessonNoteWithRecipientDto dto) {
        LessonNote existing = repository.findById(dto.id()).orElse(null);
        LessonNote received = dtoWithRecipientToLessonNote(dto);
        LessonStatus newStatus = lessonStatusUpdater.updateStatusLesson(received);

        if (existing != null) {
            // Проверяем изменения статуса
            boolean statusChanged = existing.getStatus() != newStatus;
            if (statusChanged) {
                existing.setStatus(newStatus);
                var result = repository.save(existing);
                notificationService.createFromLessonNote(result);
            }

            // Проверяем изменения даты
            boolean dateChanged = !Objects.equals(existing.getStartTime(), received.getStartTime());
            if (dateChanged) {
                existing.setStartTime(received.getStartTime());
                repository.save(existing);
                notificationService.createForDateChange(existing);
            }

        } else {
            // Новое занятие
            received.setStatus(newStatus);
            var result = repository.save(received);
            recipientService.createFromDto(dto.recipientDtos(), received);
            notificationService.createFromLessonNote(result);
        }
    }


    @Async
    public void createForLessonPeriod(LessonsForPeriodDto lessonsForPeriod) {
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (LessonNoteWithRecipientDto dto : lessonsForPeriod.list()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> createWithRecipient(dto));
                futures.add(future);
            }

            // Дожидаемся завершения всех асинхронных операций
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("Синхронизация занятий окончена");
        } catch (Exception ex) {
            log.error("Ошибка при синхронизации занятий: " + ex.getMessage(), ex);
        }
    }


    private static LessonNote dtoWithRecipientToLessonNote(LessonNoteWithRecipientDto dto) {
        LessonNote recieved = new LessonNote();
        recieved.setId(dto.id());
        recieved.setStatus(dto.status());
        recieved.setStartTime(dto.startTime());
        recieved.setLogopedId(dto.logopedId());
        return recieved;
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

    public LessonNote save(LessonNote changedLessonNote) {
        return repository.save(changedLessonNote);
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
