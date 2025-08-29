package logopedis.msnotification.kafka;

import logopedis.libentities.kafka.LessonPeriodRequest;
import logopedis.libutils.kafka.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LessonRequestKafkaProducer {
    private final KafkaTemplate<String, LessonPeriodRequest> kafkaTemplate;

    public LessonRequestKafkaProducer(KafkaTemplate<String, LessonPeriodRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void requestLessons(LessonPeriodRequest request) {
        kafkaTemplate.send(KafkaTopicConfig.lessonRequestTopic, request);
    }
}
