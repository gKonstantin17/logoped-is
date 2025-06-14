package gk17.rsmain.dto.patient;

import java.sql.Timestamp;

public record PatientWithoutFK(
        String firstName,
        String secondName,
        Timestamp dateOfBirth
){}