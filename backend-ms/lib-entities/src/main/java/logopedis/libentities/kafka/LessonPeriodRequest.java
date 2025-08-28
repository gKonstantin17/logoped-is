package logopedis.libentities.kafka;

import java.sql.Timestamp;

public record LessonPeriodRequest(
        Timestamp periodStart,
        Timestamp periodEnd
) {}
