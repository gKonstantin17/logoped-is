package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.kafka.LessonStatusDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.kafka.LessonStatusKafkaProducer;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class LessonStatusUpdater {
    private final LessonStatusKafkaProducer lessonStatusKafkaProducer;
    private final Long MINUTES_FROM_START = 15L;
    private final Long LESSON_DURACTION = 60L;

    public LessonStatusUpdater(LessonStatusKafkaProducer lessonStatusKafkaProducer) {
        this.lessonStatusKafkaProducer = lessonStatusKafkaProducer;
    }

    public LessonStatus updateStatusLesson(LessonNote lessonNote) {
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp startTime = lessonNote.getStartTime();
        LessonStatus oldStatus = lessonNote.getStatus();

        switch (oldStatus) {
            case PLANNED -> {
                if (isWithinHour(now, startTime))
                    lessonNote.setStatus(LessonStatus.PLANNED_1H); // за час до начала
                else if (isStartingSoon(now, startTime))
                    lessonNote.setStatus(LessonStatus.STARTING_SOON); // за 15 минут
                else if (isCompleted(now, startTime))
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
            }

            case PLANNED_1H -> {
                if (isStartingSoon(now, startTime))
                    lessonNote.setStatus(LessonStatus.STARTING_SOON); // за 15 минут
                else if (isCompleted(now, startTime))
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
            }
            case STARTING_SOON -> {
                if (isAlreadyStarted(now,startTime))
                    lessonNote.setStatus(LessonStatus.NO_SHOW_LOGOPED); // не появился логопед
                else if (isCompleted(now,startTime))
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
            }
            case IN_PROGRESS -> {
                if (isCompleted(now,startTime))
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
            }
            case CANCELED_BY_CLIENT,
                    CANCELED_BY_LOGOPED,
                    COMPLETED,
                    NO_SHOW_CLIENT,
                    NO_SHOW_LOGOPED -> {
                // финальные статусы → ничего не меняем
            }
        }
        // обновление статусов Lesson в rs-main
        if (lessonNote.getStatus() != oldStatus) {
            LessonStatusDto dto = new LessonStatusDto(lessonNote.getId(),lessonNote.getStatus());
            lessonStatusKafkaProducer.sendLessonStatus(dto);
        }
        //lessonNoteService.save(lessonNote);
        return lessonNote.getStatus();
    }
    private boolean isStartingSoon(Timestamp now, Timestamp startTime) {
        // 15 минут от начала занятия
        return now.after(Timestamp.from(startTime.toInstant().minusSeconds(MINUTES_FROM_START * 60)))
                && now.before(startTime);
    }
    private boolean isAlreadyStarted(Timestamp now, Timestamp startTime) {
        // 15 минут после начала
        return now.after(Timestamp.from(startTime.toInstant().plusSeconds(MINUTES_FROM_START * 60)))
                && now.before(Timestamp.from(startTime.toInstant().plusSeconds(LESSON_DURACTION * 60)));
    }

    private boolean isCompleted(Timestamp now, Timestamp startTime) {
        // 60 минут от начала
        return now.after(Timestamp.from(startTime.toInstant().plusSeconds(LESSON_DURACTION * 60)));
    }
    private boolean isWithinHour(Timestamp now, Timestamp startTime) {
        // 60 минут до начала
        long minutesToStart = (startTime.getTime() - now.getTime()) / 60000;
        return minutesToStart <= 60 && minutesToStart > 15;
    }
}
