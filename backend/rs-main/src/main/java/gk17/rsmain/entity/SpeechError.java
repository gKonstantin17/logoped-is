package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
@Table(name = "SpeechError", schema = "logoped", catalog = "Logoped")
public class SpeechError {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Title", nullable = true, length = -1)
    private String title;

    @Column(name = "Description", nullable = true, length = -1)
    private String description;

    @ManyToMany(mappedBy = "speechErrors", fetch = FetchType.LAZY)
    private List<SpeechCard> speechCards = new ArrayList<>();
}
