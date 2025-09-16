package logopedis.rsmain.service;


import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionReadDto;
import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.libentities.rsmain.entity.SoundCorrection;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.rsmain.repository.SoundCorrectionRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundCorrectionServiceTest {

    @Mock
    private SoundCorrectionRepository repository;

    @Mock
    private DiagnosticRepository diagnosticRepository;

    @InjectMocks
    private SoundCorrectionService service;

    @Test
    void findall_ReturnsList() throws Exception {
        SoundCorrection sc = new SoundCorrection();
        sc.setId(1L);
        sc.setSound("S");
        sc.setCorrection("C");

        when(repository.findAll()).thenReturn(List.of(sc));

        CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> future = service.findall();
        ServiceResult<List<SoundCorrectionReadDto>> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).id()).isEqualTo(1L);
        assertThat(result.data().get(0).sound()).isEqualTo("S");
        assertThat(result.data().get(0).correction()).isEqualTo("C");

        verify(repository).findAll();
    }

    @Test
    void create_ReturnsSavedCorrection() throws Exception {
        SoundCorrectionDto dto = new SoundCorrectionDto("S", "C");
        SoundCorrection saved = new SoundCorrection();
        saved.setId(1L);
        saved.setSound(dto.sound());
        saved.setCorrection(dto.correction());

        when(repository.save(any(SoundCorrection.class))).thenReturn(saved);

        CompletableFuture<ServiceResult<SoundCorrectionReadDto>> future = service.create(dto);
        ServiceResult<SoundCorrectionReadDto> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(1L);
        assertThat(result.data().sound()).isEqualTo("S");
        assertThat(result.data().correction()).isEqualTo("C");

        verify(repository).save(any(SoundCorrection.class));
    }

    @Test
    void update_ReturnsUpdatedCorrection() throws Exception {
        Long id = 1L;
        SoundCorrection existing = new SoundCorrection();
        existing.setId(id);
        existing.setSound("Old");
        existing.setCorrection("OldC");

        SoundCorrectionDto dto = new SoundCorrectionDto("New", "NewC");

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Направление коррекции не найдено"))
                    .thenReturn(existing);

            when(repository.save(any(SoundCorrection.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<SoundCorrectionReadDto>> future = service.update(id, dto);
            ServiceResult<SoundCorrectionReadDto> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().id()).isEqualTo(id);
            assertThat(result.data().sound()).isEqualTo("New");
            assertThat(result.data().correction()).isEqualTo("NewC");

            verify(repository).save(existing);
        }
    }

    @Test
    void delete_ReturnsId() throws Exception {
        Long id = 1L;
        SoundCorrection existing = new SoundCorrection();
        existing.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Направление коррекции не найдено"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> future = service.delete(id);
            ServiceResult<Long> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }

    @Test
    void findLatestByPatientId_ReturnsCorrections() throws Exception {
        Long patientId = 1L;
        SoundCorrection sc = new SoundCorrection();
        sc.setId(1L);
        sc.setSound("S");
        sc.setCorrection("C");

        when(repository.findLatestSoundCorrectionsByPatientId(patientId))
                .thenReturn(Optional.of(Set.of(sc)));

        CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> future =
                service.findLatestByPatientId(patientId);
        ServiceResult<List<SoundCorrectionReadDto>> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).sound()).isEqualTo("S");

        verify(repository).findLatestSoundCorrectionsByPatientId(patientId);
    }

    @Test
    void findLatestByPatientId_WhenEmpty_ReturnsEmptyList() throws Exception {
        Long patientId = 1L;
        when(repository.findLatestSoundCorrectionsByPatientId(patientId))
                .thenReturn(Optional.empty());

        var result = service.findLatestByPatientId(patientId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isEmpty();
    }

    @Test
    void findChanges_WhenNoDiagnostic_ReturnsEmptyChanges() throws Exception {
        Long lessonId = 1L;
        when(diagnosticRepository.findByLessonId(lessonId)).thenReturn(Optional.empty());

        var result = service.findChanges(lessonId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().added()).isEmpty();
        assertThat(result.data().removed()).isEmpty();
    }

    @Test
    void findChanges_WhenPreviousExists_ComputesAddedAndRemoved() throws Exception {
        Long lessonId = 1L;

        // предыдущая диагностика
        SoundCorrection before = new SoundCorrection();
        before.setId(1L);
        before.setSound("B");
        before.setCorrection("Before");

        // текущая диагностика
        SoundCorrection after = new SoundCorrection();
        after.setId(2L);
        after.setSound("A");
        after.setCorrection("After");

        Diagnostic prev = new Diagnostic();
        prev.setId(10L);
        var prevSC = new logopedis.libentities.rsmain.entity.SpeechCard();
        prevSC.setSoundCorrections(Set.of(before));
        prev.setSpeechCard(prevSC);

        Diagnostic curr = new Diagnostic();
        curr.setId(20L);
        var currSC = new logopedis.libentities.rsmain.entity.SpeechCard();
        currSC.setSoundCorrections(Set.of(after));
        curr.setSpeechCard(currSC);

        // подставляем пациента
        var patient = new logopedis.libentities.rsmain.entity.Patient();
        patient.setId(5L);
        var lesson = new logopedis.libentities.rsmain.entity.Lesson();
        lesson.setPatients(Set.of(patient));
        curr.setLesson(lesson);

        when(diagnosticRepository.findByLessonId(lessonId)).thenReturn(Optional.of(curr));
        when(diagnosticRepository.findPreviousByPatientIdAndDate(eq(5L), any()))
                .thenReturn(List.of(prev));

        var result = service.findChanges(lessonId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().added()).hasSize(1);
        assertThat(result.data().removed()).hasSize(1);
        assertThat(result.data().added().iterator().next().sound()).isEqualTo("A");
        assertThat(result.data().removed().iterator().next().sound()).isEqualTo("B");
    }

}
