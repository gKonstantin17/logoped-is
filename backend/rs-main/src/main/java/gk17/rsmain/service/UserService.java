package gk17.rsmain.service;

import gk17.rsmain.dto.serviceResult.ServiceResult;
import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserData> get() {
        return repository.findAll();
    }
    public ServiceResult<UserData> create(UserDto dto) {
        UserData user  = new UserData();
        user.setFirstName(dto.firstName());
        user.setSecondName(dto.secondName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        return ServiceResult.success(repository.save(user));
    }
    public ServiceResult<UserData> update(Long id, UserDto dto) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return ServiceResult.error("Пользователь не найден");

        var user = result.get();
        if (dto.firstName() != null)     user.setFirstName(dto.firstName());
        if (dto.secondName() != null)    user.setSecondName(dto.secondName());
        if (dto.email() != null)         user.setEmail(dto.email());
        if (dto.phone() != null)         user.setPhone(dto.phone());
        return ServiceResult.success(repository.save(user));
    }
    public ServiceResult<UserData> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return ServiceResult.error("Пользователь не найден");

        var user = result.get();
        repository.deleteById(id);
        return ServiceResult.success(user);
    }
}
