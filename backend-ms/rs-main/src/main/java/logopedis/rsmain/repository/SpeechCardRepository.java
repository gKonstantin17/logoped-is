package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.SpeechCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SpeechCardRepository extends JpaRepository<SpeechCard,Long> {
    @NonNull
    @EntityGraph(attributePaths = {"speechErrors", "soundCorrections"})
    List<SpeechCard> findAll();

    @NonNull
    @EntityGraph(attributePaths = {"speechErrors", "soundCorrections"})
    Optional<SpeechCard> findById(@NonNull Long id);

    @Query("""
    SELECT sc
    FROM Diagnostic d
    JOIN d.speechCard sc
    LEFT JOIN FETCH sc.soundCorrections
    LEFT JOIN FETCH sc.speechErrors
    JOIN d.lesson l
    JOIN l.patients p
    WHERE p.id = :patientId
    AND d.date = (
        SELECT MAX(d2.date)
        FROM Diagnostic d2
        JOIN d2.lesson l2
        JOIN l2.patients p2
        WHERE p2.id = :patientId
    )
""")
    Optional<SpeechCard> findLatestSpeechCardByPatientId(@Param("patientId") Long patientId);


    @Query("""
    SELECT sc
    FROM Diagnostic d
    JOIN d.speechCard sc
    LEFT JOIN FETCH sc.soundCorrections
    LEFT JOIN FETCH sc.speechErrors
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
    Optional<SpeechCard> findEarliestSpeechCardByPatientId(@Param("patientId") Long patientId);

}
