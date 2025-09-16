package logopedis.msnotification.utils;


import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.msnotification.service.LessonNoteService;
import logopedis.msnotification.service.LessonStatusUpdater;
import logopedis.msnotification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Component
public class NotificationScheduler {
    //@Scheduled, проверка LessonNote и создание Notification за 1 час / 15 минут / при отмене
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    private final LessonNoteService lessonNoteService;
    private final LessonStatusUpdater lessonStatusUpdater;
    private final NotificationService notificationService;
    private final List<LessonStatus> checkedStatuses = List.of(
            LessonStatus.PLANNED,
            LessonStatus.STARTING_SOON,
            LessonStatus.IN_PROGRESS);
    public NotificationScheduler(LessonNoteService lessonNoteService, LessonStatusUpdater lessonStatusUpdater, NotificationService notificationService) {
        this.lessonNoteService = lessonNoteService;
        this.lessonStatusUpdater = lessonStatusUpdater;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void updateStatusesInDB() {
        Timestamp start = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(23, 59, 59));

        List<LessonNote> lessonNotes = lessonNoteService.findByPeriodAndStatuses(start, end, checkedStatuses);
        int updatedCount = 0;

        for (LessonNote lessonNote : lessonNotes) {
            LessonStatus oldStatus = lessonNote.getStatus();
            LessonStatus newStatus = lessonStatusUpdater.updateStatusLesson(lessonNote);
            if (newStatus != oldStatus) {
                lessonNote.setStatus(newStatus);
                var result = lessonNoteService.save(lessonNote);
                notificationService.createFromLessonNote(result);
                updatedCount++;
                log.info("У занятия id={} обновлен статус: {} -> {}", lessonNote.getId(), oldStatus, newStatus);
            }
        }
        log.info("Запланированное обновление статусов закончено. Всего просмотрено: {}, обновлено: {}", lessonNotes.size(), updatedCount);

    }
}
