package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SoundCorrection", schema = "logoped", catalog = "Logoped")
public class SoundCorrection {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Sound", nullable = true, length = -1)
    private String sound;
    @Basic
    @Column(name = "Correction", nullable = true, length = -1)
    private String correction;
}
