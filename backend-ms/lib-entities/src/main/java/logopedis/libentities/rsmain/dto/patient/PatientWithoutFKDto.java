package logopedis.libentities.rsmain.dto.patient;

import java.sql.Timestamp;

public record PatientWithoutFKDto(
        Long id,
        String firstName,
        String lastName,
        Timestamp dateOfBirth
){}