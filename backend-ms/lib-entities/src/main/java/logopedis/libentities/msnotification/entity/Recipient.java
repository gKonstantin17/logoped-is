package logopedis.libentities.msnotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Recipient", schema = "notification", catalog = "Logoped")
public class Recipient {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "PatientId", nullable = true)
    private Long patientId;

    @Column(name = "UserId", nullable = true)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonNoteId")
    private LessonNote lessonNote;
}
