package logopedis.libentities.kafka;

import logopedis.libentities.enums.LessonStatus;

public record LessonStatusDto (
        Long id,
        LessonStatus status
) {}
