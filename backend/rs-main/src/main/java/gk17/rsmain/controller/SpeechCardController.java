package gk17.rsmain.controller;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.speechCard.SpeechCardDto;
import gk17.rsmain.dto.speechCard.SpeechCardReadDto;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.entity.SpeechCard;
import gk17.rsmain.service.PatientService;
import gk17.rsmain.service.SpeechCardService;
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
