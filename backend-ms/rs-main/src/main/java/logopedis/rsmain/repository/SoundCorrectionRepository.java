package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.SoundCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface SoundCorrectionRepository extends JpaRepository<SoundCorrection,Long> {
    Optional<SoundCorrection> findBySoundAndCorrection(String sound, String correction);

    @Query("""
    SELECT sc.soundCorrections
    FROM Diagnostic d
    JOIN d.speechCard sc
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
    Optional<Set<SoundCorrection>> findLatestSoundCorrectionsByPatientId(@Param("patientId") Long patientId);

}
