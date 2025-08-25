package logopedis.msnotification.service;

import logopedis.libentities.msnotification.entity.Notification;
import logopedis.msnotification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<Notification> findall() {
        return repository.findAll();
    }

    public Notification findById(Long id) {
        Notification result = repository.findById(id).get();
        return result;
    }

    public Notification create(Notification note) {
        Notification lessonNote = new Notification();
        lessonNote.setLessonId(note.getLessonId());
        lessonNote.setMessage(note.getMessage());
        lessonNote.setSendDate(note.getSendDate());
        lessonNote.setReceived(note.getReceived());
        repository.save(lessonNote);
        return lessonNote;
    }

    public Notification update(Notification note) {
        Notification updated =  findById(note.getId());
        if (note.getMessage() != null)
            updated.setMessage(note.getMessage());
        if (note.getSendDate() != null)
            updated.setSendDate(note.getSendDate());
        if (note.getReceived() != null)
            updated.setReceived(note.getReceived());
        repository.save(updated);
        return updated;
    }

    public Notification delete (Long id) {
        Notification deleted = findById(id);
        repository.deleteById(id);
        return deleted;
    }
}
