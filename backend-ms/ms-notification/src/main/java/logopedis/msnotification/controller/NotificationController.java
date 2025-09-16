package logopedis.msnotification.controller;

import logopedis.libentities.msnotification.dto.notification.NotificationCreateDto;
import logopedis.libentities.msnotification.dto.notification.NotificationReadDto;
import logopedis.libentities.msnotification.dto.notification.NotificationUpdateDto;
import logopedis.msnotification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<NotificationReadDto> findall() throws ExecutionException, InterruptedException {
        var result = service.findall();
        return result.get().data();
    }
    @PostMapping("/find-messages")
    public List<NotificationReadDto> findByUser(@RequestBody UUID userId) throws ExecutionException, InterruptedException {
        var result = service.findByUser(userId);
        return result.get().data();
    }

    @PutMapping("/receive")
    public ResponseEntity<?> receive(@RequestBody Long id) throws ExecutionException, InterruptedException {
        var future = service.receive(id);
        var result = future.get();

        return result.isSuccess()
                ? ResponseEntity.ok(result.data())
                : ResponseEntity.badRequest().body(result.message());
    }




    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody NotificationUpdateDto dto) throws ExecutionException, InterruptedException {
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
