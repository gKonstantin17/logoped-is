package logopedis.libentities.msnotification.dto.notification;

import java.sql.Timestamp;


public record NotificationUpdateDto(
        Timestamp sendDate,
        String message,
        Boolean received
) {}