package ops.bffforangular.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyEvent {
    private LocalDateTime date;
    private String descr;
}
