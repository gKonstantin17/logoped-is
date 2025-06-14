package gk17.rsmain.repository;

import gk17.rsmain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository  extends JpaRepository<Patient,Long> {
    List<Patient> findByUserId(Long id);

    List<Patient> findByLogopedId(Long id);

    // есть ли у пациента речевая карта
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Patient p " +
            "JOIN p.lessons l " +
            "JOIN Diagnostic d ON d.lesson = l " +
            "WHERE p.id = :patientId AND d.speechCard IS NOT NULL")
    boolean existsSpeechCardByPatientId(@Param("patientId") Long patientId);

}
