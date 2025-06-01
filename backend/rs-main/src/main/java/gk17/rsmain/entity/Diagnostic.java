package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Diagnostic", schema = "logoped", catalog = "Logoped")
public class Diagnostic {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Date", nullable = true)
    private Timestamp date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonId", nullable = true)
    private Lesson lesson;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpeechCardId", nullable = true)
    private SpeechCard speechCard;
}
