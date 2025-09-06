package logopedis.msnotification.kafka;

import logopedis.libentities.kafka.LessonsForPeriodDto;
import logopedis.libutils.kafka.KafkaTopicConfig;
import logopedis.msnotification.service.LessonNoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LessonForPeriodConsumer {
    private final LessonNoteService service;
    public LessonForPeriodConsumer(LessonNoteService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopicConfig.lessonForPeriodTopic)
    public void consume(LessonsForPeriodDto dto) {
        service.createForLessonPeriod(dto);
    }
}