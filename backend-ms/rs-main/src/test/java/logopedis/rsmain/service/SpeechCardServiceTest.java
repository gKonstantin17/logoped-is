package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.patient.PatientReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.speechCard.*;
import logopedis.libentities.rsmain.entity.*;
import logopedis.rsmain.repository.*;
import logopedis.libutils.hibernate.ResponseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;
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
    @Mock
    private DiagnosticService diagnosticService;
    @Mock
    private PatientService patientService;

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
    void findFullById_ReturnsFullDto() throws Exception {
        Long cardId = 1L;
        SpeechCard card = new SpeechCard(); card.setId(cardId);

        Diagnostic diag = new Diagnostic();
        diag.setSpeechCard(card);
        Lesson lesson = new Lesson();
        Logoped logoped = new Logoped(); logoped.setFirstName("Имя"); logoped.setLastName("Фамилия");
        lesson.setLogoped(logoped);
        Patient patient = new Patient(); patient.setId(5L); patient.setFirstName("Пациент");
        lesson.setPatients(Set.of(patient));
        diag.setLesson(lesson);
        when(diagnosticService.findBySpeechCard(any(SpeechCard.class))).thenReturn(diag);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));
        when(diagnosticService.findBySpeechCard(card)).thenReturn(diag);

        var result = service.findFullById(cardId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().logopedFirstName()).isEqualTo("Имя");
    }

    @Test
    void findAllPatientsFirstCards_ReturnsList() throws Exception {
        UUID logopedId = UUID.randomUUID();
        PatientReadDto patientDto = new PatientReadDto(1L,"Имя","Фам",new Timestamp(System.currentTimeMillis()), UUID.randomUUID(),UUID.randomUUID(),false);

        when(patientService.findByLogopegId(logopedId))
                .thenReturn(CompletableFuture.completedFuture(ServiceResult.success(List.of(patientDto))));

        SpeechCard card = new SpeechCard(); card.setId(10L);
        when(repository.findEarliestSpeechCardByPatientId(1L)).thenReturn(Optional.of(card));

        Diagnostic diag = new Diagnostic(); diag.setDate(new Timestamp(System.currentTimeMillis()));
        when(diagnosticService.findEarliestDiagnosticByPatientId(1L)).thenReturn(diag);

        Patient patient = new Patient(); patient.setId(1L); patient.setFirstName("Имя"); patient.setLastName("Фам");
        when(patientService.findById(1L)).thenReturn(patient);

        var result = service.findAllPatientsFirstCards(logopedId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).patientName()).contains("Имя");
    }

    @Test
    void createUpdateWithCorrctions_CreatesNewCard() throws Exception {
        Long patientId = 1L;
        Long lessonId = 2L;

        SpeechCard lastCard = new SpeechCard(); lastCard.setId(100L);
        when(repository.findLatestSpeechCardByPatientId(patientId)).thenReturn(Optional.of(lastCard));

        Lesson lesson = new Lesson(); lesson.setId(lessonId);
        Logoped logoped = new Logoped(); logoped.setFirstName("Имя"); logoped.setLastName("Фамилия");
        lesson.setLogoped(logoped);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        Patient patient = new Patient(); patient.setId(patientId); patient.setFirstName("Пациент");
        when(patientService.findById(patientId)).thenReturn(patient);

        SoundCorrection sc = new SoundCorrection(); sc.setId(5L); sc.setSound("S"); sc.setCorrection("C");
        when(soundCorrectionRepository.findBySoundAndCorrection("S","C")).thenReturn(Optional.of(sc));

        when(repository.save(any(SpeechCard.class))).thenAnswer(inv -> {
            SpeechCard c = inv.getArgument(0);
            c.setId(200L);
            return c;
        });
        when(diagnosticService.save(any(Diagnostic.class))).thenAnswer(inv -> inv.getArgument(0));

        SpeechCardCorrectionDto dto = new SpeechCardCorrectionDto(
                patientId,
                List.of(new SoundCorrectionDto("S","C")),
                lessonId
        );
        var result = service.createUpdateWithCorrctions(dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(200L);
        assertThat(result.data().logopedFirstName()).isEqualTo("Имя");
    }

    @Test
    void findPatientHistory_ReturnsChanges() throws Exception {
        Long patientId = 1L;
        Diagnostic d1 = new Diagnostic(); d1.setDate(new Timestamp(1000));
        SpeechCard c1 = new SpeechCard();
        SpeechError e1 = new SpeechError(); e1.setTitle("Ошибка1");
        c1.setSpeechErrors(Set.of(e1));
        d1.setSpeechCard(c1);

        Diagnostic d2 = new Diagnostic(); d2.setDate(new Timestamp(2000));
        SpeechCard c2 = new SpeechCard();
        SpeechError e2 = new SpeechError(); e2.setTitle("Ошибка2");
        c2.setSpeechErrors(Set.of(e2));
        d2.setSpeechCard(c2);

        when(diagnosticService.findAllByPatientId(patientId)).thenReturn(List.of(d1,d2));

        var result = service.findPatientHistory(patientId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(2);
        assertThat(result.data().get(0).speechErrors()).contains("Ошибка1");
        assertThat(result.data().get(1).speechErrors()).contains("Ошибка2");
    }
}

