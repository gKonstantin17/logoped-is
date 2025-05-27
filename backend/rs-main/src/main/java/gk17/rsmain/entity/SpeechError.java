package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SpeechError", schema = "logoped", catalog = "Logoped")
public class SpeechError {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Title", nullable = true, length = -1)
    private String title;
    @Basic
    @Column(name = "Description", nullable = true, length = -1)
    private String description;
}
