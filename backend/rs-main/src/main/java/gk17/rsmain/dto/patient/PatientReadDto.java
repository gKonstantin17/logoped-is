package gk17.rsmain.dto.patient;

import java.sql.Timestamp;
import java.util.UUID;

public record PatientReadDto(
        Long id,
        String firstName,
        String lastName,
        Timestamp dateOfBirth,
        UUID userId,
        UUID logopedId,
        boolean isHidden
){}
