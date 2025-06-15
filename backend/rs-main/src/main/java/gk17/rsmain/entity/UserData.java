package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "UserData", schema = "logoped", catalog = "Logoped")
public class UserData {
    @Id
    @Column(name = "Id", nullable = false)
    private UUID id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone")
    private String phone;
}
