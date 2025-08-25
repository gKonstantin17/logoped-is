package logopedis.msnotification.repository;

import logopedis.libentities.msnotification.entity.LessonNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonNoteRepository extends JpaRepository<LessonNote, Long> {
}
