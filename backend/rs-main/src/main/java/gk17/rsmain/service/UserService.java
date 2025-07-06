package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.dto.user.UserWithIdDto;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.UserRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
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
    public UserService(UserRepository repository,LogopedService logopedService) {
        this.repository = repository;
        this.logopedService = logopedService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<UserData>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
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

            return AsyncResult.success(repository.save(result));
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
