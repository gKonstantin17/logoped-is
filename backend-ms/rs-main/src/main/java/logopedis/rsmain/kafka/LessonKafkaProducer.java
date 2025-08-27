package logopedis.rsmain.kafka;

import logopedis.libentities.msnotification.entity.LessonNote;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LessonKafkaProducer {
    private final KafkaTemplate<String, LessonNote> kafkaTemplate;

    public LessonKafkaProducer(KafkaTemplate<String, LessonNote> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLesson(LessonNote lessonNote) {
        kafkaTemplate.send("lesson-topic", lessonNote);
    }
}
