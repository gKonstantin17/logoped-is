package logopedis.libentities.msnotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Notification", schema = "notification", catalog = "Logoped")
public class Notification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonNoteId")
    private LessonNote lessonNote;

    @Column(name = "SendDate")
    private Timestamp sendDate;

    @Column(name = "Message")
    private String message;

    @Column(name = "Received")
    private Boolean received;

    @Column(name = "RecipientId")
    private UUID recipientId;

    @Column(name = "PatientsId", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<Long> patientsId;
}
