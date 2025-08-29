package logopedis.msnotification.service;

import logopedis.libentities.kafka.LessonPeriodRequest;
import logopedis.msnotification.kafka.LessonRequestKafkaProducer;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class LessonIntegrationService {
    private final LessonRequestKafkaProducer kafkaProducer;

    public LessonIntegrationService(LessonRequestKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void fetchUpcomingLessons(int days) {
        // Текущая дата
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate endDay = today.plusDays(days);

        // Начало сегодняшнего дня 00:00:00
        LocalDateTime startOfToday = today.atStartOfDay();
        Timestamp start = Timestamp.valueOf(startOfToday);

        // Конец конечного дня 23:59:59.999
        LocalDateTime endOfEndDay = endDay.plusDays(1).atStartOfDay().minusNanos(1);
        Timestamp end = Timestamp.valueOf(endOfEndDay);
        // запрос через кафку
        LessonPeriodRequest request = new LessonPeriodRequest(start, end);
        kafkaProducer.requestLessons(request);
    }
}
