package logopedis.rsmain.repository;

import logopedis.rsmain.entity.Logoped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogopedRepository extends JpaRepository<Logoped, UUID> {
}
