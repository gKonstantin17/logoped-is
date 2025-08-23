package logopedis.rsmain.dto.speechCard;

import java.sql.Timestamp;
import java.util.List;

public record SpeechCardFullDto (
        Long id,
        String reason,
        String stateOfHearning,
        String anamnesis,
        String generalMotor,
        String fineMotor,
        String articulatory,
        String soundReproduction,
        String soundComponition,
        String speechChars,
        String patientChars,
        List<String> speechErrors,
        List<String> soundCorrections,
        Timestamp diagnosticDate,
        String logopedFirstName,
        String logopedLastName,
        String patientFirstName,
        String patientLastName,
        Timestamp patientDateOfBirth
) {}
