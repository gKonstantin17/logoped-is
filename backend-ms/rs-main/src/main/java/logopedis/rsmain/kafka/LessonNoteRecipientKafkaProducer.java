package logopedis.rsmain.kafka;

import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LessonNoteRecipientKafkaProducer {
    private final KafkaTemplate<String, LessonNoteWithRecipientDto> kafkaTemplate;

    public LessonNoteRecipientKafkaProducer(KafkaTemplate<String, LessonNoteWithRecipientDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLessonNoteRecipient(LessonNoteWithRecipientDto dto) {
        kafkaTemplate.send(KafkaTopicConfig.noteRecipientTopic, dto);
    }
}
