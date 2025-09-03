package logopedis.rsmain.kafka;

import logopedis.libentities.kafka.LessonStatusDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import logopedis.rsmain.service.LessonService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
public class LessonStatusKafkaConsumer {
    private final LessonService service;

    public LessonStatusKafkaConsumer(LessonService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopicConfig.lessonStatusTopic)
    public void consume(LessonStatusDto dto) {
       service.updateStatusFromKafka(dto);
    }
}
