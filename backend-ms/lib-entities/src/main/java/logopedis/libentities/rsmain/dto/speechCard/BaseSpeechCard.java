package logopedis.libentities.rsmain.dto.speechCard;

import java.util.List;

public interface BaseSpeechCard {
    String reason();
    String stateOfHearning();
    String anamnesis();
    String generalMotor();
    String fineMotor();
    String articulatory();
    String soundReproduction();
    String soundComponition();
    String speechChars();
    String patientChars();
    List<Long> speechErrors();
}
