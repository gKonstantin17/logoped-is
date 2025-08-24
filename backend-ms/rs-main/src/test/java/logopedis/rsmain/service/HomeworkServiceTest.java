package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.homework.HomeworkDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Homework;
import logopedis.rsmain.repository.HomeworkRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
import logopedis.rsmain.service.HomeworkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeworkServiceTest {
    @Mock
    private HomeworkRepository repository;
    @InjectMocks
    private HomeworkService service;

    @Test
    void findall_ReturnsList() throws ExecutionException, InterruptedException {
        List<Homework> list = List.of(new Homework());
        when(repository.findAll()).thenReturn(list);

        CompletableFuture<ServiceResult<List<Homework>>> resultFuture = service.findall();
        ServiceResult<List<Homework>> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);

        verify(repository).findAll();
    }
    @Test
    void findById_ReturnsOptional(){
        Long id = 1L;
        Homework homework = new Homework();

        when(repository.findById(id)).thenReturn(Optional.of(homework));
        Optional<Homework> result = service.findById(id);

        assertThat(result).isPresent();
        verify(repository).findById(id);
    }
    @Test
    void create_ReturnsSavedHomework(){
        Long id = 1L;
        String task = "Задача";
        when(repository.save(any(Homework.class))).thenAnswer(inv -> {
            Homework h = inv.getArgument(0);
            h.setId(id);
            return h;
        });


        Homework result = service.create(task);

        assertThat(result).isNotNull();
        assertThat(result.getTask()).isEqualTo(task);
        assertThat(result.getId()).isEqualTo(id);

        verify(repository).save(any(Homework.class));
    }
    @Test
    void update_ReturnsUpdatedHomework() throws ExecutionException, InterruptedException {
        Long id = 1L;
        HomeworkDto newHM = new HomeworkDto("НоваяЗадача");
        Homework existing = new Homework();
        existing.setId(id);
        existing.setTask("Задача");

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(()->ResponseHelper.findById(repository,id,"Домашняя работа не найдена"))
                    .thenReturn(existing);
            when(repository.save(any(Homework.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<Homework>> resultFuture = service.update(id,newHM);
            ServiceResult<Homework> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().getTask()).isEqualTo("НоваяЗадача");

            verify(repository).save(any(Homework.class));
        }

    }
    @Test
    void delete_ReturnsIdOfDeleted() throws ExecutionException, InterruptedException {
        Long id = 1L;
        Homework existing = new Homework();
        existing.setId(id);
        existing.setTask("Задача");

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(()->ResponseHelper.findById(repository,id,"Домашняя работа не найдена"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> resultFuture = service.delete(id);
            ServiceResult<Long> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }
}
