package logopedis.libentities.msnotification.dto.recipient;

import logopedis.libentities.msnotification.entity.LessonNote;

import java.util.UUID;

public record RecipientCreateDto(
        LessonNote lessonNote,
        Long patientId,
        UUID userId
) {
}
