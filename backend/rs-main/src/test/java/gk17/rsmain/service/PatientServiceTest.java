package gk17.rsmain.service;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientReadDto;
import gk17.rsmain.dto.patient.PatientWithSpeechCard;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.*;
import gk17.rsmain.repository.PatientRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private LogopedService logopedService;

    @InjectMocks
    private PatientService service;

    @Test
    void findAll_ReturnsMappedDtos() throws Exception {
        // given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Иван");
        patient.setLastName("Иванов");
        patient.setDateOfBirth(Timestamp.valueOf("2010-01-01 00:00:00"));
        patient.setHidden(false);

        when(repository.findAll()).thenReturn(List.of(patient));

        // when
        ServiceResult<List<PatientReadDto>> result = service.findall().get();

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).firstName()).isEqualTo("Иван");

        verify(repository).findAll();
    }

    @Test
    void create_SavesPatientAndReturnsDto() throws Exception {
        UUID userId = UUID.randomUUID();
        PatientCreateDto dto = new PatientCreateDto("Иван", "Иванов", Timestamp.valueOf("2010-01-01 00:00:00"), userId);

        UserData user = new UserData();
        user.setId(userId);

        Patient saved = new Patient();
        saved.setId(1L);
        saved.setFirstName("Иван");
        saved.setLastName("Иванов");
        saved.setDateOfBirth(Timestamp.valueOf("2010-01-01 00:00:00"));
        saved.setUser(user);

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any(Patient.class))).thenReturn(saved);

        ServiceResult<PatientReadDto> result = service.create(dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().firstName()).isEqualTo("Иван");
        assertThat(result.data().userId()).isEqualTo(userId);

        verify(repository).save(any(Patient.class));
    }

    @Test
    void hide_SetsHiddenTrue() throws Exception {
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setHidden(false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        ServiceResult<PatientReadDto> result = service.hide(1L).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().isHidden()).isTrue();

        verify(repository).save(existing);
    }

    @Test
    void delete_RemovesPatientById() throws Exception {
        Patient existing = new Patient();
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        ServiceResult<Long> result = service.delete(1L).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isEqualTo(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void findAllWithSC_ReturnsMappedPatientWithSpeechCard() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Иван");
        patient.setLastName("Иванов");
        patient.setDateOfBirth(Timestamp.valueOf("2010-01-01 00:00:00"));
        patient.setHidden(false);

        // создаём SpeechCard с ошибкой и коррекцией
        SpeechError error = new SpeechError();
        error.setTitle("Кортавость");
        error.setDescription("Не произносит звук R");

        SoundCorrection correction = new SoundCorrection();
        correction.setSound("S");
        correction.setCorrection("Замена на Ш");

        SpeechCard sc = new SpeechCard();
        sc.setSpeechErrors(Set.of(error));
        sc.setSoundCorrections(Set.of(correction));

        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setSpeechCard(sc);

        Lesson lesson = new Lesson();
        lesson.setDiagnostic(diagnostic);

        patient.setLessons(List.of(lesson));

        when(repository.findAllWithSpeechData(userId)).thenReturn(List.of(patient));

        // when
        ServiceResult<List<PatientWithSpeechCard>> result = service.findAllWithSC(userId).get();

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);

        PatientWithSpeechCard dto = result.data().get(0);
        assertThat(dto.firstName()).isEqualTo("Иван");
        assertThat(dto.speechErrors()).extracting("title").containsExactly("Кортавость");
        assertThat(dto.soundCorrections()).extracting("sound").containsExactly("S");

        verify(repository).findAllWithSpeechData(userId);
    }

    @Test
    void findAllWithSC_WhenRepositoryThrows_ReturnsError() throws Exception {
        UUID userId = UUID.randomUUID();
        when(repository.findAllWithSpeechData(userId)).thenThrow(new RuntimeException("DB error"));

        ServiceResult<List<PatientWithSpeechCard>> result = service.findAllWithSC(userId).get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("DB error");
    }
    @Test
    void update_ChangesFirstNameOnly() throws Exception {
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setFirstName("Старое");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientDto dto = new PatientDto("Новое", null, null, null, null);

        ServiceResult<PatientReadDto> result = service.update(1L, dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().firstName()).isEqualTo("Новое");

        verify(repository).save(existing);
    }

    @Test
    void update_ChangesUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserData user = new UserData();
        user.setId(userId);

        Patient existing = new Patient();
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientDto dto = new PatientDto(null, null, null, userId, null);

        ServiceResult<PatientReadDto> result = service.update(1L, dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().userId()).isEqualTo(userId);

        verify(userService).findById(userId);
        verify(repository).save(existing);
    }

    @Test
    void update_ChangesLogoped() throws Exception {
        UUID logopedId = UUID.randomUUID();
        Logoped logoped = new Logoped();
        logoped.setId(logopedId);

        Patient existing = new Patient();
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(logopedService.findById(logopedId)).thenReturn(Optional.of(logoped));
        when(repository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientDto dto = new PatientDto(null, null, null, null, logopedId);

        ServiceResult<PatientReadDto> result = service.update(1L, dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().logopedId()).isEqualTo(logopedId);

        verify(logopedService).findById(logopedId);
        verify(repository).save(existing);
    }

    @Test
    void update_WhenPatientNotFound_ReturnsError() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        PatientDto dto = new PatientDto("Иван", "Иванов", Timestamp.valueOf("2010-01-01 00:00:00"), null, null);

        ServiceResult<PatientReadDto> result = service.update(99L, dto).get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Пациент не найден");
    }


}

