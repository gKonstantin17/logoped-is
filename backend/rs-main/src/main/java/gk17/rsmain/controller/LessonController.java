package gk17.rsmain.controller;

import gk17.rsmain.dto.lesson.LessonDto;
import gk17.rsmain.dto.lesson.LessonReadDto;
import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.entity.Lesson;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.service.LessonService;
import gk17.rsmain.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/lesson")
public class LessonController {
    private final LessonService service;

    public LessonController(LessonService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<LessonReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LessonDto dto) throws ExecutionException, InterruptedException {
        if (dto.type() == null)
            return new ResponseEntity<>("Пропущен тип", HttpStatus.BAD_REQUEST);
        if (dto.topic() == null)
            return new ResponseEntity<>("Пропущена тема", HttpStatus.BAD_REQUEST);
        if (dto.dateOfLesson() == null)
            return new ResponseEntity<>("Пропущена дата урока", HttpStatus.BAD_REQUEST);
        if (dto.description() == null)
            return new ResponseEntity<>("Пропущено описание", HttpStatus.BAD_REQUEST);
//        if (dto.homeworkId() == null)
//            return new ResponseEntity<>("Пропущено домашнее задание", HttpStatus.BAD_REQUEST);
//        if (dto.logopedId() == null)
//            return new ResponseEntity<>("Пропущен логопед", HttpStatus.BAD_REQUEST);
        if (dto.patientsId() == null)
            return new ResponseEntity<>("Пропущен(ы) пациент(ы)", HttpStatus.BAD_REQUEST);


        var future = service.create(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody LessonDto dto) throws ExecutionException, InterruptedException {
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
