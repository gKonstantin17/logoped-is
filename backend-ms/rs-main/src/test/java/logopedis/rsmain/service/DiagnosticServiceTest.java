package logopedis.rsmain.service;

import logopedis.rsmain.dto.diagnostic.DiagnosticDto;
import logopedis.rsmain.dto.diagnostic.DiagnosticReadDto;
import logopedis.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.rsmain.entity.Diagnostic;
import logopedis.rsmain.entity.Lesson;
import logopedis.rsmain.entity.SpeechCard;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
import logopedis.rsmain.service.DiagnosticService;
import logopedis.rsmain.service.LessonService;
import logopedis.rsmain.service.SpeechCardService;
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
    void create_ReturnsDto() throws ExecutionException, InterruptedException {
        Long lessonId = 1L;
        Long speechCardId = 1L;
        Timestamp date = Timestamp.valueOf("2025-01-01 10:00:00");
        DiagnosticDto dto = new DiagnosticDto(date,lessonId,speechCardId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        when(lessonService.findById(lessonId)).thenReturn(lesson);

        SpeechCard speechCard = new SpeechCard();
        speechCard.setId(speechCardId);
        when(speechCardService.findById(speechCardId)).thenReturn(speechCard);

        when(repository.save(any(Diagnostic.class))).thenAnswer(inv  -> {
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
        verify(speechCardService).findById(dto.speechCardId());
    }
    @Test
    void update_ReturnsDto() throws ExecutionException, InterruptedException {
        Long id = 1L;
        Timestamp date = Timestamp.valueOf("2026-01-01 10:00:00");
        Long lessonId = 2L;
        Long speechCardId = 2L;
        DiagnosticDto dto = new DiagnosticDto(date,lessonId,speechCardId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        SpeechCard speechCard = new SpeechCard();
        speechCard.setId(speechCardId);

        Diagnostic existing = new Diagnostic();
        existing.setId(1L);
        existing.setLesson(lesson);
        existing.setSpeechCard(speechCard);
        existing.setDate(Timestamp.valueOf("2025-01-01 10:00:00"));


        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(()->ResponseHelper.findById(repository,id,"Диагностика не найдена"))
                    .thenReturn(existing);

            when(lessonService.findById(lessonId)).thenReturn(lesson);
            when(speechCardService.findById(speechCardId)).thenReturn(speechCard);
            when(repository.save(any(Diagnostic.class))).thenAnswer(inv -> inv.getArgument(0));
            when(repository.findById(existing.getId())).thenReturn(Optional.of(existing));

            CompletableFuture<ServiceResult<DiagnosticReadDto>> resultFuture = service.update(id,dto);
            ServiceResult<DiagnosticReadDto> result = resultFuture.get();

            assertThat(result.data().id()).isEqualTo(id);
            assertThat(result.data().date()).isEqualTo(date);
            assertThat(result.data().lessonId()).isEqualTo(lessonId);
            assertThat(result.data().speechCardId()).isEqualTo(speechCardId);

            verify(repository).save(any(Diagnostic.class));
            verify(lessonService).findById(dto.lessonId());
            verify(speechCardService).findById(dto.speechCardId());
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
}
