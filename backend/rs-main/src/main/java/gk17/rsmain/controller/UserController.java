package gk17.rsmain.controller;

import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.dto.user.UserWithIdDto;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<UserData> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/is-exist")
    public ResponseEntity<?> createIfNotExists(@RequestBody UserWithIdDto dto) throws ExecutionException, InterruptedException {
        var future = service.createIfNotExists(dto);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserWithIdDto dto) throws ExecutionException, InterruptedException {
        // валидация в keycloak
        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UserDto dto) throws ExecutionException, InterruptedException {
        var future = service.update(id, dto);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        var future = service.delete(id);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

}
