package logopedis.msnotification.service;

import logopedis.libentities.msnotification.dto.recipient.RecipientDataDto;
import logopedis.libentities.msnotification.entity.LessonNote;
import logopedis.libentities.msnotification.entity.Recipient;
import logopedis.msnotification.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipientServiceTest {

    private RecipientRepository repository;
    private RecipientService service;

    @BeforeEach
    void setUp() {
        repository = mock(RecipientRepository.class);
        service = new RecipientService(repository);
    }

    @Test
    void findByLessonNote_shouldReturnRecipients() {
        LessonNote lesson = new LessonNote();
        Recipient r1 = new Recipient();
        r1.setPatientId(1L);
        r1.setUserId(UUID.randomUUID());

        when(repository.findByLessonNote(lesson)).thenReturn(List.of(r1));

        var result = service.findByLessonNote(lesson);
        assertEquals(1, result.size());
        assertEquals(r1, result.get(0));
    }

    @Test
    void findPairsByLessonNote_shouldReturnPatientUserPairs() {
        LessonNote lesson = new LessonNote();
        UUID userId = UUID.randomUUID();

        Recipient r1 = new Recipient();
        r1.setPatientId(1L);
        r1.setUserId(userId);

        when(repository.findByLessonNote(lesson)).thenReturn(List.of(r1));

        Set<String> pairs = service.findPairsByLessonNote(lesson);
        assertEquals(Set.of("1:" + userId), pairs);
    }

    @Test
    void save_shouldDelegateToRepository() {
        Recipient r = new Recipient();
        when(repository.save(r)).thenReturn(r);

        Recipient result = service.save(r);
        assertEquals(r, result);
        verify(repository).save(r);
    }

    @Test
    void createFromDto_shouldSaveNewRecipientsOnly() {
        LessonNote lesson = new LessonNote();
        lesson.setId(1L);
        UUID userId = UUID.randomUUID();

        RecipientDataDto dto = new RecipientDataDto(1L, userId);
        List<RecipientDataDto> dtos = List.of(dto);

        // имитируем, что записи ещё нет
        when(repository.existsByLessonNoteIdAndPatientId(lesson.getId(), dto.patientId())).thenReturn(false);

        service.createFromDto(dtos, lesson);

        ArgumentCaptor<Recipient> captor = ArgumentCaptor.forClass(Recipient.class);
        verify(repository).save(captor.capture());

        Recipient saved = captor.getValue();
        assertEquals(lesson, saved.getLessonNote());
        assertEquals(1L, saved.getPatientId());
        assertEquals(userId, saved.getUserId());
    }

    @Test
    void createFromDto_shouldNotSaveIfExists() {
        LessonNote lesson = new LessonNote();
        lesson.setId(1L);
        UUID userId = UUID.randomUUID();

        RecipientDataDto dto = new RecipientDataDto(1L, userId);

        when(repository.existsByLessonNoteIdAndPatientId(lesson.getId(), dto.patientId())).thenReturn(true);

        service.createFromDto(List.of(dto), lesson);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteByLessonNoteAndPatientIdAndUserId_shouldCallRepository() {
        LessonNote lesson = new LessonNote();
        UUID userId = UUID.randomUUID();

        service.deleteByLessonNoteAndPatientIdAndUserId(lesson, 1L, userId);

        verify(repository).deleteByLessonNoteAndPatientIdAndUserId(lesson, 1L, userId);
    }
}
