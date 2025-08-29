package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.entity.LessonNote;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class LessonStatusUpdater {
    private final Long minutesFromStart = 15L;
    private final Long lessonDuraction = 60L;

    public LessonStatus updateStatusLesson(LessonNote lessonNote) {
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp startTime = lessonNote.getStartTime();

        switch (lessonNote.getStatus()) {
            case PLANNED -> {
                if (isStartingSoon(now, startTime)) {
                    lessonNote.setStatus(LessonStatus.STARTING_SOON); // скоро начнется
                } else if (isCompleted(now,startTime)) {
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
                }
            }
            case STARTING_SOON -> {
                if (isAlreadyStarted(now,startTime)) {
                    lessonNote.setStatus(LessonStatus.NO_SHOW_LOGOPED); // не появился логопед
                } else if (isCompleted(now,startTime)) {
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
                }
            }
            case IN_PROGRESS -> {
                if (isCompleted(now,startTime)) {
                    lessonNote.setStatus(LessonStatus.COMPLETED); // прошло
                }
            }
            case CANCELED_BY_CLIENT,
                    CANCELED_BY_LOGOPED,
                    COMPLETED,
                    NO_SHOW_CLIENT,
                    NO_SHOW_LOGOPED -> {
                // финальные статусы → ничего не меняем
            }
        }

        //lessonNoteService.save(lessonNote);
        return lessonNote.getStatus();
    }
    private boolean isStartingSoon(Timestamp now, Timestamp startTime) {
        // 15 минут от начала занятия
        return now.after(Timestamp.from(startTime.toInstant().minusSeconds(minutesFromStart * 60)))
                && now.before(startTime);
    }
    private boolean isAlreadyStarted(Timestamp now, Timestamp startTime) {
        // 15 минут после начала
        return now.after(Timestamp.from(startTime.toInstant().plusSeconds(minutesFromStart * 60)))
                && now.before(Timestamp.from(startTime.toInstant().plusSeconds(lessonDuraction * 60)));
    }

    private boolean isCompleted(Timestamp now, Timestamp startTime) {
        // 60 минут от начала
        return now.after(Timestamp.from(startTime.toInstant().plusSeconds(lessonDuraction * 60)));
    }
}
