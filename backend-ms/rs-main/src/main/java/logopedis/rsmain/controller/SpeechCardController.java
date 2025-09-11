package logopedis.rsmain.controller;

import logopedis.libentities.rsmain.dto.speechCard.*;
import logopedis.rsmain.service.SpeechCardService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/speechcard")
public class SpeechCardController {
    private final SpeechCardService service;

    public SpeechCardController(SpeechCardService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<SpeechCardReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/find-by-patient")
    public SpeechCardFullDto findByPatientId(@RequestBody Long id) throws ExecutionException, InterruptedException, ChangeSetPersister.NotFoundException {
        return service.findByPatientId(id).get().data();
    }
    @PostMapping("/find-patient-history")
    public List<PatientHistoryDto> findHistory(@RequestBody Long id) throws ExecutionException, InterruptedException, ChangeSetPersister.NotFoundException {
        return service.findPatientHistory(id).get().data();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SpeechCardDto dto) throws ExecutionException, InterruptedException {
        if (dto.reason() == null || dto.reason().isBlank())
            return new ResponseEntity<>("Пропущена причина обращения", HttpStatus.BAD_REQUEST);
        if (dto.stateOfHearning() == null || dto.stateOfHearning().isBlank())
            return new ResponseEntity<>("Пропущено состояние слуха", HttpStatus.BAD_REQUEST);
        if (dto.anamnesis() == null || dto.anamnesis().isBlank())
            return new ResponseEntity<>("Пропущен анамнез", HttpStatus.BAD_REQUEST);
        if (dto.generalMotor() == null || dto.generalMotor().isBlank())
            return new ResponseEntity<>("Пропущена общая моторика", HttpStatus.BAD_REQUEST);
        if (dto.fineMotor() == null || dto.fineMotor().isBlank())
            return new ResponseEntity<>("Пропущена мелкая моторика", HttpStatus.BAD_REQUEST);
        if (dto.articulatory() == null || dto.articulatory().isBlank())
            return new ResponseEntity<>("Пропущена артикуляция", HttpStatus.BAD_REQUEST);
        if (dto.soundReproduction() == null || dto.soundReproduction().isBlank())
            return new ResponseEntity<>("Пропущено воспроизведение звуков", HttpStatus.BAD_REQUEST);
        if (dto.soundComponition() == null || dto.soundComponition().isBlank())
            return new ResponseEntity<>("Пропущена звуковая композиция", HttpStatus.BAD_REQUEST);
        if (dto.speechChars() == null || dto.speechChars().isBlank())
            return new ResponseEntity<>("Пропущена характеристика речи", HttpStatus.BAD_REQUEST);
        if (dto.patientChars() == null || dto.patientChars().isBlank())
            return new ResponseEntity<>("Пропущена характеристика пациента", HttpStatus.BAD_REQUEST);
        if (dto.speechErrors() == null || dto.speechErrors().isEmpty())
            return new ResponseEntity<>("Пропущены речевые нарушения", HttpStatus.BAD_REQUEST);
        if (dto.soundCorrections() == null || dto.soundCorrections().isEmpty())
            return new ResponseEntity<>("Пропущены звуки для коррекции", HttpStatus.BAD_REQUEST);

        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PostMapping("/create-with-diagnostic")
    public ResponseEntity<?> createWithDiagnostic(@RequestBody SCFromDiagnosticDto dto) throws ExecutionException, InterruptedException {
        var future = service.createFromDiag(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PostMapping("/create-with-corrections")
    public ResponseEntity<?> createUpdateWithCorrctions(@RequestBody SpeechCardCorrectionDto dto) throws ExecutionException, InterruptedException {
        var future = service.createUpdateWithCorrctions(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody SpeechCardDto dto) throws ExecutionException, InterruptedException {
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
