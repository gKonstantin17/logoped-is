package logopedis.msnotification.kafka;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LessonKafkaConsumer {
    private final LessonNoteService service;
    public LessonKafkaConsumer(LessonNoteService service) {
        this.service = service;
    }

    @KafkaListener(topics = "lesson-topic")
    public void consume(LessonNote lessonNote) {
        System.out.println("ID CREATED:"+lessonNote.getId());
        service.create(lessonNote);
    }
}
