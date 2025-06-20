package gk17.rsmain.repository;

import gk17.rsmain.entity.Logoped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogopedRepository extends JpaRepository<Logoped, UUID> {
}
