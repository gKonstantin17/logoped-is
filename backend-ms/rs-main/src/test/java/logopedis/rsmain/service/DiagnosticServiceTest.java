package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticDto;
import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.libentities.rsmain.entity.Lesson;
import logopedis.libentities.rsmain.entity.SpeechCard;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.rsmain.repository.SpeechCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiagnosticServiceTest {
    @Mock
    private DiagnosticRepository repository;
    @Mock
    private LessonService lessonService;
    @Mock
    private SpeechCardService speechCardService;
    @Mock
    private SpeechCardRepository speechCardRepository;
    @InjectMocks
    private DiagnosticService service;

    @Test
    void findall_ReturnsDto() throws ExecutionException, InterruptedException {
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        SpeechCard speechCard = new SpeechCard();
        speechCard.setId(1L);
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setId(1L);
        diagnostic.setLesson(lesson);
        diagnostic.setSpeechCard(speechCard);
        diagnostic.setDate(Timestamp.valueOf("2025-01-01 10:00:00"));

        List<Diagnostic> list = List.of(diagnostic);
        when(repository.findAll()).thenReturn(list);

        CompletableFuture<ServiceResult<List<DiagnosticReadDto>>> resultFuture = service.findall();
        ServiceResult<List<DiagnosticReadDto>> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);

        verify(repository).findAll();
    }
    @Test
    void create_ReturnsDto() throws Exception {
        Long lessonId = 1L;
        Long speechCardId = 1L;
        Timestamp date = Timestamp.valueOf("2025-01-01 10:00:00");
        DiagnosticDto dto = new DiagnosticDto(date, lessonId, speechCardId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        when(lessonService.findById(lessonId)).thenReturn(lesson);

        SpeechCard speechCard = new SpeechCard();
        speechCard.setId(speechCardId);
        when(speechCardRepository.findById(speechCardId)).thenReturn(Optional.of(speechCard));

        when(repository.save(any(Diagnostic.class))).thenAnswer(inv -> {
            Diagnostic d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        CompletableFuture<ServiceResult<DiagnosticReadDto>> resultFuture = service.create(dto);
        ServiceResult<DiagnosticReadDto> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().date()).isEqualTo(date);
        assertThat(result.data().lessonId()).isEqualTo(lessonId);
        assertThat(result.data().speechCardId()).isEqualTo(speechCardId);

        verify(repository).save(any(Diagnostic.class));
        verify(lessonService).findById(dto.lessonId());
        verify(speechCardRepository).findById(dto.speechCardId());
    }

    @Test
    void update_ReturnsDto() throws Exception {
        Long id = 1L;
        Timestamp date = Timestamp.valueOf("2026-01-01 10:00:00");
        Long lessonId = 2L;
        Long speechCardId = 2L;
        DiagnosticDto dto = new DiagnosticDto(date, lessonId, speechCardId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        SpeechCard speechCard = new SpeechCard();
        speechCard.setId(speechCardId);

        Diagnostic existing = new Diagnostic();
        existing.setId(id);
        existing.setLesson(new Lesson());
        existing.setSpeechCard(new SpeechCard());
        existing.setDate(Timestamp.valueOf("2025-01-01 10:00:00"));

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Диагностика не найдена"))
                    .thenReturn(existing);

            when(lessonService.findById(lessonId)).thenReturn(lesson);
            when(speechCardRepository.findById(speechCardId)).thenReturn(Optional.of(speechCard));
            when(repository.save(any(Diagnostic.class))).thenAnswer(inv -> inv.getArgument(0));
            when(repository.findById(existing.getId())).thenReturn(Optional.of(existing));

            CompletableFuture<ServiceResult<DiagnosticReadDto>> resultFuture = service.update(id, dto);
            ServiceResult<DiagnosticReadDto> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().id()).isEqualTo(id);
            assertThat(result.data().date()).isEqualTo(date);
            assertThat(result.data().lessonId()).isEqualTo(lessonId);
            assertThat(result.data().speechCardId()).isEqualTo(speechCardId);

            verify(repository).save(any(Diagnostic.class));
            verify(lessonService).findById(dto.lessonId());
            verify(speechCardRepository).findById(dto.speechCardId());
        }
    }

    @Test
    void delete_ReturnsId() throws ExecutionException, InterruptedException {
        Long id = 1L;
        Diagnostic existing = new Diagnostic();
        existing.setId(id);
        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(()->ResponseHelper.findById(repository,id,"Диагностика не найдена"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> resultFuture = service.delete(id);
            ServiceResult<Long> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }

    @Test
    void findBySpeechCard_ReturnsDiagnostic() {
        SpeechCard sc = new SpeechCard();
        sc.setId(1L);
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setId(10L);

        when(repository.findBySpeechCard(sc)).thenReturn(Optional.of(diagnostic));

        Diagnostic result = service.findBySpeechCard(sc);

        assertThat(result).isEqualTo(diagnostic);
        verify(repository).findBySpeechCard(sc);
    }

    @Test
    void findLatestDiagnosticByPatientId_ReturnsDiagnostic() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setId(11L);

        when(repository.findLatestDiagnosticByPatientId(5L)).thenReturn(Optional.of(diagnostic));

        Diagnostic result = service.findLatestDiagnosticByPatientId(5L);

        assertThat(result.getId()).isEqualTo(11L);
        verify(repository).findLatestDiagnosticByPatientId(5L);
    }

    @Test
    void findEarliestDiagnosticByPatientId_ReturnsDiagnostic() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setId(12L);

        when(repository.findEarliestDiagnosticByPatientId(5L)).thenReturn(Optional.of(diagnostic));

        Diagnostic result = service.findEarliestDiagnosticByPatientId(5L);

        assertThat(result.getId()).isEqualTo(12L);
        verify(repository).findEarliestDiagnosticByPatientId(5L);
    }

    @Test
    void findAllByPatientId_ReturnsList() {
        Diagnostic d1 = new Diagnostic();
        d1.setId(1L);
        Diagnostic d2 = new Diagnostic();
        d2.setId(2L);

        when(repository.findAllByPatientIdWithSpeechCard(99L)).thenReturn(List.of(d1, d2));

        List<Diagnostic> result = service.findAllByPatientId(99L);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(d1, d2);
        verify(repository).findAllByPatientIdWithSpeechCard(99L);
    }

    @Test
    void save_PersistsEntity() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setId(123L);

        when(repository.save(diagnostic)).thenReturn(diagnostic);

        Diagnostic result = service.save(diagnostic);

        assertThat(result).isEqualTo(diagnostic);
        verify(repository).save(diagnostic);
    }

    @Test
    void create_WhenSpeechCardNotFound_ReturnsError() throws Exception {
        Long speechCardId = 100L;
        DiagnosticDto dto = new DiagnosticDto(
                Timestamp.valueOf("2025-01-01 10:00:00"),
                null,
                speechCardId
        );

        when(speechCardRepository.findById(speechCardId))
                .thenReturn(Optional.empty());

        CompletableFuture<ServiceResult<DiagnosticReadDto>> resultFuture = service.create(dto);
        ServiceResult<DiagnosticReadDto> result = resultFuture.get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("Речевая карта не найдена");
    }

    @Test
    void update_WhenDiagnosticNotFound_ReturnsError() throws Exception {
        Long id = 999L;
        DiagnosticDto dto = new DiagnosticDto(
                Timestamp.valueOf("2026-01-01 10:00:00"),
                null,
                null
        );

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Диагностика не найдена"))
                    .thenThrow(new RuntimeException("Диагностика не найдена"));

            CompletableFuture<ServiceResult<DiagnosticReadDto>> resultFuture = service.update(id, dto);
            ServiceResult<DiagnosticReadDto> result = resultFuture.get();

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.message()).contains("Диагностика не найдена");
        }
    }

}
