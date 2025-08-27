package logopedis.libentities.msnotification.dto.lessonNote;

import logopedis.libentities.enums.LessonStatus;

import java.sql.Timestamp;

public record LessonNoteChangeDto(
        LessonStatus status,
        Timestamp startTime
) {}