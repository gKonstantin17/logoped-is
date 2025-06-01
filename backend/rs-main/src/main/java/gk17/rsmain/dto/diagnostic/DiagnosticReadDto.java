package gk17.rsmain.dto.diagnostic;

import java.sql.Timestamp;

public record DiagnosticReadDto(
        Long id,
        Timestamp date,
        Long lessonId,
        Long speechCardId) { }
