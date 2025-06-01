package gk17.rsmain.controller;

import gk17.rsmain.dto.logoped.LogopedDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.service.LogopedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody LogopedDto dto) throws ExecutionException, InterruptedException {
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
