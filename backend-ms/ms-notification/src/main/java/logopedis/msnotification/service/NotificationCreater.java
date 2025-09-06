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
import java.util.UUID;

@Service
public class NotificationCreater {

    private final List<LessonStatus> noiceStatuses = List.of(
            LessonStatus.PLANNED,
            LessonStatus.CANCELED_BY_CLIENT,
            LessonStatus.CANCELED_BY_LOGOPED,
            LessonStatus.STARTING_SOON,
            LessonStatus.NO_SHOW_CLIENT,
            LessonStatus.NO_SHOW_LOGOPED
    );

    public Notification createNotification(LessonNote lessonNote, UUID recipientId, Long patientId) {
        Notification n = new Notification();
        n.setLessonNote(lessonNote);
        n.setSendDate(Timestamp.valueOf(LocalDateTime.now()));
        n.setMessage(setMessageFromStatus(lessonNote));
        n.setReceived(setReceivedFromStatus(lessonNote));
        n.setRecipientId(recipientId);
        n.setPatientId(patientId);
        return n;
    }

    public List<Notification> createNotifications(LessonNote lessonNote, List<Recipient> recipients) {
        List<Notification> notifications = new ArrayList<>();

        // Логопед получает одно уведомление
        if (lessonNote.getLogopedId() != null)
            notifications.add(createNotification(lessonNote, lessonNote.getLogopedId(), null));

        for (Recipient recipient : recipients)
            notifications.add(createNotification(lessonNote, recipient.getUserId(), recipient.getPatientId()));
        return notifications;
    }

    private String setMessageFromStatus(LessonNote lessonNote) {
        String msg = "";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        long minutesToStart = (lessonNote.getStartTime().getTime() - now.getTime()) / 60000;
        switch (lessonNote.getStatus()) {
            case PLANNED -> {
                if (minutesToStart <= 60 && minutesToStart > 15)
                    msg = NotificationMsg.STARTING_SOON_1H.getDescription();
                else if (minutesToStart <= 15)
                    msg = NotificationMsg.STARTING_SOON_15М.getDescription();
                else
                    msg = NotificationMsg.PLANNED.getDescription();
            }
            case COMPLETED ->   msg = NotificationMsg.COMPLETED.getDescription();
            case IN_PROGRESS -> msg = NotificationMsg.IN_PROGRESS.getDescription();
            case CANCELED_BY_CLIENT -> msg = NotificationMsg.CANCELED_BY_CLIENT.getDescription();
            case CANCELED_BY_LOGOPED -> msg = NotificationMsg.CANCELED_BY_LOGOPED.getDescription();
            case STARTING_SOON -> msg = NotificationMsg.STARTING_SOON_15М.getDescription();
            case NO_SHOW_CLIENT -> msg = NotificationMsg.NO_SHOW_CLIENT.getDescription();
            case NO_SHOW_LOGOPED -> msg = NotificationMsg.NO_SHOW_LOGOPED.getDescription();
        }
        return msg;
//          или
//        return NotificationMsg.valueOf(lessonNote.getStatus().name())
//                .getDescription();
    }

    private boolean setReceivedFromStatus(LessonNote lessonNote) {
        return noiceStatuses.contains(lessonNote.getStatus());
    }
}
