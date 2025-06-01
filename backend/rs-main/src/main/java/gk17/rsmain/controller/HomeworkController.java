package gk17.rsmain.controller;

import gk17.rsmain.dto.homework.HomeworkDto;
import gk17.rsmain.entity.Homework;
import gk17.rsmain.service.HomeworkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/homework")
public class HomeworkController {
    private final HomeworkService service;

    public HomeworkController(HomeworkService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<Homework> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody HomeworkDto dto) throws ExecutionException, InterruptedException {
        if (dto.task() == null)
            return new ResponseEntity<>("Пропущено задание", HttpStatus.BAD_REQUEST);

        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody HomeworkDto dto) throws ExecutionException, InterruptedException {
        var future = service.update(id, dto);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ExecutionException, InterruptedException {
        var future = service.delete(id);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
}
