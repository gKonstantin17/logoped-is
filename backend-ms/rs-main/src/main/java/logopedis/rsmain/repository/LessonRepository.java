package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    List<Lesson> findByLogopedId(UUID logopedId);
    @Query("""
    SELECT l FROM Lesson l
    WHERE l.logoped.id = :logopedId
      AND l.dateOfLesson BETWEEN :start AND :end
    """)
    List<Lesson> findByLogopedIdAndDateRange(
            @Param("logopedId") UUID logopedId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.patients WHERE l.dateOfLesson BETWEEN :start AND :end")
    List<Lesson> findByDateOfLessonBetween(@Param("start") Timestamp start, @Param("end") Timestamp end);
}
