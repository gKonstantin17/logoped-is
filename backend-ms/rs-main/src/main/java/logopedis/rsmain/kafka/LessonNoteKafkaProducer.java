package logopedis.rsmain.kafka;

import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libutils.kafka.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonNoteKafkaProducer {
    private final KafkaTemplate<String, LessonNote> kafkaTemplate;

    public LessonNoteKafkaProducer(KafkaTemplate<String, LessonNote> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLessonNote(LessonNote lessonNote) {
        kafkaTemplate.send(KafkaTopicConfig.lessonNoteTopic, lessonNote);
    }

    public void sendLessonNotes(List<LessonNote> lessonNotes) {
        lessonNotes.forEach(lessonNote -> {
            kafkaTemplate.send(KafkaTopicConfig.lessonNoteTopic, lessonNote);
        });
    }
}
