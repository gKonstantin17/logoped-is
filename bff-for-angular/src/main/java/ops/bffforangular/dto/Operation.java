package ops.bffforangular.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operation {
    private HttpMethod httpMethod;
    private String url;
    private Object body;
}
