package gk17.rsmain.dto.patient;

import java.sql.Timestamp;

public record PatientDto(
        String firstName,
        String secondName,
        Timestamp dateOfBirth,
        Long userId,
        Long logopedId
){}
