package logopedis.libentities.rsmain.dto.patient;

import java.sql.Timestamp;
import java.util.UUID;

public record PatientCreateDto(
        String firstName,
        String lastName,
        Timestamp dateOfBirth,
        UUID userId
){}
