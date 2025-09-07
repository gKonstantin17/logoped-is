package logopedis.libentities.msnotification.dto.notification;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


public record NotificationUpdateDto(
        Long lessonNoteId,
        Timestamp sendDate,
        String message,
        Boolean received,
        UUID recipientId,
        List<Long> patientsId
) {}