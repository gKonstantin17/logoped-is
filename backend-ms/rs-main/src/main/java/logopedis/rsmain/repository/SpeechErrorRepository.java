package logopedis.rsmain.repository;

import logopedis.rsmain.entity.SpeechError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeechErrorRepository extends JpaRepository<SpeechError,Long> {
}
