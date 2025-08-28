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
        System.out.println("Получено из rs:"+lessonNote.getId());
        service.createIfNotExist(lessonNote).thenAccept(result -> {
            if (result.isSuccess())
                System.out.println("LessonNote создан: " + result.data().getId());
            else
                System.out.println("Ошибка: " + result.message());
        });
    }
}
