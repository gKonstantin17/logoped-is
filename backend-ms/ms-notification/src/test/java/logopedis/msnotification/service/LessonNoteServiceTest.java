package logopedis.msnotification.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.kafka.LessonNoteWithRecipientDto;
import logopedis.libentities.msnotification.dto.lessonNote.LessonNoteChangeDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.msnotification.repository.LessonNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonNoteServiceTest {

    private LessonNoteRepository repository;
    private LessonStatusUpdater statusUpdater;
    private RecipientService recipientService;
    private NotificationService notificationService;
    private LessonNoteService service;

    @BeforeEach
    void setUp() {
        repository = mock(LessonNoteRepository.class);
        statusUpdater = mock(LessonStatusUpdater.class);
        recipientService = mock(RecipientService.class);
        notificationService = mock(NotificationService.class);

        service = new LessonNoteService(repository, statusUpdater, recipientService, notificationService);
    }

    @Test
    void findall_shouldReturnAllLessons() throws Exception {
        List<LessonNote> notes = Arrays.asList(new LessonNote(), new LessonNote());
        when(repository.findAll()).thenReturn(notes);

        var future = service.findall();
        var result = future.get().data();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnLessonIfExists() {
        LessonNote note = new LessonNote();
        note.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(note));

        var result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void create_shouldSaveLesson() throws Exception {
        LessonNote note = new LessonNote();
        when(repository.save(note)).thenReturn(note);

        var future = service.create(note);
        var result = future.get().data();

        assertEquals(note, result);
    }

    @Test
    void updateStatus_shouldUpdateLessonStatusAndNotify() {
        LessonNote existing = new LessonNote();
        existing.setId(1L);
        existing.setStatus(LessonStatus.PLANNED);
        existing.setStartTime(new Timestamp(System.currentTimeMillis()));

        LessonNote updated = new LessonNote();
        updated.setId(1L);
        updated.setStatus(LessonStatus.COMPLETED);
        updated.setStartTime(existing.getStartTime());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(updated);

        assertEquals(LessonStatus.COMPLETED, existing.getStatus());
        verify(notificationService).createFromLessonNote(existing);
    }

    @Test
    void save_shouldDelegateToRepository() {
        LessonNote note = new LessonNote();
        when(repository.save(note)).thenReturn(note);

        var result = service.save(note);
        assertEquals(note, result);
    }

    @Test
    void findByPeriod_shouldReturnLessonsInPeriod() {
        Timestamp start = Timestamp.valueOf("2025-01-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2025-01-31 23:59:59");
        List<LessonNote> notes = List.of(new LessonNote());
        when(repository.findByStartTimeBetween(start, end)).thenReturn(notes);

        var result = service.findByPeriod(start, end);
        assertEquals(notes, result);
    }

    @Test
    void findByPeriodAndStatuses_shouldReturnFilteredLessons() {
        Timestamp start = Timestamp.valueOf("2025-01-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2025-01-31 23:59:59");
        List<LessonStatus> statuses = List.of(LessonStatus.PLANNED);
        List<LessonNote> notes = List.of(new LessonNote());
        when(repository.findByStartTimeBetweenAndStatusIn(start, end, statuses)).thenReturn(notes);

        var result = service.findByPeriodAndStatuses(start, end, statuses);
        assertEquals(notes, result);
    }

    @Test
    void delete_shouldDeleteLesson() throws Exception {
        LessonNote note = new LessonNote();
        note.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(note));

        var future = service.delete(1L);
        var result = future.get().data();

        verify(repository).deleteById(1L);
        assertEquals(1L, result);
    }

    @Test
    void update_shouldUpdateLessonFields() throws Exception {
        LessonNote note = new LessonNote();
        note.setId(1L);
        note.setStatus(LessonStatus.PLANNED);
        when(repository.findById(1L)).thenReturn(Optional.of(note));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LessonNoteChangeDto dto = new LessonNoteChangeDto(LessonStatus.COMPLETED, new Timestamp(System.currentTimeMillis()));
        var future = service.update(1L, dto);
        var result = future.get().data();

        assertEquals(LessonStatus.COMPLETED, result.getStatus());
        assertEquals(dto.startTime(), result.getStartTime());
    }
}
