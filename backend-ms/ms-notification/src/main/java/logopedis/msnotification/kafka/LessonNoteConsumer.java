package logopedis.msnotification.kafka;

import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libutils.kafka.KafkaTopicConfig;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LessonNoteConsumer {
    private final LessonNoteService service;
    public LessonNoteConsumer(LessonNoteService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopicConfig.lessonNoteTopic)
    public void consume(LessonNote lessonNote) {
        service.updateStatus(lessonNote);
    }
}