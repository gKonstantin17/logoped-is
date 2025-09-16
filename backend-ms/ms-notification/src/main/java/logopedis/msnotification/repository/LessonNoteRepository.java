package logopedis.msnotification.repository;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.rsmain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface LessonNoteRepository extends JpaRepository<LessonNote, Long> {
    List<LessonNote> findByStartTimeBetween(Timestamp start, Timestamp end);
    List<LessonNote> findByStartTimeBetweenAndStatusIn(Timestamp start, Timestamp end, List<LessonStatus> statuses);

}
