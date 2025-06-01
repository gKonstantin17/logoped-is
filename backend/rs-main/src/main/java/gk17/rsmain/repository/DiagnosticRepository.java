package gk17.rsmain.repository;

import gk17.rsmain.entity.Diagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticRepository extends JpaRepository<Diagnostic,Long> {
}
