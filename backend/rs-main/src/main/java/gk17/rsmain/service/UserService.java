package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<UserData>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> create(UserDto dto) {
        try {
            UserData user = new UserData();
            user.setFirstName(dto.firstName());
            user.setSecondName(dto.secondName());
            user.setEmail(dto.email());
            user.setPhone(dto.phone());

            UserData savedUser = repository.save(user);
            return AsyncResult.success(savedUser);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> update(Long id, UserDto dto) {
        try {
            var result = repository.findById(id);
            if (result.isEmpty())
                return AsyncResult.error("Пользователь не найден");
            var user = result.get();

            if (dto.firstName() != null)     user.setFirstName(dto.firstName());
            if (dto.secondName() != null)    user.setSecondName(dto.secondName());
            if (dto.email() != null)         user.setEmail(dto.email());
            if (dto.phone() != null)         user.setPhone(dto.phone());

            return AsyncResult.success(repository.save(user));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Пользователь не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }
}
