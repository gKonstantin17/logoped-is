package logopedis.libentities.rsmain.dto.speechCard;

import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;

import java.util.List;

public record SpeechCardCorrectionDto(
        Long patientId,
        List<SoundCorrectionDto> updatedCorrections,
        Long lessonId
) {
}
