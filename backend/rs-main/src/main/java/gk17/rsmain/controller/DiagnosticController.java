package gk17.rsmain.controller;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.service.DiagnosticService;
import gk17.rsmain.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/diagnostic")
public class DiagnosticController {
    private final DiagnosticService service;

    public DiagnosticController(DiagnosticService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<Diagnostic> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DiagnosticDto dto) throws ExecutionException, InterruptedException {
        if (dto.date() == null)
            return new ResponseEntity<>("Пропущено имя", HttpStatus.BAD_REQUEST);
        if (dto.lessonId() == null)
            return new ResponseEntity<>("Пропущен урок", HttpStatus.BAD_REQUEST);
        if (dto.speechCardId() == null)
            return new ResponseEntity<>("Пропущена речевая карта", HttpStatus.BAD_REQUEST);


        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody DiagnosticDto dto) throws ExecutionException, InterruptedException {
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
