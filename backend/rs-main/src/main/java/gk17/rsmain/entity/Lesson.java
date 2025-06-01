package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Lesson", schema = "logoped", catalog = "Logoped")
public class Lesson {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Type", nullable = true, length = -1)
    private String type;

    @Column(name = "Topic", nullable = true, length = -1)
    private String topic;

    @Column(name = "Description", nullable = true, length = -1)
    private String description;

    @Column(name = "DateOfLesson", nullable = true)
    private Timestamp dateOfLesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LogopedId", nullable = true)
    private Logoped logoped;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HomeworkId", nullable = true)
    private Homework homework;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Lesson_Patient",
            schema = "logoped",
            joinColumns = @JoinColumn(name = "LessonId"),
            inverseJoinColumns = @JoinColumn(name = "PatientId"))
    private Set<Patient> patients = new HashSet<>();
}
