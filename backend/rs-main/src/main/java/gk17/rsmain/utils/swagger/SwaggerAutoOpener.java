package gk17.rsmain.utils.swagger;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


// открытие swagger при запуске проекта
// (иначе самому открывать http://localhost:8180/swagger-ui/index.html)
@Component
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoOpener {
    @Value("${server.port}")
    private int serverPort;

    @PostConstruct
    public void openSwaggerUi() {
        String url = "http://localhost:" + serverPort + "/swagger-ui/index.html";
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                System.out.println("Unsupported OS. Swagger UI at: " + url);
            }
        } catch (Exception e) {
            System.out.println("Could not open browser. Swagger UI at: " + url);
        }
    }
}