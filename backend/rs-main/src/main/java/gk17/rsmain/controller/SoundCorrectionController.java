package gk17.rsmain.controller;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.entity.SoundCorrection;
import gk17.rsmain.service.PatientService;
import gk17.rsmain.service.SoundCorrectionService;
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
    public List<SoundCorrection> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
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
