package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.notification.NotificationCreateDto;
import logopedis.libentities.msnotification.dto.notification.NotificationReadDto;
import logopedis.libentities.msnotification.dto.notification.NotificationUpdateDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Notification;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.msnotification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final LessonNoteService lessonNoteService;
    private final NotificationCreater notificationCreater;
    private final RecipientService recipientService;
    public NotificationService(NotificationRepository repository, LessonNoteService lessonNoteService, NotificationCreater notificationCreater, RecipientService recipientService) {
        this.repository = repository;
        this.lessonNoteService = lessonNoteService;
        this.notificationCreater = notificationCreater;
        this.recipientService = recipientService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<NotificationReadDto>>> findall() {
        var data = repository.findAll();
        List<NotificationReadDto> result =  data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    private NotificationReadDto toReadDto(Notification n) {
        return new NotificationReadDto(n.getId(),
                n.getLessonNote().getId(),
                n.getSendDate(),
                n.getMessage(),
                n.getReceived(),
                n.getRecipientId());
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

    public CompletableFuture<ServiceResult<List<Notification>>> createFromLessonNote(LessonNote lessonNote) {
        try {
            List<Recipient> recipients = recipientService.findByLessonNote(lessonNote);
            List<Notification> notifications = notificationCreater.createNotifications(lessonNote,recipients);

            return AsyncResult.success(notifications);
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
