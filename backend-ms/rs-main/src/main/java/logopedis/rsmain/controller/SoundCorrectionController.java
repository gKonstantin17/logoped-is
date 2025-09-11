package logopedis.rsmain.controller;

import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionReadDto;
import logopedis.rsmain.service.SoundCorrectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/soundcorrection")
public class SoundCorrectionController {
    private final SoundCorrectionService service;

    public SoundCorrectionController(SoundCorrectionService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<SoundCorrectionReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/find-by-patient")
    public List<SoundCorrectionReadDto> findByPatient(@RequestBody Long id) throws ExecutionException, InterruptedException {
        var result = service.findLatestByPatientId(id);
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SoundCorrectionDto dto) throws ExecutionException, InterruptedException {
        if (dto.sound() == null)
            return new ResponseEntity<>("Пропущен звук", HttpStatus.BAD_REQUEST);
        if (dto.correction() == null)
            return new ResponseEntity<>("Пропущено направление коррекции", HttpStatus.BAD_REQUEST);

        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody SoundCorrectionDto dto) throws ExecutionException, InterruptedException {
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
