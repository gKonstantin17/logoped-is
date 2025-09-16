package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.libentities.rsmain.entity.SpeechCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface DiagnosticRepository extends JpaRepository<Diagnostic,Long> {
    @EntityGraph(attributePaths = {
            "speechCard",
            "lesson.logoped",
            "lesson.patients"
    })
    Optional<Diagnostic> findBySpeechCard(SpeechCard card);

    Optional<Diagnostic> findByLessonId(Long lessonId);
    @Query(""" 
    SELECT d FROM Diagnostic d 
    JOIN FETCH d.speechCard sc 
    LEFT JOIN FETCH sc.speechErrors 
    LEFT JOIN FETCH sc.soundCorrections 
    JOIN d.lesson l 
    JOIN l.patients p 
    WHERE p.id = :patientId 
    AND d.date = ( SELECT MAX(d2.date)
                    FROM Diagnostic d2 
                    JOIN d2.lesson l2 
                    JOIN l2.patients 
                    p2 WHERE p2.id = :patientId ) 
          """)
    Optional<Diagnostic> findLatestDiagnosticByPatientId(@Param("patientId") Long patientId);

    @Query("""
    SELECT DISTINCT d
    FROM Diagnostic d
    JOIN d.lesson l
    JOIN l.patients p
    LEFT JOIN FETCH d.speechCard sc
    LEFT JOIN FETCH sc.speechErrors
    LEFT JOIN FETCH sc.soundCorrections
    WHERE p.id = :patientId
    ORDER BY d.date ASC
""")
    List<Diagnostic> findAllByPatientIdWithSpeechCard(@Param("patientId") Long patientId);

    @Query("""
    SELECT d
    FROM Diagnostic d
    JOIN d.lesson l
    JOIN l.patients p
    WHERE p.id = :patientId
      AND d.date < :currentDate
    ORDER BY d.date DESC
""")
    List<Diagnostic> findPreviousByPatientIdAndDate(
            @Param("patientId") Long patientId,
            @Param("currentDate") Timestamp currentDate
    );

    @Query("""
    SELECT d
    FROM Diagnostic d
    JOIN FETCH d.speechCard sc
    LEFT JOIN FETCH sc.speechErrors
    LEFT JOIN FETCH sc.soundCorrections
    JOIN d.lesson l
    JOIN l.patients p
    WHERE p.id = :patientId
      AND d.date = (
          SELECT MIN(d2.date)
          FROM Diagnostic d2
          JOIN d2.lesson l2
          JOIN l2.patients p2
          WHERE p2.id = :patientId
      )
""")
    Optional<Diagnostic> findEarliestDiagnosticByPatientId(@Param("patientId") Long patientId);

}
