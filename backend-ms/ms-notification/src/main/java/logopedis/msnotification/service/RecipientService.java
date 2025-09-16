package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.recipient.RecipientCreateDto;
import logopedis.libentities.msnotification.dto.recipient.RecipientDataDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.msnotification.repository.RecipientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipientService {
    private final RecipientRepository repository;

    public RecipientService(RecipientRepository repository) {
        this.repository = repository;
    }
    public List<Recipient> findByLessonNote(LessonNote lessonNote) {
        return repository.findByLessonNote(lessonNote);
    }
    public Set<String> findPairsByLessonNote(LessonNote lessonNote) {
        return repository.findByLessonNote(lessonNote).stream()
                .map(r -> r.getPatientId() + ":" + r.getUserId())
                .collect(Collectors.toSet());
    }

    public Recipient save(Recipient recipient) {
        return repository.save(recipient);
    }

    public void createFromDto(List<RecipientDataDto> recipientDtos, LessonNote lessonNote) {
        for (RecipientDataDto data : recipientDtos) {
            boolean exists = repository.existsByLessonNoteIdAndPatientId(
                    lessonNote.getId(),
                    data.patientId()
            );

            if (!exists) {
                Recipient recipient = new Recipient();
                recipient.setLessonNote(lessonNote);
                recipient.setPatientId(data.patientId());
                recipient.setUserId(data.userId());
                repository.save(recipient);
            }
        }
    }

    public void deleteByLessonNoteAndPatientIdAndUserId(LessonNote lessonNote, Long patientId, UUID userId) {
        repository.deleteByLessonNoteAndPatientIdAndUserId(lessonNote, patientId, userId);
    }
}
