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
@Table(name = "LessonNote", schema = "notification", catalog = "Logoped")
public class LessonNote {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Status", nullable = true, length = -1)
    private String status;
    @Basic
    @Column(name = "StartTime", nullable = true)
    private Timestamp startTime;
}
