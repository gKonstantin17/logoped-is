package gk17.rsmain.dto.speechCard;

import java.util.List;

public record SpeechCardDto(
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
        List<Long> speechErrors,
        List<Long> soundCorrections
) {}
