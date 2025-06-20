package gk17.rsmain.repository;

import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.entity.SpeechCard;
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
