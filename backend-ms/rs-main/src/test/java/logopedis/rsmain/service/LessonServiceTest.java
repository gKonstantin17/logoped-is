package logopedis.rsmain.service;

import logopedis.libentities.enums.LessonStatus;
import logopedis.libentities.kafka.LessonStatusDto;
import logopedis.libentities.rsmain.dto.homework.HomeworkDto;
import logopedis.libentities.rsmain.dto.lesson.LessonChangeDto;
import logopedis.libentities.rsmain.dto.lesson.LessonDto;
import logopedis.libentities.rsmain.dto.lesson.LessonWithHomeworkDto;
import logopedis.libentities.rsmain.dto.patient.PatientReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.*;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.rsmain.kafka.LessonNoteKafkaProducer;
import logopedis.rsmain.kafka.LessonNoteRecipientKafkaProducer;
import logopedis.rsmain.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    UserService userService;
    @Mock
    LessonNoteKafkaProducer kafkaTemplate;
    @Mock
    LessonNoteRecipientKafkaProducer lessonNoteRecipientKafkaProducer;

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
        lesson.setStatus(LessonStatus.IN_PROGRESS);
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
    void checkTime_WhenNoBusySlots_ReturnsAllSlots() throws Exception {
        Logoped logoped = new Logoped(); logoped.setId(UUID.randomUUID());
        when(patientService.findLogoped(1L)).thenReturn(logoped);
        when(repository.findByLogopedIdAndDateRange(any(), any(), any())).thenReturn(List.of());

        var result = service.checkTime(1L, Timestamp.valueOf("2025-01-01 12:00:00")).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().availableTime()).contains("10:00","19:00");
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

    @Test
    void findById_ReturnsLesson() {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson result = service.findById(1L);

        assertThat(result).isEqualTo(lesson);
        verify(repository).findById(1L);
    }

    @Test
    void findByLogopedId_ReturnsLessons() throws Exception {
        UUID logopedId = UUID.randomUUID();
        when(repository.findByLogopedId(logopedId)).thenReturn(List.of(lesson));

        var result = service.findByLogopedId(logopedId).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
    }

    @Test
    void findByIdWithFK_ReturnsDto() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        var result = service.findByIdWithFK(1L).get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(1L);
    }

    @Test
    void updateStatusFromKafka_UpdatesStatus() {
        Lesson existing = new Lesson();
        existing.setId(1L);
        existing.setStatus(LessonStatus.PLANNED);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,1L,"Занятие не найдено"))
                    .thenReturn(existing);

            LessonStatusDto dto = new LessonStatusDto(1L, LessonStatus.COMPLETED);
            service.updateStatusFromKafka(dto);

            assertThat(existing.getStatus()).isEqualTo(LessonStatus.COMPLETED);
            verify(repository).save(existing);
        }
    }

    @Test
    void updateStatusFromFE_UpdatesAndSendsKafka() throws Exception {
        Lesson existing = new Lesson();
        existing.setId(1L);
        existing.setStatus(LessonStatus.PLANNED);
        existing.setLogoped(new Logoped());

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,1L,"Занятие не найдено"))
                    .thenReturn(existing);

            when(repository.save(existing)).thenAnswer(inv -> inv.getArgument(0));

            // замокать Kafka или любые другие внешние вызовы внутри метода
            doNothing().when(kafkaTemplate).sendLessonNote(any());

            LessonStatusDto dto = new LessonStatusDto(1L, LessonStatus.CANCELED_BY_CLIENT);
            var result = service.updateStatusFromFE(dto).get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().status()).isEqualTo(LessonStatus.CANCELED_BY_CLIENT);

            verify(repository).save(existing);
            verify(kafkaTemplate).sendLessonNote(any());
        }
    }


    @Test
    void changeLesson_UpdatesHomework() throws Exception {
        Lesson existing = new Lesson();
        existing.setId(1L);
        existing.setHomework(null);
        existing.setLogoped(new Logoped());
        existing.setPatients(new HashSet<>());

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, 1L, "Занятие не найдено"))
                    .thenReturn(existing);

            LessonChangeDto dto = new LessonChangeDto(
                    1L, null, null, null, null,
                    new HomeworkDto("Новое задание")
            );

            // Мокаем сохранение домашки и сразу устанавливаем её в lesson
            when(homeworkService.save(any(Homework.class))).thenAnswer(inv -> {
                Homework h = inv.getArgument(0);
                h.setTask("Новое задание");
                existing.setHomework(h); // обновляем lesson
                return h;
            });

            // Мокаем save репозитория
            when(repository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

            // Мокаем Kafka
            doNothing().when(lessonNoteRecipientKafkaProducer).sendLessonNoteRecipient(any());

            var result = service.changeLesson(dto).get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().homework().task()).isEqualTo("Новое задание");

            verify(repository).save(any(Lesson.class));
            verify(homeworkService).save(any(Homework.class));
            verify(lessonNoteRecipientKafkaProducer).sendLessonNoteRecipient(any());
        }
    }





    @Test
    void findByPeriod_ReturnsLessons() {
        Timestamp start = Timestamp.valueOf("2025-01-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2025-01-02 00:00:00");
        when(repository.findByDateOfLessonBetween(start, end)).thenReturn(List.of(lesson));

        var result = service.findByPeriod(start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void createResponseInLessonNote_ReturnsLessonsForPeriod() throws Exception {
        Timestamp start = Timestamp.valueOf("2025-01-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2025-01-02 00:00:00");
        lesson.setLogoped(new Logoped());
        lesson.setPatients(Set.of(new Patient(){{
            setId(1L);
        }}));
        when(repository.findByDateOfLessonBetween(start, end)).thenReturn(List.of(lesson));
        when(userService.findByPatient(1L)).thenReturn(new UserData());

        var result = service.createResponseInLessonNote(start, end).get();

        assertThat(result).isNotNull();
        assertThat(result.list()).hasSize(1);
    }

    @Test
    void changeDate_WhenDateNotChanged_ReturnsError() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(lesson));

        var result = service.changeDate(1L, lesson.getDateOfLesson()).get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("Дата не изменена");
    }

    @Test
    void update_WhenPatientsNotFound_ReturnsError() throws Exception {
        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,1L,"Урок не найден"))
                    .thenReturn(lesson);

            LessonDto dto = new LessonDto("Индивид.","Тема","Описание",
                    lesson.getDateOfLesson(), null,null,List.of(1L));

            when(patientService.findAllById(dto.patientsId())).thenReturn(List.of());

            var result = service.update(1L, dto).get();

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.message()).contains("Некоторые пациенты не найдены");
        }
    }

    @Test
    void delete_WhenNotFound_ReturnsError() throws Exception {
        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,2L,"Занятие не найдено"))
                    .thenThrow(new RuntimeException("Занятие не найдено"));

            var result = service.delete(2L).get();

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.message()).isEqualTo("Занятие не найдено");
        }
    }

}

