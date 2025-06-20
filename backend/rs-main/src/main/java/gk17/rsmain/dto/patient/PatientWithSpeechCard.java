package gk17.rsmain.dto.patient;

import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.dto.speechCard.SpeechCardReadDto;
import gk17.rsmain.dto.speechError.SpeechErrorDto;
import gk17.rsmain.dto.speechError.SpeechErrorReadDto;

import java.sql.Timestamp;
import java.util.List;

public record PatientWithSpeechCard(
        Long id,
        String firstName,
        String lastName,
        Timestamp dateOfBirth,
        List<SpeechErrorDto> speechErrors,
        List<SoundCorrectionDto> soundCorrections
){}