package logopedis.libentities.msnotification.entity;

import jakarta.persistence.*;
import logopedis.libentities.enums.LessonStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "LessonNote", schema = "notification", catalog = "Logoped")
public class LessonNote {
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private LessonStatus status;
    @Column(name = "StartTime")
    private Timestamp startTime;
}
