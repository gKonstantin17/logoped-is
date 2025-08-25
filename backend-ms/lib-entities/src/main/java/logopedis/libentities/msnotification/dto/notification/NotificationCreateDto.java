package logopedis.libentities.msnotification.dto.notification;

import java.sql.Timestamp;


public record NotificationCreateDto(
        Long lessonNoteId,
        Timestamp sendDate,
        String message,
        Boolean received
) {}