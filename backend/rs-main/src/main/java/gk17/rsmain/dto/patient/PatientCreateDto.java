package gk17.rsmain.dto.patient;

import java.sql.Timestamp;

public record PatientCreateDto(
        String firstName,
        String secondName,
        Timestamp dateOfBirth,
        Long userId
){}
