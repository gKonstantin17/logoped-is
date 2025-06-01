package gk17.rsmain.repository;

import gk17.rsmain.entity.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    @EntityGraph(attributePaths = {"patients", "logoped", "homework"})
    List<Lesson> findAll();
}
