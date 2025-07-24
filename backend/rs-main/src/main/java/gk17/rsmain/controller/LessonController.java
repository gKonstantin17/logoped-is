package gk17.rsmain.controller;

import gk17.rsmain.dto.lesson.*;
import gk17.rsmain.dto.patient.PatientWithoutFKDto;
import gk17.rsmain.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
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
    @PostMapping("/find-with-fk")
    public LessonWithFKDto findWithFk(@RequestBody Long id) throws ExecutionException, InterruptedException {
        var result = service.findByIdWithFK(id);
        return result.get().data();
    }
    @PostMapping("find-by-user")
    public List<LessonWithFKDto> findByUserId(@RequestBody UUID userId) throws ExecutionException, InterruptedException {
        var result = service.findByUserId(userId);
        return result.get().data();
    }
    @PostMapping("find-by-logoped")
    public List<LessonWithFKDto> findByLogopedId(@RequestBody UUID logopedId) throws ExecutionException, InterruptedException {
        var result = service.findByLogopedId(logopedId);
        return result.get().data();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWithHomework(@RequestBody LessonWithHomeworkDto dto) throws ExecutionException, InterruptedException {

        if (dto.type() == null)
            return new ResponseEntity<>("Пропущен тип", HttpStatus.BAD_REQUEST);
        if (dto.topic() == null)
            return new ResponseEntity<>("Пропущена тема", HttpStatus.BAD_REQUEST);
        if (dto.dateOfLesson() == null)
            return new ResponseEntity<>("Пропущена дата урока", HttpStatus.BAD_REQUEST);
        if (dto.description() == null)
            return new ResponseEntity<>("Пропущено описание", HttpStatus.BAD_REQUEST);
        if (dto.patientsId() == null)
            return new ResponseEntity<>("Пропущен(ы) пациент(ы)", HttpStatus.BAD_REQUEST);

        var future = service.createLessonWithHomework(dto);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }

    @PostMapping("check-time")
    public ResponseEntity<?> checkTime(@RequestBody CheckAvailableTime dto) throws ExecutionException, InterruptedException {
//    public ResponseEntity<?> checkTime(@PathVariable Long patientId,
//                                       @RequestBody CheckAvailableTime dto) throws ExecutionException, InterruptedException {

        var future = service.checkTime(dto.patientId(), dto.date());
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("cancel/{id}")
    public ResponseEntity<?> cansel(@PathVariable Long id) throws ExecutionException, InterruptedException {
        var future = service.canselLesson(id);
        var result = future.get();
        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }
    @PutMapping("changeDate/{id}")
    public ResponseEntity<?> changeDate(@PathVariable Long id, @RequestBody Timestamp newDate) throws ExecutionException, InterruptedException {
        var future = service.changeDate(id,newDate);
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
