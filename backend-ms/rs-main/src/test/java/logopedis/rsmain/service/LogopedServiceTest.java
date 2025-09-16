package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.logoped.LogopedDto;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.user.BaseUserDto;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.rsmain.repository.LogopedRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.libutils.keycloak.KeycloakAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogopedServiceTest {
    @Mock
    private KeycloakAdminService kcService;
    @Mock
    private LogopedRepository repository;
    @InjectMocks
    private LogopedService service;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void findAll_ReturnList() throws Exception  {
        List<Logoped> list = List.of(new Logoped());
        when(repository.findAll()).thenReturn(list);

        CompletableFuture<ServiceResult<List<Logoped>>> resultFuture = service.findall();
        ServiceResult<List<Logoped>> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void findById_ReturnsOptional() {
        UUID id = UUID.randomUUID();
        Logoped logoped = new Logoped();
        when(repository.findById(id)).thenReturn(Optional.of(logoped));

        Optional<Logoped> result = service.findById(id);

        assertThat(result).isPresent();
        verify(repository).findById(id);

    }

    @Test
    void create_Success() throws ExecutionException, InterruptedException {
        BaseUserDto dto = new LogopedDto(UUID.randomUUID(),"Иван","Иванов","ivan@gmail.com","+79123456789");
        Logoped saved = new Logoped();
        saved.setId(UUID.randomUUID());

        when(repository.save(any(Logoped.class))).thenReturn(saved);
        CompletableFuture<ServiceResult<Logoped>> resultFuture = service.create(dto);
        ServiceResult<Logoped> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().getId()).isEqualTo(saved.getId());

        verify(repository).save(any(Logoped.class));
    }

    @Test
    void update_Success() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        Logoped existing = new Logoped();
        existing.setId(id);

        LogopedDto dto = new LogopedDto(UUID.randomUUID(),"NewName",null,null,null);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,id,"Логопед не найден"))
                    .thenReturn(existing);
            when(repository.save(any(Logoped.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<Logoped>> resultFuture = service.update(id,dto);
            ServiceResult<Logoped> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().getFirstName()).isEqualTo("NewName");

            verify(repository,times(2)).save(any(Logoped.class));
            verify(kcService).updateUserInKeycloak(id,dto);

        }
    }

    @Test
    void delete_Success() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        Logoped existing = new Logoped();
        existing.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,id,"Логопед не найден"))
                    .thenReturn(existing);
            CompletableFuture<ServiceResult<UUID>> resultFuture = service.delete(id);
            ServiceResult<UUID> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }

    @Test
    void create_WhenException_ReturnError() throws ExecutionException, InterruptedException {
        BaseUserDto dto = new LogopedDto(UUID.randomUUID(),"Иван","Иванов","ivan@gmail.com","+79123456789");
        when(repository.save(any(Logoped.class))).thenThrow(new RuntimeException("DB Error"));

        CompletableFuture<ServiceResult<Logoped>> resultFuture = service.create(dto);
        ServiceResult<Logoped> result = resultFuture.get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo("DB Error");
    }

}
