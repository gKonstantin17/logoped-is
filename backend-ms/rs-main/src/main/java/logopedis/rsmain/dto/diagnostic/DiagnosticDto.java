package logopedis.rsmain.dto.diagnostic;

import java.sql.Timestamp;

public record DiagnosticDto (
        Timestamp date,
        Long lessonId,
        Long speechCardId) { }
