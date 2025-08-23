package logopedis.rsmain.dto.speechCard;

import logopedis.rsmain.dto.soundCorrection.SoundCorrectionDto;

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
) implements BaseSpeechCard {}
