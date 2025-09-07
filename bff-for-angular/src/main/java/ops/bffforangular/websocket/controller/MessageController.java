package ops.bffforangular.websocket.controller;


import ops.bffforangular.websocket.dto.Message;
import ops.bffforangular.websocket.dto.MyEvent;
import ops.bffforangular.websocket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private EventService eventService;

    @MessageMapping("/message") // получение на /app/message
    @SendTo("/topic/messages") // отправка
    public Message getMessage(final Message message){
        message.setMessageContent(HtmlUtils.htmlEscape(message.getMessageContent()));
        System.out.println("!!! Message from BACKEND " + message.getMessageContent());
        // в message засунуть коль-во символов
        //message.setMessageContent(String.valueOf(message.getMessageContent().length()));
        return message;
    }
    @MessageMapping("/events") // получение на /app/message
    @SendTo("/topic/messages") // отправка
    public void getEvent(final MyEvent event, @Headers Map<String, Object> headers) {
        // Извлекаем токен из атрибутов WebSocket сессии
//        Map<String, Object> simpSessionAttributes = (Map<String, Object>) headers.get("simpSessionAttributes");
//        String accessToken = (String) simpSessionAttributes.get("accessToken");
//        eventService.sendEvent(event,accessToken);
//        System.out.println("!!! Event from BACKEND: " + event.getDescr());
//
//        return new MyEvent(LocalDateTime.now(), "Событие получено");
    }

}
