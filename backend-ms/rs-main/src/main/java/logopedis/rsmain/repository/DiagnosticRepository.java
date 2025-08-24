package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.libentities.rsmain.entity.SpeechCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DiagnosticRepository extends JpaRepository<Diagnostic,Long> {
    @EntityGraph(attributePaths = {
            "speechCard",
            "lesson.logoped",
            "lesson.patients"
    })
    Optional<Diagnostic> findBySpeechCard(SpeechCard card);

    Optional<Diagnostic> findByLessonId(Long lessonId);
}
