package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Patient", schema = "logoped", catalog = "Logoped")
public class Patient {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "FirstName", nullable = true, length = -1)
    private String firstName;
    @Basic
    @Column(name = "SecondName", nullable = true, length = -1)
    private String secondName;
    @Basic
    @Column(name = "DateOfBirth", nullable = true)
    private Timestamp dateOfBirth;
    @Basic
    @Column(name = "UserId", nullable = false)
    private Long userId;
}
