package gk17.rsmain.service;

import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserData> get() {
        return repository.findAll();
    }

}
