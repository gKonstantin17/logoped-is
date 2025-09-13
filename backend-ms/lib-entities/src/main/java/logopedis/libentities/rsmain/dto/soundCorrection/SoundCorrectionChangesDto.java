package logopedis.libentities.rsmain.dto.soundCorrection;

import java.util.Set;

public record SoundCorrectionChangesDto(
        Set<SoundCorrectionReadDto> added,
        Set<SoundCorrectionReadDto> removed
) {
}
