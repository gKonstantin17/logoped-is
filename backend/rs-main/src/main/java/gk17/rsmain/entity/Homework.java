package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Homework", schema = "logoped", catalog = "Logoped")
public class Homework {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Task")
    private String task;
}
