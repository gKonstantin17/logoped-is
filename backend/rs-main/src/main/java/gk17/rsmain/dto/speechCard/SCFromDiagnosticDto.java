package gk17.rsmain.dto.speechCard;

import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;

import java.util.List;
import java.util.UUID;

public record SCFromDiagnosticDto(
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
        List<SoundCorrectionDto> soundCorrections,
        Long lessonId,
        UUID logopedId
) {}
