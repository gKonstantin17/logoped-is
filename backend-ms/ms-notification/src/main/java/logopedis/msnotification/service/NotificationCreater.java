package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.enums.NotificationMsg;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Notification;
import logopedis.libentities.msnotification.entity.Recipient;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class NotificationCreater {

    private final List<LessonStatus> noiceStatuses = List.of(
            LessonStatus.PLANNED,
            LessonStatus.PLANNED_1H,
            LessonStatus.CANCELED_BY_CLIENT,
            LessonStatus.CANCELED_BY_LOGOPED,
            LessonStatus.STARTING_SOON,
            LessonStatus.NO_SHOW_CLIENT,
            LessonStatus.NO_SHOW_LOGOPED
    );

    public Notification createNotification(LessonNote lessonNote, UUID recipientId, List<Long> patientsId) {
        Notification n = new Notification();
        n.setLessonNote(lessonNote);
        n.setSendDate(Timestamp.valueOf(LocalDateTime.now()));
        n.setMessage(setMessageFromStatus(lessonNote));
        n.setReceived(setReceivedFromStatus(lessonNote));
        n.setRecipientId(recipientId);
        n.setPatientsId(patientsId);
        return n;
    }

    public List<Notification> createNotifications(LessonNote lessonNote, List<Recipient> recipients) {
        List<Notification> notifications = new ArrayList<>();

        // Логопед получает одно уведомление о всех пациентах
        if (lessonNote.getLogopedId() != null) {
            List<Long> allPatientIds = recipients.stream()
                    .map(Recipient::getPatientId)
                    .filter(Objects::nonNull)
                    .toList();
            notifications.add(createNotification(lessonNote, lessonNote.getLogopedId(), allPatientIds));
        }

        // Каждый пациент получает отдельное уведомление
        for (Recipient recipient : recipients) {
            notifications.add(createNotification(lessonNote,
                    recipient.getUserId(),
                    List.of(recipient.getPatientId()) // один пациент в списке
            ));
        }

        return notifications;
    }

    private String setMessageFromStatus(LessonNote lessonNote) {
        // из enum LessonStatus находит соответствующий enum NotificationMsg
        return NotificationMsg.valueOf(lessonNote.getStatus().name()).getDescription();
    }

    private boolean setReceivedFromStatus(LessonNote lessonNote) {
        return !noiceStatuses.contains(lessonNote.getStatus());
    }

    public List<Notification> createWithCustomMessage(LessonNote lessonNote, List<Recipient> recipients, NotificationMsg msg) {
        List<Notification> notifications = createNotifications(lessonNote,recipients);
        notifications.forEach(notification -> {
            notification.setMessage(msg.getDescription());
            notification.setReceived(false);
        });
        return notifications;
    }

}
