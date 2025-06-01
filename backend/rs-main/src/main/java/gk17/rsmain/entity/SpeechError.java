package gk17.rsmain.entity;

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
@Table(name = "SpeechError", schema = "logoped", catalog = "Logoped")
public class SpeechError {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @ManyToMany(mappedBy = "speechErrors", fetch = FetchType.LAZY)
    private List<SpeechCard> speechCards = new ArrayList<>();
}
