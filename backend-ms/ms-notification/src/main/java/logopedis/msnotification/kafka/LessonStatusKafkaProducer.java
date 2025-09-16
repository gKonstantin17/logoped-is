package logopedis.msnotification.kafka;

import logopedis.libentities.kafka.LessonStatusDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LessonStatusKafkaProducer {
    private final KafkaTemplate<String, LessonStatusDto> kafkaTemplate;

    public LessonStatusKafkaProducer(KafkaTemplate<String, LessonStatusDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLessonStatus(LessonStatusDto lessonStatusDto) {
        kafkaTemplate.send(KafkaTopicConfig.lessonStatusTopic, lessonStatusDto);
    }
}
