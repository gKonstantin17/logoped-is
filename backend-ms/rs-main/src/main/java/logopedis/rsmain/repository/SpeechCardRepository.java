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
    SELECT sc FROM SpeechCard sc
    JOIN FETCH sc.speechErrors
    JOIN FETCH sc.soundCorrections
    WHERE sc IN (
        SELECT d.speechCard
        FROM Diagnostic d
        JOIN d.lesson l
        JOIN l.patients patient
        WHERE patient.id = :patientId
        ORDER BY d.date DESC
        )
    """)
    Optional<SpeechCard> findDetailedByPatientId(@Param("patientId") Long patientId);

}
