package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SpeechCard", schema = "logoped", catalog = "Logoped")
public class SpeechCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Reason", nullable = true, length = -1)
    private String reason;
    @Basic
    @Column(name = "StateOfHearning", nullable = true, length = -1)
    private String stateOfHearning;
    @Basic
    @Column(name = "Anamnesis", nullable = true, length = -1)
    private String anamnesis;
    @Basic
    @Column(name = "GeneralMotor", nullable = true, length = -1)
    private String generalMotor;
    @Basic
    @Column(name = "FineMotor", nullable = true, length = -1)
    private String fineMotor;
    @Basic
    @Column(name = "Articulatory", nullable = true, length = -1)
    private String articulatory;
    @Basic
    @Column(name = "SoundReproduction", nullable = true, length = -1)
    private String soundReproduction;
    @Basic
    @Column(name = "SoundComponition", nullable = true, length = -1)
    private String soundComponition;
    @Basic
    @Column(name = "SpeechChars", nullable = true, length = -1)
    private String speechChars;
    @Basic
    @Column(name = "PatientChars", nullable = true, length = -1)
    private String patientChars;
}
