package gk17.rsmain.repository;

import gk17.rsmain.entity.SoundCorrection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoundCorrectionRepository extends JpaRepository<SoundCorrection,Long> {
    Optional<SoundCorrection> findBySoundAndCorrection(String sound, String correction);

}
