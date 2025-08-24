package logopedis.libentities.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SoundCorrection", schema = "logoped", catalog = "Logoped")
public class SoundCorrection {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Sound")
    private String sound;

    @Column(name = "Correction")
    private String correction;

    @ManyToMany(mappedBy = "soundCorrections", fetch = FetchType.LAZY)
    private List<SpeechCard> speechCards = new ArrayList<>();
}
