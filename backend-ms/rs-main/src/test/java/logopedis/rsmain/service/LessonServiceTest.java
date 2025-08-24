package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.lesson.LessonDto;
import logopedis.libentities.rsmain.dto.lesson.LessonWithHomeworkDto;
import logopedis.libentities.rsmain.dto.patient.PatientReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Homework;
import logopedis.libentities.rsmain.entity.Lesson;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.rsmain.repository.LessonRepository;
import logopedis.rsmain.service.LogopedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    LessonRepository repository;
    @Mock
    LogopedService logopedService;
    @Mock
    HomeworkService homeworkService;
    @Mock
    PatientService patientService;

    @InjectMocks
    LessonService service;

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = new Lesson();
        lesson.setId(1L);
        lesson.setType("Индивидуальное");
        lesson.setTopic("Звуки");
        lesson.setDescription("Тестовое занятие");
        lesson.setDateOfLesson(Timestamp.valueOf("2025-01-01 10:00:00"));
        lesson.setStatus("Активно");
    }


    @Test
    void findall_ReturnsAllLessons() throws Exception {
        when(repository.findAll()).thenReturn(List.of(lesson));

        var result = service.findall().get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).topic()).isEqualTo("Звуки");
    }


    @Test
    void findByUserId_WhenPatientsFound_ReturnsLessons() throws Exception {
        UUID userId = UUID.randomUUID();

        PatientReadDto patientDto = new PatientReadDto(1L,"Иван","Иванов",null,null,null,false);
        when(patientService.findByUserId(userId))
                .thenReturn(CompletableFuture.completedFuture(ServiceResult.success(List.of(patientDto))));
        when(repository.findByPatientsId(1L)).thenReturn(List.of(lesson));

        var result = service.findByUserId(userId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
    }

    @Test
    void findByUserId_WhenNoPatients_ReturnsError() throws Exception {
        UUID userId = UUID.randomUUID();
        when(patientService.findByUserId(userId))
                .thenReturn(CompletableFuture.completedFuture(ServiceResult.success(List.of())));

        var result = service.findByUserId(userId).get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Пациенты не найдены");
    }

    @Test
    void createLessonWithHomework_WithLogopedAndHomework_Success() throws Exception {
        UUID logopedId = UUID.randomUUID();
        Logoped logoped = new Logoped();
        logoped.setId(logopedId);

        Homework hw = new Homework();
        hw.setId(5L);
        hw.setTask("Задание");

        Lesson savedLesson = new Lesson();
        savedLesson.setId(1L);
        savedLesson.setLogoped(logoped);
        savedLesson.setHomework(hw);

        when(logopedService.findById(logopedId)).thenReturn(Optional.of(logoped));
        when(homeworkService.create("Задание")).thenReturn(hw);
        when(repository.save(any(Lesson.class))).thenAnswer(inv -> {
            Lesson l = inv.getArgument(0);
            l.setId(1L);
            return l;
        });
        when(repository.findById(1L)).thenReturn(Optional.of(savedLesson));

        LessonWithHomeworkDto dto = new LessonWithHomeworkDto(
                "Групповое",
                "Тема",
                "Описание",
                Timestamp.valueOf("2025-01-01 11:00:00"),
                logopedId,
                "Задание",
                List.of(1L)
        );

        var result = service.createLessonWithHomework(dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isNotNull();
        assertThat(result.data().homeworkId()).isEqualTo(5L);

        verify(repository).save(any(Lesson.class));
    }


    @Test
    void checkTime_WhenNoBusySlots_ReturnsAllSlots() throws Exception {
        Logoped logoped = new Logoped(); logoped.setId(UUID.randomUUID());
        when(patientService.findLogoped(1L)).thenReturn(logoped);
        when(repository.findByLogopedIdAndDateRange(any(), any(), any())).thenReturn(List.of());

        var result = service.checkTime(1L, Timestamp.valueOf("2025-01-01 12:00:00")).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().availableTime()).contains("10:00","19:00");
    }

    @Test
    void canselLesson_SetsStatusCancelled() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        var result = service.canselLesson(1L).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().getStatus()).isEqualTo("Отменено");
    }

    @Test
    void changeDate_UpdatesDate() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        Timestamp newDate = Timestamp.valueOf("2025-01-02 15:00:00");
        var result = service.changeDate(1L, newDate).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().getDateOfLesson()).isEqualTo(newDate);
    }

    @Test
    void update_ChangesTopicAndHomework() throws Exception {
        Homework hw = new Homework(); hw.setId(10L);
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));
        when(homeworkService.findById(10L)).thenReturn(Optional.of(hw));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LessonDto dto = new LessonDto("Индивид.","Новая тема","Описание",
                Timestamp.valueOf("2025-01-03 14:00:00"), null,10L,null);

        var result = service.update(1L, dto).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().topic()).isEqualTo("Новая тема");
        assertThat(result.data().homeworkId()).isEqualTo(10L);
    }

    @Test
    void delete_RemovesLesson() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        var result = service.delete(1L).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isEqualTo(1L);
        verify(repository).deleteById(1L);
    }

}

