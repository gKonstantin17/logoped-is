package logopedis.libentities.rsmain.dto.patient;

import java.sql.Timestamp;

public record PatientWithoutFK(
        String firstName,
        String lastName,
        Timestamp dateOfBirth
){}