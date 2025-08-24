package logopedis.libentities.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Diagnostic", schema = "logoped", catalog = "Logoped")
public class Diagnostic {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Date")
    private Timestamp date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonId")
    private Lesson lesson;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpeechCardId")
    private SpeechCard speechCard;
}
