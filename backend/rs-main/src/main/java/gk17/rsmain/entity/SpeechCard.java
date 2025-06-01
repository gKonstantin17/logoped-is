package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SpeechCard", schema = "logoped", catalog = "Logoped")
public class SpeechCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Reason", nullable = true, length = -1)
    private String reason;

    @Column(name = "StateOfHearning", nullable = true, length = -1)
    private String stateOfHearning;

    @Column(name = "Anamnesis", nullable = true, length = -1)
    private String anamnesis;

    @Column(name = "GeneralMotor", nullable = true, length = -1)
    private String generalMotor;

    @Column(name = "FineMotor", nullable = true, length = -1)
    private String fineMotor;

    @Column(name = "Articulatory", nullable = true, length = -1)
    private String articulatory;

    @Column(name = "SoundReproduction", nullable = true, length = -1)
    private String soundReproduction;

    @Column(name = "SoundComponition", nullable = true, length = -1)
    private String soundComponition;

    @Column(name = "SpeechChars", nullable = true, length = -1)
    private String speechChars;

    @Column(name = "PatientChars", nullable = true, length = -1)
    private String patientChars;

    @ManyToMany
    @JoinTable(
            name = "SpeechCard_SpeechError",
            schema = "logoped",
            joinColumns = @JoinColumn(name = "SpeechCardId"),
            inverseJoinColumns = @JoinColumn(name = "SpeechErrorId")
    )
    private Set<SpeechError> speechErrors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "SpeechCard_SoundCorrection",
            schema = "logoped",
            joinColumns = @JoinColumn(name = "SpeechCardId"),
            inverseJoinColumns = @JoinColumn(name = "SpeechCorrectionId")
    )
    private Set<SoundCorrection> soundCorrections = new HashSet<>();
}
