package gk17.rsmain.repository;

import gk17.rsmain.entity.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    @NonNull
    @EntityGraph(attributePaths = {"patients", "logoped", "homework"})
    List<Lesson> findAll();
    @NonNull
    @EntityGraph(attributePaths = {"patients", "logoped", "homework"})
    Optional<Lesson> findById(@NonNull Long id);

    @NonNull
    @EntityGraph(attributePaths = {"patients", "logoped", "homework"})
    List<Lesson> findByPatientsId(Long patientId);
    @NonNull
    @EntityGraph(attributePaths = {"patients", "logoped", "homework"})
    List<Lesson> findByLogopedId(Long patientId);
}
