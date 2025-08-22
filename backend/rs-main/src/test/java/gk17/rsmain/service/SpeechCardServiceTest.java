package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.dto.speechCard.SCFromDiagnosticDto;
import gk17.rsmain.dto.speechCard.SpeechCardDto;
import gk17.rsmain.dto.speechCard.SpeechCardFullDto;
import gk17.rsmain.dto.speechCard.SpeechCardReadDto;
import gk17.rsmain.entity.*;
import gk17.rsmain.repository.*;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechCardServiceTest {

    @Mock
    private SpeechCardRepository repository;
    @Mock
    private SoundCorrectionRepository soundCorrectionRepository;
    @Mock
    private SpeechErrorRepository speechErrorRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private DiagnosticRepository diagnosticRepository;

    @InjectMocks
    private SpeechCardService service;

    @Test
    void findall_ReturnsList() throws ExecutionException, InterruptedException {
        SpeechCard card = new SpeechCard();
        card.setId(1L);
        when(repository.findAll()).thenReturn(List.of(card));

        CompletableFuture<ServiceResult<List<SpeechCardReadDto>>> future = service.findall();
        ServiceResult<List<SpeechCardReadDto>> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).id()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test
    void create_ReturnsSavedCard() throws ExecutionException, InterruptedException {
        SpeechCardDto dto = new SpeechCardDto("Причина", "Состояние", "Анамнез", "GM", "FM", "Артик", "SR", "SC", "SPC", "PC", List.of(1L), List.of(1L));
        SpeechError error = new SpeechError(); error.setId(1L);
        SoundCorrection correction = new SoundCorrection(); correction.setId(1L);

        when(speechErrorRepository.findAllById(dto.speechErrors())).thenReturn(List.of(error));
        when(soundCorrectionRepository.findAllById(dto.soundCorrections())).thenReturn(List.of(correction));
        when(repository.save(any(SpeechCard.class))).thenAnswer(inv -> inv.getArgument(0));

        CompletableFuture<ServiceResult<SpeechCardReadDto>> future = service.create(dto);
        ServiceResult<SpeechCardReadDto> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().speechErrors()).containsExactly(1L);
        assertThat(result.data().soundCorrections()).containsExactly(1L);
        verify(repository).save(any(SpeechCard.class));
    }
    @Test
    void createFromDiag_ReturnsSavedCard() throws ExecutionException, InterruptedException {
        // Подготовка данных
        Long lessonId = 1L;
        UUID logopedId = UUID.randomUUID();
        SCFromDiagnosticDto dto = new SCFromDiagnosticDto(
                "Причина",
                "Состояние",
                "Анамнез",
                "GM",
                "FM",
                "Артик",
                "SR",
                "SC",
                "SPC",
                "PC",
                List.of(1L),   // speechErrors
                List.of(new SoundCorrectionDto("S","C")), // soundCorrections
                lessonId,
                logopedId
        );

        // Мок speechErrors
        SpeechError error = new SpeechError();
        error.setId(1L);
        when(speechErrorRepository.findAllById(dto.speechErrors())).thenReturn(List.of(error));

        // Мок soundCorrections
        SoundCorrection correction = new SoundCorrection();
        correction.setId(1L);
        when(soundCorrectionRepository.findBySoundAndCorrection("S", "C"))
                .thenReturn(Optional.of(correction));

        // Мок сохранения карты
        when(repository.save(any(SpeechCard.class))).thenAnswer(inv -> inv.getArgument(0));

        // Мок урока
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // Мок сохранения диагностики
        when(diagnosticRepository.save(any(Diagnostic.class))).thenAnswer(inv -> inv.getArgument(0));

        // Вызов сервиса
        CompletableFuture<ServiceResult<SpeechCardReadDto>> future = service.createFromDiag(dto);
        ServiceResult<SpeechCardReadDto> result = future.get();

        // Проверка
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().speechErrors()).containsExactly(1L);
        assertThat(result.data().soundCorrections()).containsExactly(1L);
        assertThat(result.data().reason()).isEqualTo("Причина");

        // Верификация вызовов
        verify(speechErrorRepository).findAllById(dto.speechErrors());
        verify(soundCorrectionRepository).findBySoundAndCorrection("S", "C");
        verify(repository).save(any(SpeechCard.class));
        verify(lessonRepository).findById(lessonId);
        verify(diagnosticRepository).save(any(Diagnostic.class));
    }

    @Test
    void update_ReturnsUpdatedCard() throws ExecutionException, InterruptedException {
        Long id = 1L;
        SpeechCard existing = new SpeechCard();
        existing.setId(id);

        when(repository.save(any(SpeechCard.class))).thenAnswer(inv -> inv.getArgument(0));

        SpeechCardDto dto = new SpeechCardDto(
                "Новая причина", // reason
                null,            // stateOfHearning
                null,            // anamnesis
                null,            // generalMotor
                null,            // fineMotor
                null,            // articulatory
                null,            // soundReproduction
                null,            // soundComponition
                null,            // speechChars
                null,            // patientChars
                null,            // speechErrors
                null             // soundCorrections
        );


        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Речевая карта не найдена"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<SpeechCardReadDto>> future = service.update(id, dto);
            ServiceResult<SpeechCardReadDto> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().reason()).isEqualTo("Новая причина");
            verify(repository).save(existing);
        }
    }

    @Test
    void delete_ReturnsId() throws ExecutionException, InterruptedException {
        Long id = 1L;
        SpeechCard existing = new SpeechCard();
        existing.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Речевая карта не найдена"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> future = service.delete(id);
            ServiceResult<Long> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);
            verify(repository).deleteById(id);
        }
    }

    @Test
    void findByPatientId_ReturnsFullDto() throws Exception {
        Long patientId = 1L;
        SpeechCard card = new SpeechCard();
        card.setId(10L);

        Diagnostic diag = new Diagnostic();
        diag.setSpeechCard(card);
        Lesson lesson = new Lesson();
        Logoped logoped = new Logoped();
        logoped.setFirstName("Логопед"); logoped.setLastName("Тест");
        lesson.setLogoped(logoped);
        Patient patient = new Patient(); patient.setId(patientId); patient.setFirstName("Пациент"); patient.setLastName("Тест"); patient.setDateOfBirth(Timestamp.valueOf("2025-01-01 10:00:00"));
        lesson.setPatients(Set.of(patient));
        diag.setLesson(lesson);

        when(repository.findDetailedByPatientId(patientId)).thenReturn(Optional.of(card));
        when(diagnosticRepository.findBySpeechCard(card)).thenReturn(Optional.of(diag));

        CompletableFuture<ServiceResult<SpeechCardFullDto>> future = service.findByPatientId(patientId);
        ServiceResult<SpeechCardFullDto> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(10L);
        assertThat(result.data().logopedFirstName()).isEqualTo("Логопед");
    }
}

