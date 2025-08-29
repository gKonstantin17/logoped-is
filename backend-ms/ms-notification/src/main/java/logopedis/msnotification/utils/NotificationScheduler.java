package logopedis.msnotification.utils;


import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.service.LessonNoteService;
import logopedis.msnotification.service.LessonStatusUpdater;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Component
public class NotificationScheduler {
    //@Scheduled, проверка LessonNote и создание Notification за 1 час / 15 минут / при отмене
    private final LessonNoteService lessonNoteService;
    private final LessonStatusUpdater lessonStatusUpdater;
    private final List<LessonStatus> checkedStatuses = List.of(
            LessonStatus.PLANNED,
            LessonStatus.STARTING_SOON,
            LessonStatus.IN_PROGRESS);
    public NotificationScheduler(LessonNoteService lessonNoteService, LessonStatusUpdater lessonStatusUpdater) {
        this.lessonNoteService = lessonNoteService;
        this.lessonStatusUpdater = lessonStatusUpdater;
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void runEveryFiveMinutes() {
        Timestamp start = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(23, 59, 59));

        List<LessonNote> lessonNotes = lessonNoteService.findByPeriodAndStatuses(start, end, checkedStatuses);
        lessonNotes.forEach(lessonNote -> {
            LessonStatus newStatus = lessonStatusUpdater.updateStatusLesson(lessonNote);
            lessonNote.setStatus(newStatus);
            lessonNoteService.save(lessonNote);
        });

    }
}
