package logopedis.msnotification.controller;

import logopedis.libentities.msnotification.entity.Notification;
import logopedis.msnotification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/findall")
    public List<Notification> findall()  {
        var result = service.findall();
        return result;
    }

    @PostMapping("/find-by-id")
    public Notification findById(@RequestBody Long id)  {
        var result = service.findById(id);
        return result;
    }

    @PostMapping("/create")
    public Notification create(@RequestBody Notification note)  {
        var result = service.create(note);
        return result;
    }

    @PutMapping("/update")
    public Notification update(@RequestBody Notification note)  {
        var result = service.update(note);
        return result;
    }
    @DeleteMapping("/delete")
    public Notification delete(@RequestBody Long id)  {
        var result = service.delete(id);
        return result;
    }
}
