package logopedis.msnotification.kafka;

import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class LessonNoteRecipientKafkaConsumer {
    private final LessonNoteService service;
    public LessonNoteRecipientKafkaConsumer(LessonNoteService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopicConfig.noteRecipientTopic)
    public void consume(LessonNoteWithRecipientDto dto) {
        service.createWithRecipient(dto);
    }
}
