package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "UserData", schema = "logoped", catalog = "Logoped")
public class UserData {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "SecondName")
    private String secondName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone")
    private String phone;
}
