package logopedis.msnotification.service;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.repository.LessonNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonNoteService {
    private final LessonNoteRepository repository;
    public LessonNoteService(LessonNoteRepository repository) {
        this.repository = repository;
    }
    public List<LessonNote> findall() {
        return repository.findAll();
    }

    public LessonNote findById(Long id) {
        LessonNote result = repository.findById(id).get();
        return result;
    }

    public LessonNote create(LessonNote note) {
        LessonNote lessonNote = new LessonNote();
        lessonNote.setStatus(note.getStatus());
        lessonNote.setStartTime(note.getStartTime());
        repository.save(lessonNote);
        return lessonNote;
    }

    public LessonNote update(LessonNote note) {
        LessonNote updated =  findById(note.getId());
        if (note.getStatus() != null)
            updated.setStatus(note.getStatus());
        if (note.getStartTime() != null)
            updated.setStartTime(note.getStartTime());
        repository.save(updated);
        return updated;
    }

    public LessonNote delete (Long id) {
        LessonNote deleted = findById(id);
        repository.deleteById(id);
        return deleted;
    }
}
