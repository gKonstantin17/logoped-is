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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final NotificationCreater notificationCreater;
    private final RecipientService recipientService;
    private static final RestTemplate restTemplate = new RestTemplate();

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    public NotificationService(NotificationRepository repository,  NotificationCreater notificationCreater, RecipientService recipientService) {
        this.repository = repository;
        this.notificationCreater = notificationCreater;
        this.recipientService = recipientService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<NotificationReadDto>>> findall() {
        var data = repository.findAll();
        List<NotificationReadDto> result =  data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<List<NotificationReadDto>>> findByUser(UUID userId) {
        var data = repository.findByRecipientId(userId);
        List<NotificationReadDto> result =  data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    private NotificationReadDto toReadDto(Notification n) {
        return new NotificationReadDto(n.getId(),
                n.getLessonNote().getId(),
                n.getSendDate(),
                n.getMessage(),
                n.getReceived(),
                n.getRecipientId(),
                n.getPatientsId());
    }
    public Notification findById(Long id) {
        return repository.findById(id).get();
    }


    public CompletableFuture<ServiceResult<List<Notification>>> createFromLessonNote(LessonNote lessonNote) {
        try {
            List<Recipient> recipients = recipientService.findByLessonNote(lessonNote);
            List<Notification> notifications = notificationCreater.createNotifications(lessonNote,recipients);

            List<Notification> results = repository.saveAll(notifications);
            for (Notification n: results) {
                sendNotification(n);
                log.info("Создано уведомление: "+String.valueOf(n));
            }
            return AsyncResult.success(results);
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
            if (dto.recipientId() != null)    updated.setRecipientId(dto.recipientId());
            if (dto.patientsId() != null)    updated.setPatientsId(dto.patientsId());

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

    private void sendNotification(Notification notification) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Оборачиваем тело в HttpEntity
        HttpEntity<Notification> entity = new HttpEntity<>(notification, headers);

        // Отправляем POST-запрос
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8380/notification/send-to-client",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode()== HttpStatus.OK) {
            System.out.println("ОКей");
        }
    }

    @Async
    public CompletableFuture<ServiceResult<NotificationReadDto>> receive(Long id) {
        try {
            Notification n = findById(id);
            n.setReceived(true);
            var changed = repository.save(n);
            var result = toReadDto(changed);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
}
