package logopedis.libentities.kafka;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.dto.recipient.RecipientDataDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record LessonNoteWithRecipientDto(
        Long id,
        LessonStatus status,
        Timestamp startTime,
        UUID logopedId,
        List<RecipientDataDto> recipientDtos
) {}