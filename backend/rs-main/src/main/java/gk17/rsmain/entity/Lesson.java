package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Lesson", schema = "logoped", catalog = "Logoped")
public class Lesson {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Type")
    private String type;

    @Column(name = "Topic")
    private String topic;

    @Column(name = "Description")
    private String description;

    @Column(name = "DateOfLesson")
    private Timestamp dateOfLesson;
    @Column(name = "Status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LogopedId")
    private Logoped logoped;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HomeworkId")
    private Homework homework;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Lesson_Patient",
            schema = "logoped",
            joinColumns = @JoinColumn(name = "LessonId"),
            inverseJoinColumns = @JoinColumn(name = "PatientId"))
    private Set<Patient> patients = new HashSet<>();
}
