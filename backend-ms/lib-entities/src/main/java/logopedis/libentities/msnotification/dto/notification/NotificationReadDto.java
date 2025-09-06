package logopedis.libentities.msnotification.dto.notification;

import java.sql.Timestamp;
import java.util.UUID;

public record NotificationReadDto(
        Long id,
        Long lessonNoteId,
        Timestamp sendDate,
        String message,
        Boolean received,
        UUID recipientId
) {
}
