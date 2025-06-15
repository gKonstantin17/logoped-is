package ops.bffforangular.websocket.controller;

import ops.bffforangular.websocket.dto.MyEvent;
import ops.bffforangular.websocket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forevent")
public class EventController {
    @Autowired
    private EventService eventService;
    @PostMapping("event")
    public ResponseEntity getEvent(@RequestBody MyEvent event) {
        System.out.println(event);
        eventService.checkUpcomingEvents(event);
        return ResponseEntity.ok().build();
    }
}
