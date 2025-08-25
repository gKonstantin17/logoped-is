package logopedis.libentities.msnotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

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
    @JoinColumn(name = "LessonId")
    private LessonNote lessonNote;
    @Basic
    @Column(name = "SendDate", nullable = true)
    private Timestamp sendDate;
    @Basic
    @Column(name = "Message", nullable = true, length = -1)
    private String message;
    @Basic
    @Column(name = "Received", nullable = true)
    private Boolean received;
}
