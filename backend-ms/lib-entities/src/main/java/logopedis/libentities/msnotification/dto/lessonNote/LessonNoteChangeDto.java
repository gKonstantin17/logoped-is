package logopedis.libentities.msnotification.dto.lessonNote;

import java.sql.Timestamp;

public record LessonNoteChangeDto(
        String status,
        Timestamp startTime
) {}