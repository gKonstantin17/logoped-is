package logopedis.rsmain.controller;

import logopedis.libentities.rsmain.dto.logoped.LogopedDto;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.rsmain.service.LogopedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/logoped")
public class LogopedController {
    private final LogopedService service;
    public LogopedController(LogopedService service) {
        this.service = service;
    }
    @PostMapping("/findall")
    public List<Logoped> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LogopedDto dto) throws ExecutionException, InterruptedException {
        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody LogopedDto dto) throws ExecutionException, InterruptedException {
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
