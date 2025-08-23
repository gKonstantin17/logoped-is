package logopedis.rsmain.service;

import logopedis.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.rsmain.dto.user.UserDto;
import logopedis.rsmain.dto.user.UserWithIdDto;
import logopedis.rsmain.entity.UserData;
import logopedis.rsmain.repository.UserRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
import logopedis.rsmain.utils.keycloak.KeycloakAdminService;
import logopedis.rsmain.service.LogopedService;
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

public class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private LogopedService logopedService;
    @Mock
    private KeycloakAdminService kcService;
    @InjectMocks
    private UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsList() throws ExecutionException, InterruptedException {
        List<UserData> list = List.of(new UserData());
        when(repository.findAll()).thenReturn(list);

        CompletableFuture<ServiceResult<List<UserData>>> resultFuture = service.findall();
        ServiceResult<List<UserData>> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);

        verify(repository).findAll();
    }

    @Test
    void findById_ReturnsOptional() {
        UUID id = UUID.randomUUID();
        UserData user = new UserData();
        when(repository.findById(id)).thenReturn(Optional.of(user));

        Optional<UserData> result = service.findById(id);

        assertThat(result).isPresent();
        verify(repository).findById(id);
    }

    @Test
    void create_ReturnsSavedUser() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        UserWithIdDto dto = new UserWithIdDto(id,"Иван","Иванов","ivan@gmail.com","+79123456789","savedUser");
        UserData savedUser = new UserData();
        savedUser.setId(id);

        when(repository.save(any(UserData.class))).thenAnswer(inv -> inv.getArgument(0));
        CompletableFuture<ServiceResult<UserData>> resultFuture = service.create(dto);
        ServiceResult<UserData> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().getId()).isEqualTo(id);

        verify(repository).save(any(UserData.class));
    }

    @Test
    void update_ReturnsUpdatedUser() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        UserDto dto = new UserDto("NewName", null,null,null);
        UserData user = new UserData();
        user.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,id,"Пользователь не найден"))
                    .thenReturn(user);
            when(repository.save(any(UserData.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<UserData>> resultFuture = service.update(id,dto);
            ServiceResult<UserData> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().getFirstName()).isEqualTo("NewName");

            verify(repository).save(any(UserData.class));
            verify(kcService).updateUserInKeycloak(id,dto);
        }
    }

    @Test
    void createIfNotExists_UserRole_NewUser_SavesAndReturnsTrue() throws Exception {
        UUID id = UUID.randomUUID();
        UserWithIdDto dto = new UserWithIdDto(id,"Иван","Иванов","ivan@gmail.com","+79123456789","user");

        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.save(any(UserData.class))).thenAnswer(inv -> inv.getArgument(0));

        CompletableFuture<ServiceResult<Boolean>> resultFuture = service.createIfNotExists(dto);
        ServiceResult<Boolean> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isTrue();

        verify(repository).save(any(UserData.class));
    }

    @Test
    void createIfNotExists_UserRole_ExistingUser_ReturnsTrueWithoutSave() throws Exception {
        UUID id = UUID.randomUUID();
        UserWithIdDto dto = new UserWithIdDto(id,"Иван","Иванов","ivan@gmail.com","+79123456789","user");

        when(repository.findById(id)).thenReturn(Optional.of(new UserData()));

        CompletableFuture<ServiceResult<Boolean>> resultFuture = service.createIfNotExists(dto);
        ServiceResult<Boolean> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isTrue();

        verify(repository, never()).save(any());
    }

    @Test
    void createIfNotExists_LogopedRole_NewLogoped_CreatesLogoped() throws Exception {
        UUID id = UUID.randomUUID();
        UserWithIdDto dto = new UserWithIdDto(id,"Иван","Иванов","ivan@gmail.com","+79123456789","logoped");

        // Логопеда нет
        when(logopedService.findById(id)).thenReturn(Optional.empty());
        // Но есть UserData, который должен удалиться
        when(repository.findById(id)).thenReturn(Optional.of(new UserData()));

        CompletableFuture<ServiceResult<Boolean>> resultFuture = service.createIfNotExists(dto);
        ServiceResult<Boolean> result = resultFuture.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).isTrue();

        verify(repository).findById(id);
        verify(repository).delete(any(UserData.class)); // теперь вызов реально будет
        verify(logopedService).create(dto);
    }

    @Test
    void createIfNotExists_UnknownRole_ReturnsError() throws Exception {
        UserWithIdDto dto = new UserWithIdDto(UUID.randomUUID(),"Иван","Иванов","ivan@gmail.com","+79123456789","admin");

        CompletableFuture<ServiceResult<Boolean>> resultFuture = service.createIfNotExists(dto);
        ServiceResult<Boolean> result = resultFuture.get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("Неизвестная роль");
    }

    @Test
    void delete_ExistingUser_DeletesAndReturnsId() throws Exception {
        UUID id = UUID.randomUUID();
        UserData user = new UserData();
        user.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,id,"Пользователь не найден"))
                    .thenReturn(user);

            CompletableFuture<ServiceResult<UUID>> resultFuture = service.delete(id);
            ServiceResult<UUID> result = resultFuture.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }

    @Test
    void delete_NotFound_ReturnsError() throws Exception {
        UUID id = UUID.randomUUID();

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository,id,"Пользователь не найден"))
                    .thenThrow(new RuntimeException("Пользователь не найден"));

            CompletableFuture<ServiceResult<UUID>> resultFuture = service.delete(id);
            ServiceResult<UUID> result = resultFuture.get();

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.message()).contains("Пользователь не найден");
        }
    }


}
