package logopedis.libentities.rsmain.dto.lesson;

import logopedis.libentities.rsmain.dto.homework.HomeworkDto;

import java.util.List;

public record LessonChangeDto(
        Long id,
        String type,
        String topic,
        String description,
        List<Long> patients,
        HomeworkDto homework
) {
}
