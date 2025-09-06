package logopedis.libentities.kafka;

import java.util.List;

public record LessonsForPeriodDto(
        List<LessonNoteWithRecipientDto> list
) {
}
