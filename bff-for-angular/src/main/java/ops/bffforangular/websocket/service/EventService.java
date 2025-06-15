package ops.bffforangular.websocket.service;



import ops.bffforangular.websocket.dto.MyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private static final RestTemplate restTemplate = new RestTemplate();
    //private Map<String, MyEvent> eventsStorage = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void sendEvent(MyEvent event, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // слово Bearer будет добавлено автоматически
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Оборачиваем тело в HttpEntity
        HttpEntity<MyEvent> entity = new HttpEntity<>(event, headers);
        System.out.println("\n\n"+entity);
        System.out.println(entity.getBody());
        // Отправляем POST-запрос
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8765/rs/data/event",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode()== HttpStatus.OK) {
            System.out.println("ОКей");
        }
//        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8765/rs/event",HttpMethod.POST,)
//        String eventId = "event_" + System.currentTimeMillis();
//        eventsStorage.put(eventId, event);
    }
//    public List<MyEvent> getAllEvents() {
//        return new ArrayList<>(eventsStorage.values());
//    }
    public void checkUpcomingEvents(MyEvent eventToSend) {
        // просто отправка клиенту
        messagingTemplate.convertAndSend("/topic/messages", eventToSend);
        System.out.println("Sent notification: " + eventToSend);
    }
}
