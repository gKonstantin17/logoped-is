package ops.bffforangular.websocket.controller;

import ops.bffforangular.dto.NotificationReadDto;
import ops.bffforangular.websocket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private EventService eventService;
    @PostMapping("send-to-client")
    public ResponseEntity getEvent(@RequestBody NotificationReadDto dto) {
        System.out.println(dto);
        eventService.checkUpcomingEvents(dto);
        return ResponseEntity.ok().build();
    }
}
