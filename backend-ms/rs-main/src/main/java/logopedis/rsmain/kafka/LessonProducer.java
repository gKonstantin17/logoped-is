package logopedis.rsmain.kafka;

import logopedis.libentities.msnotification.entity.LessonNote;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LessonProducer {
    private final KafkaTemplate<String, LessonNote> kafkaTemplate;

    public LessonProducer(KafkaTemplate<String, LessonNote> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLesson(LessonNote lessonNote) {
        kafkaTemplate.send("lesson-topic", lessonNote);
    }
}
