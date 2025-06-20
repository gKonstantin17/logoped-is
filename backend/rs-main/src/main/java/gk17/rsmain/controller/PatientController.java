package gk17.rsmain.controller;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientReadDto;
import gk17.rsmain.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<PatientReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PatientCreateDto dto) throws ExecutionException, InterruptedException {
        if (dto.firstName() == null)
            return new ResponseEntity<>("Пропущено имя", HttpStatus.BAD_REQUEST);
        if (dto.lastName() == null)
            return new ResponseEntity<>("Пропущена фамилия", HttpStatus.BAD_REQUEST);
        if (dto.dateOfBirth() == null)
            return new ResponseEntity<>("Пропущена дата рождения", HttpStatus.BAD_REQUEST);
        if (dto.userId() == null)
            return new ResponseEntity<>("Пропущен пользователь", HttpStatus.BAD_REQUEST);


        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PostMapping ("find-by-user")
    public ResponseEntity<?> findByUserId(@RequestBody UUID userId) throws ExecutionException, InterruptedException {
        var result = service.findByUserId(userId).get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PostMapping ("find-by-logoped")
    public ResponseEntity<?> findByLogopedId(@RequestBody UUID logopedId) throws ExecutionException, InterruptedException {
        var result = service.findByLogopegId(logopedId).get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody PatientDto dto) throws ExecutionException, InterruptedException {
        var future = service.update(id, dto);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PostMapping("/hide/{id}")
    public ResponseEntity<?> hide(@PathVariable Long id) throws ExecutionException, InterruptedException {
        var future = service.hide(id);
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

    @PostMapping("exists-speechcard")
    public ResponseEntity<?> existsCardByPatient(@RequestBody Long id) throws ExecutionException, InterruptedException {
        var result = service.existsCardByPatient(id).get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
}
