package gk17.rsmain.controller;

import gk17.rsmain.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;
    public UserController(UserService service) {
        this.service = service;
    }
    @GetMapping("/get")
    public List<UserData> get() {
        return service.get();
    }
}
