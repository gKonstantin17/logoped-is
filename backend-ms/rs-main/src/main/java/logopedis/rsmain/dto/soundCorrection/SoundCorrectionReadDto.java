package logopedis.rsmain.dto.soundCorrection;

public record SoundCorrectionReadDto(
        Long id,
        String sound,
        String correction)
{}
