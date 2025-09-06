package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.recipient.RecipientCreateDto;
import logopedis.libentities.msnotification.dto.recipient.RecipientDataDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.msnotification.repository.RecipientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecipientService {
    private final RecipientRepository repository;

    public RecipientService(RecipientRepository repository) {
        this.repository = repository;
    }

    public Recipient create(RecipientCreateDto dto) {
        Recipient recipient = new Recipient();
        recipient.setLessonNote(dto.lessonNote());
        recipient.setUserId(dto.userId());
        recipient.setPatientId(dto.patientId());
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

    public List<Recipient> findByLessonNote(LessonNote lessonNote) {
        return repository.findByLessonNote(lessonNote);
    }

    public List<UUID> findUsersByLessonNote(LessonNote lessonNote) {
        List<Recipient> list = findByLessonNote(lessonNote);
        return list.stream().map(Recipient::getUserId).toList();
    }
}
