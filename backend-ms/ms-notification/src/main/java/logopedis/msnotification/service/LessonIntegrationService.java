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

    public void fetchUpcomingLessons() {
        // Текущая дата
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        // Вчерашний день
        LocalDate yesterday = today.minusDays(1);
        // Завтрашний день
        LocalDate tomorrow = today.plusDays(1);

        // Начало вчерашнего дня 00:00:00
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        Timestamp start = Timestamp.valueOf(startOfYesterday);

        // Конец завтрашнего дня 23:59:59.999
        LocalDateTime endOfTomorrow = tomorrow.plusDays(1).atStartOfDay().minusNanos(1);
        Timestamp end = Timestamp.valueOf(endOfTomorrow);

        // Запрос через Kafka
        LessonPeriodRequest request = new LessonPeriodRequest(start, end);
        kafkaProducer.requestLessons(request);
    }
}
