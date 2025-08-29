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
    private final LessonNoteKafkaProducer lessonNoteKafkaProducer;

    public LessonRequestKafkaConsumer(LessonService service, LessonNoteKafkaProducer lessonNoteKafkaProducer) {
        this.service = service;
        this.lessonNoteKafkaProducer = lessonNoteKafkaProducer;
    }
    @KafkaListener(topics = KafkaTopicConfig.lessonRequestTopic)
    public void consume(LessonPeriodRequest request) {
        Timestamp start = request.periodStart();
        Timestamp end = request.periodEnd();

        service.createResponseInLessonNote(start,end)
                .thenAccept(lessonNoteKafkaProducer::sendLessonNotes)
                .join();
    }
}
