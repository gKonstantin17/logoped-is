package gk17.rsmain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Diagnostic", schema = "logoped", catalog = "Logoped")
public class Diagnostic {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "Date", nullable = true)
    private Timestamp date;
    @Basic
    @Column(name = "patientId", nullable = false)
    private Long patientId;
    @Basic
    @Column(name = "SpeechCardId", nullable = false)
    private Long speechCardId;
    @Basic
    @Column(name = "LogopedId", nullable = false)
    private Long logopedId;
}
