package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.speechError.SpeechErrorDto;
import gk17.rsmain.dto.speechError.SpeechErrorReadDto;
import gk17.rsmain.entity.SpeechError;
import gk17.rsmain.repository.SpeechErrorRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechErrorServiceTest {

    @Mock
    private SpeechErrorRepository repository;

    @InjectMocks
    private SpeechErrorService service;

    @Test
    void findall_ReturnsList() throws ExecutionException, InterruptedException {
        SpeechError error = new SpeechError();
        error.setId(1L);
        error.setTitle("Ошибка 1");
        error.setDescription("Описание 1");

        when(repository.findAll()).thenReturn(List.of(error));

        CompletableFuture<ServiceResult<List<SpeechErrorReadDto>>> future = service.findall();
        ServiceResult<List<SpeechErrorReadDto>> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).id()).isEqualTo(1L);
        assertThat(result.data().get(0).title()).isEqualTo("Ошибка 1");
        verify(repository).findAll();
    }

    @Test
    void create_ReturnsSavedError() throws ExecutionException, InterruptedException {
        SpeechErrorDto dto = new SpeechErrorDto("Новая ошибка", "Новое описание");
        SpeechError saved = new SpeechError();
        saved.setId(1L);
        saved.setTitle(dto.title());
        saved.setDescription(dto.description());

        when(repository.save(any(SpeechError.class))).thenReturn(saved);

        CompletableFuture<ServiceResult<SpeechErrorReadDto>> future = service.create(dto);
        ServiceResult<SpeechErrorReadDto> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(1L);
        assertThat(result.data().title()).isEqualTo("Новая ошибка");
        assertThat(result.data().description()).isEqualTo("Новое описание");
        verify(repository).save(any(SpeechError.class));
    }

    @Test
    void update_ReturnsUpdatedError() throws ExecutionException, InterruptedException {
        Long id = 1L;
        SpeechError existing = new SpeechError();
        existing.setId(id);
        existing.setTitle("Старая ошибка");
        existing.setDescription("Старое описание");

        SpeechErrorDto dto = new SpeechErrorDto("Обновлённая ошибка", "Обновлённое описание");

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Речевая ошибка не найдена"))
                    .thenReturn(existing);

            when(repository.save(any(SpeechError.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<SpeechErrorReadDto>> future = service.update(id, dto);
            ServiceResult<SpeechErrorReadDto> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().id()).isEqualTo(id);
            assertThat(result.data().title()).isEqualTo("Обновлённая ошибка");
            assertThat(result.data().description()).isEqualTo("Обновлённое описание");

            verify(repository).save(existing);
        }
    }

    @Test
    void delete_ReturnsId() throws ExecutionException, InterruptedException {
        Long id = 1L;
        SpeechError existing = new SpeechError();
        existing.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Речевая ошибка не найдена"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> future = service.delete(id);
            ServiceResult<Long> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);
            verify(repository).deleteById(id);
        }
    }
}
