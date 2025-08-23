package logopedis.rsmain.entity;

import jakarta.persistence.*;
import logopedis.rsmain.entity.Lesson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Patient", schema = "logoped", catalog = "Logoped")
public class Patient {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "DateOfBirth")
    private Timestamp dateOfBirth;

    @Column(name = "IsHidden")
    private boolean isHidden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private UserData user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LogopedId")
    private Logoped logoped;

    @ManyToMany(mappedBy = "patients", fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();
}
