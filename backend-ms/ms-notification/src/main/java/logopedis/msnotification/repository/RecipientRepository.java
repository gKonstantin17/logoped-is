package logopedis.msnotification.repository;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    boolean existsByLessonNoteIdAndPatientId(Long lessonNoteId, Long patientId);

    List<Recipient> findByLessonNote(LessonNote lessonNote);
    void deleteByLessonNoteAndPatientIdAndUserId(LessonNote lessonNote, Long patientId, UUID userId);

}
