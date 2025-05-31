package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
@Data
@NoArgsConstructor
@Entity
@Table(name = "Homework", schema = "logoped", catalog = "Logoped")
public class Homework {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Task", nullable = true, length = -1)
    private String task;
}
