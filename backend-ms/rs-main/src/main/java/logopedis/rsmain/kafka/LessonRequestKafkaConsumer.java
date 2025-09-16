package logopedis.rsmain.kafka;

import logopedis.libentities.kafka.LessonPeriodRequest;
import logopedis.libutils.kafka.KafkaTopicConfig;
import logopedis.rsmain.service.LessonService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class LessonRequestKafkaConsumer {
    private final LessonService service;
    private final LessonForPeriodKafkaProducer forPeriodKafkaProducer;

    public LessonRequestKafkaConsumer(LessonService service, LessonForPeriodKafkaProducer forPeriodKafkaProducer) {
        this.service = service;
        this.forPeriodKafkaProducer = forPeriodKafkaProducer;
    }
    @KafkaListener(topics = KafkaTopicConfig.lessonRequestTopic)
    public void consume(LessonPeriodRequest request) {
        Timestamp start = request.periodStart();
        Timestamp end = request.periodEnd();

        service.createResponseInLessonNote(start,end)
                .thenAccept(forPeriodKafkaProducer::sendLessonForPeriod)
                .join();
    }
}
