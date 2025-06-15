package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.dto.user.UserWithIdDto;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.LogopedRepository;
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
    private final LogopedRepository logopedRepository;
    public UserService(UserRepository repository, LogopedRepository logopedRepository) {
        this.repository = repository;
        this.logopedRepository = logopedRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<UserData>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> create(UserWithIdDto dto) {
        try {
            UserData user = new UserData();
            user.setId(dto.id());
            user.setFirstName(dto.firstName());
            user.setLastName(dto.lastName());
            user.setEmail(dto.email());
            user.setPhone(dto.phone());

            UserData result = repository.save(user);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<UserData>> createIfNotExists(UserWithIdDto dto) {
        try {
            if ("user".equalsIgnoreCase(dto.role())) {
                Optional<UserData> existing = repository.findById(dto.id());

                if (existing.isPresent()) {
                    return CompletableFuture.completedFuture(ServiceResult.success(existing.get()));
                }

                UserData user = new UserData();
                user.setId(dto.id());
                user.setFirstName(dto.firstName());
                user.setLastName(dto.lastName());
                user.setEmail(dto.email());
                user.setPhone(dto.phone());

                UserData saved = repository.save(user);
                return CompletableFuture.completedFuture(ServiceResult.success(saved));
            }

            if ("logoped".equalsIgnoreCase(dto.role())) {
                Optional<UserData> existingUser = repository.findById(dto.id());

                existingUser.ifPresent(repository::delete); // удаляем UserData, если есть

                // Проверка: существует ли логопед уже
                Optional<Logoped> existingLogoped = logopedRepository.findById(dto.id());
                if (existingLogoped.isPresent()) {
                    return CompletableFuture.completedFuture(ServiceResult.error("Логопед с таким ID уже существует"));
                }

                // Создаём логопеда
                Logoped logoped = new Logoped();
                logoped.setId(dto.id());
                logoped.setFirstName(dto.firstName());
                logoped.setLastName(dto.lastName());
                logoped.setEmail(dto.email());
                logoped.setPhone(dto.phone());

                logopedRepository.save(logoped);
                return CompletableFuture.completedFuture(ServiceResult.success(null)); // или вернуть logoped, если нужно
            }

            return CompletableFuture.completedFuture(ServiceResult.error("Неизвестная роль: " + dto.role()));

        } catch (Exception e) {
            return CompletableFuture.completedFuture(ServiceResult.error("Ошибка создания пользователя: " + e.getMessage()));
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
}
