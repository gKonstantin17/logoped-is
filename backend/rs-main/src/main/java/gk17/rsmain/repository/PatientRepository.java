package gk17.rsmain.repository;

import gk17.rsmain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PatientRepository  extends JpaRepository<Patient,Long> {
    List<Patient> findByUserId(UUID id);

    List<Patient> findByLogopedId(UUID id);

    // есть ли у пациента речевая карта
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Patient p " +
            "JOIN p.lessons l " +
            "JOIN Diagnostic d ON d.lesson = l " +
            "WHERE p.id = :patientId AND d.speechCard IS NOT NULL")
    boolean existsSpeechCardByPatientId(@Param("patientId") Long patientId);

//    @Query("""
//    SELECT DISTINCT p FROM Patient p
//    JOIN FETCH p.lessons l
//    WHERE p.logoped.id = :logopedId
//      AND EXISTS (
//        SELECT d FROM Diagnostic d
//        WHERE d.lesson = l
//          AND d.speechCard IS NOT NULL
//      )
//""")
//    List<Patient> findByLogopedIdWithSpeechCard(@Param("logopedId") UUID logopedId);

    @Query("""
    SELECT DISTINCT p FROM Patient p
    LEFT JOIN FETCH p.lessons l
    LEFT JOIN FETCH l.diagnostic d
    LEFT JOIN FETCH d.speechCard sc
    LEFT JOIN FETCH sc.speechErrors se
    LEFT JOIN FETCH sc.soundCorrections corr
    WHERE p.user.id = :userId
""")
    List<Patient> findAllWithSpeechData(@Param("userId") UUID userId);




}
