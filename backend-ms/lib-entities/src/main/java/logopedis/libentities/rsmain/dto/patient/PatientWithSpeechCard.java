package logopedis.libentities.rsmain.dto.patient;

import logopedis.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.rsmain.dto.speechError.SpeechErrorDto;

import java.sql.Timestamp;
import java.util.List;

public record PatientWithSpeechCard(
        Long id,
        String firstName,
        String lastName,
        Timestamp dateOfBirth,
        boolean isHidden,
        List<SpeechErrorDto> speechErrors,
        List<SoundCorrectionDto> soundCorrections
){}