package logopedis.rsmain.entity;

import jakarta.persistence.*;
import logopedis.rsmain.entity.SoundCorrection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SpeechCard", schema = "logoped", catalog = "Logoped")
public class SpeechCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Reason")
    private String reason;

    @Column(name = "StateOfHearning")
    private String stateOfHearning;

    @Column(name = "Anamnesis")
    private String anamnesis;

    @Column(name = "GeneralMotor")
    private String generalMotor;

    @Column(name = "FineMotor")
    private String fineMotor;

    @Column(name = "Articulatory")
    private String articulatory;

    @Column(name = "SoundReproduction")
    private String soundReproduction;

    @Column(name = "SoundComponition")
    private String soundComponition;

    @Column(name = "SpeechChars")
    private String speechChars;

    @Column(name = "PatientChars")
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
