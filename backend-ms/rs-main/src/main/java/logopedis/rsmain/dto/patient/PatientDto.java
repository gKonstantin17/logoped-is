package logopedis.rsmain.dto.patient;

import java.sql.Timestamp;
import java.util.UUID;

public record PatientDto(
        String firstName,
        String lastName,
        Timestamp dateOfBirth,
        UUID userId,
        UUID logopedId
){}
