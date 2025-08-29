package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.notification.NotificationCreateDto;
import logopedis.libentities.msnotification.dto.notification.NotificationUpdateDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Notification;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.msnotification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final LessonNoteService lessonNoteService;
    public NotificationService(NotificationRepository repository, LessonNoteService lessonNoteService) {
        this.repository = repository;
        this.lessonNoteService = lessonNoteService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<Notification>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }

    @Async
    public CompletableFuture<ServiceResult<Notification>> create(NotificationCreateDto dto) {
        try {
            LessonNote lessonNote = lessonNoteService.findById(dto.lessonNoteId()).get();

            Notification notification = notificationFromDto(dto,lessonNote);
            Notification result = repository.save(notification);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Notification>> update(Long id, NotificationUpdateDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Уведомление не найдено");

            if (dto.sendDate() != null)     updated.setSendDate(dto.sendDate());
            if (dto.message() != null)     updated.setMessage(dto.message());
            if (dto.received() != null)    updated.setReceived(dto.received());

            Notification saved = repository.save(updated);

            return AsyncResult.success(repository.save(saved));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Уведомление не найдено");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }


    private Notification notificationFromDto(NotificationCreateDto dto, LessonNote lessonNote) {
        Notification notification = new Notification();
        notification.setLessonNote(lessonNote);
        notification.setMessage(dto.message());
        notification.setReceived(dto.received());
        notification.setSendDate(dto.sendDate());
        return notification;
    }
}
