package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "UserData", schema = "logoped", catalog = "Logoped")
public class UserData {
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
    @Column(name = "Email", nullable = true, length = -1)
    private String email;
    @Basic
    @Column(name = "Phone", nullable = true, length = -1)
    private String phone;
}
