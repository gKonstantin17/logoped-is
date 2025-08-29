package logopedis.msnotification.kafka;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class LessonNoteKafkaConsumer {
    private final LessonNoteService service;
    public LessonNoteKafkaConsumer(LessonNoteService service) {
        this.service = service;
    }

    @KafkaListener(topics = "lesson-note-topic")
    public void consume(LessonNote lessonNote) {
        service.createIfNotExist(lessonNote);
    }
}
