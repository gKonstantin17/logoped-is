package logopedis.msnotification.service;

import logopedis.libentities.enums.NotificationMsg;
import logopedis.libentities.msnotification.dto.notification.NotificationUpdateDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Notification;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.msnotification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository repository;
    private NotificationCreater notificationCreater;
    private RecipientService recipientService;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationRepository.class);
        notificationCreater = mock(NotificationCreater.class);
        recipientService = mock(RecipientService.class);
        service = new NotificationService(repository, notificationCreater, recipientService);
    }

    @Test
    void findall_shouldReturnAllNotifications() throws Exception {
        // создаём LessonNote для каждой нотификации
        LessonNote lesson1 = new LessonNote();
        lesson1.setId(1L);
        LessonNote lesson2 = new LessonNote();
        lesson2.setId(2L);

        Notification n1 = new Notification();
        n1.setLessonNote(lesson1);

        Notification n2 = new Notification();
        n2.setLessonNote(lesson2);

        List<Notification> notifications = List.of(n1, n2);
        when(repository.findAll()).thenReturn(notifications);

        var future = service.findall();
        var result = future.get().data(); // или get().getData() в зависимости от версии AsyncResult

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).lessonNoteId()); // проверяем ID урока
        assertEquals(2L, result.get(1).lessonNoteId());
    }

    @Test
    void findByUser_shouldReturnNotificationsForUser() throws Exception {
        UUID userId = UUID.randomUUID();

        // создаём LessonNote для Notification
        LessonNote lesson = new LessonNote();
        lesson.setId(1L);

        Notification n = new Notification();
        n.setLessonNote(lesson);
        n.setRecipientId(userId);

        List<Notification> notifications = List.of(n);
        when(repository.findByRecipientId(userId)).thenReturn(notifications);

        var future = service.findByUser(userId);
        var result = future.get().data(); // или get().getData() в зависимости от версии AsyncResult

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).lessonNoteId()); // проверяем ID урока
    }


    @Test
    void findById_shouldReturnNotification() {
        Notification n = new Notification();
        n.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(n));

        Notification result = service.findById(1L);
        assertEquals(1L, result.getId());
    }


    @Test
    void update_shouldUpdateNotification() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(n));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationUpdateDto dto = new NotificationUpdateDto(1L,new Timestamp(System.currentTimeMillis()), "msg", true, UUID.randomUUID(), List.of(1L));
        var future = service.update(1L, dto);
        var result = future.get().data();

        assertEquals("msg", result.getMessage());
        assertTrue(result.getReceived());
    }

    @Test
    void delete_shouldDeleteNotification() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(n));

        var future = service.delete(1L);
        var result = future.get().data();

        verify(repository).deleteById(1L);
        assertEquals(1L, result);
    }

    @Test
    void receive_shouldMarkNotificationAsReceived() throws Exception {
        // создаём LessonNote
        LessonNote lesson = new LessonNote();
        lesson.setId(1L);

        Notification n = new Notification();
        n.setId(1L);
        n.setReceived(false);
        n.setLessonNote(lesson); // важный шаг

        when(repository.findById(1L)).thenReturn(Optional.of(n));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var future = service.receive(1L);
        var result = future.get().data(); // или get().getData() в зависимости от версии AsyncResult

        assertNotNull(result);
        assertTrue(result.received());
    }
}
