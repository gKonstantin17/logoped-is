package gk17.rsmain.controller;

import gk17.rsmain.dto.user.UserDto;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/get")
    public List<UserData> get() {
        return service.get();
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserDto dto) {
        if (dto.firstName() == null)
            return new ResponseEntity<>("Пропущено имя", HttpStatus.BAD_REQUEST);
        if (dto.secondName() == null)
            return new ResponseEntity<>("Пропущена фамилия", HttpStatus.BAD_REQUEST);
        if (dto.email() == null)
            return new ResponseEntity<>("Пропущен email", HttpStatus.BAD_REQUEST);
        if (dto.phone() == null)
            return new ResponseEntity<>("Пропущен телефон", HttpStatus.BAD_REQUEST);


        var result = service.create(dto);
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody UserDto dto) {
        var result = service.update(id,dto);
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        var result = service.delete(id);
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

}
