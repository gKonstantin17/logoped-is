package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.user.UserDto;
import logopedis.libentities.rsmain.dto.user.UserWithIdDto;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.libentities.rsmain.entity.UserData;
import logopedis.rsmain.repository.UserRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.libutils.keycloak.KeycloakAdminService;
import logopedis.rsmain.service.LogopedService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final UserRepository repository;
    private final LogopedService logopedService;
    private final KeycloakAdminService kcService;
    public UserService(UserRepository repository, LogopedService logopedService, KeycloakAdminService kcService) {
        this.repository = repository;
        this.logopedService = logopedService;
        this.kcService = kcService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<UserData>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    public Optional<UserData> findById(UUID id) {
        return repository.findById(id);
    }
    @Async
    public CompletableFuture<ServiceResult<UserData>> create(UserWithIdDto dto) {
        try {
            UserData user = userFromDto(dto);
            UserData result = repository.save(user);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    public UserData findByPatient(Long id) {
        return repository.findByPatient(id);
    }
    @Async
    public CompletableFuture<ServiceResult<Boolean>> createIfNotExists(UserWithIdDto dto) {
        try {
            if ("user".equalsIgnoreCase(dto.role())) {
                Optional<UserData> existing = repository.findById(dto.id());

                if (existing.isPresent())
                    return AsyncResult.success(true);

                UserData user = userFromDto(dto);

                repository.save(user);
                return AsyncResult.success(true);
            }

            if ("logoped".equalsIgnoreCase(dto.role())) {
                Optional<Logoped> existingLogoped = logopedService.findById(dto.id());
                if (existingLogoped.isPresent()) {
                    return AsyncResult.success(true); //Логопед с таким ID уже существует
                }
                repository.findById(dto.id()).ifPresent(repository::delete); // удаляем из UserData, если есть

                logopedService.create(dto);
                return AsyncResult.success(true); // или вернуть logoped, если нужно
            }

            return AsyncResult.error("Неизвестная роль: " + dto.role());

        } catch (Exception e) {
            return AsyncResult.error("Ошибка создания пользователя: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> update(UUID id, UserDto dto) {
        try {
            var result = ResponseHelper.findById(repository,id,"Пользователь не найден");

            if (dto.firstName() != null)     result.setFirstName(dto.firstName());
            if (dto.lastName() != null)    result.setLastName(dto.lastName());
            if (dto.email() != null)         result.setEmail(dto.email());
            if (dto.phone() != null)         result.setPhone(dto.phone());

            UserData saved = repository.save(result);

            // Обновляем Keycloak
            if (saved.getId() != null) {
                kcService.updateUserInKeycloak(saved.getId(), dto);
            }

            return AsyncResult.success(saved);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UUID>> delete(UUID id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Пользователь не найден");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

    }

    private UserData userFromDto(UserWithIdDto dto) {
        UserData user = new UserData();
        user.setId(dto.id());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        return user;
    }
}
