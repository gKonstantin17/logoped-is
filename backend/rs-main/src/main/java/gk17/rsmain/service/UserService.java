package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.UserRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
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

            UserData result = repository.save(user);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UserData>> update(Long id, UserDto dto) {
        try {
            var result = ResponseHelper.findById(repository,id,"Пользователь не найден");

            if (dto.firstName() != null)     result.setFirstName(dto.firstName());
            if (dto.secondName() != null)    result.setSecondName(dto.secondName());
            if (dto.email() != null)         result.setEmail(dto.email());
            if (dto.phone() != null)         result.setPhone(dto.phone());

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Пользователь не найден");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

    }
}
