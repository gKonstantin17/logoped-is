package logopedis.libentities.msnotification.entity;

import jakarta.persistence.*;
import logopedis.libentities.enums.LessonStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "LogopedId")
    private UUID logopedId;

    @Column(name = "PatientsId", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<Long> patientsId;
}
