package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;
@Data
@NoArgsConstructor
@Entity
@Table(name = "Lesson", schema = "logoped", catalog = "Logoped")
public class Lesson {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Type", nullable = true, length = -1)
    private String type;
    @Basic
    @Column(name = "Topic", nullable = true, length = -1)
    private String topic;
    @Basic
    @Column(name = "Description", nullable = true, length = -1)
    private String description;
    @Basic
    @Column(name = "DateOfLesson", nullable = true)
    private Timestamp dateOfLesson;
    @Basic
    @Column(name = "LogopedId", nullable = true)
    private Long logopedId;
    @Basic
    @Column(name = "HomeworkId", nullable = true)
    private Long homeworkId;
}
