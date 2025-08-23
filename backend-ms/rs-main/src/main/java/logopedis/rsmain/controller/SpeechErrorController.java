package logopedis.rsmain.controller;

import logopedis.rsmain.dto.speechError.SpeechErrorDto;
import logopedis.rsmain.dto.speechError.SpeechErrorReadDto;
import logopedis.rsmain.service.SpeechErrorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/speecherror")
public class SpeechErrorController {
    private final SpeechErrorService service;

    public SpeechErrorController(SpeechErrorService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<SpeechErrorReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SpeechErrorDto dto) throws ExecutionException, InterruptedException {
        if (dto.title() == null)
            return new ResponseEntity<>("Пропущено название", HttpStatus.BAD_REQUEST);
        if (dto.description() == null)
            return new ResponseEntity<>("Пропущено описание", HttpStatus.BAD_REQUEST);

        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody SpeechErrorDto dto) throws ExecutionException, InterruptedException {
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
