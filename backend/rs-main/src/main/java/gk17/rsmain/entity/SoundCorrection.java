package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SoundCorrection", schema = "logoped", catalog = "Logoped")
public class SoundCorrection {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Sound", nullable = true, length = -1)
    private String sound;

    @Column(name = "Correction", nullable = true, length = -1)
    private String correction;

    @ManyToMany(mappedBy = "soundCorrections", fetch = FetchType.LAZY)
    private List<SpeechCard> speechCards = new ArrayList<>();
}
