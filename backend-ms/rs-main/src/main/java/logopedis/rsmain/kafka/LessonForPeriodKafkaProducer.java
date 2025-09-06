package logopedis.rsmain.kafka;

import logopedis.libentities.kafka.LessonsForPeriodDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class LessonForPeriodKafkaProducer {
    private final KafkaTemplate<String, LessonsForPeriodDto> kafkaTemplate;

    public LessonForPeriodKafkaProducer(KafkaTemplate<String, LessonsForPeriodDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLessonForPeriod(LessonsForPeriodDto dto) {
        kafkaTemplate.send(KafkaTopicConfig.lessonForPeriodTopic, dto);
    }

}