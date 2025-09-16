package logopedis.libutils.swagger;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoOpener {

    @EventListener
    public void onWebServerReady(WebServerInitializedEvent event) {
        int serverPort = event.getWebServer().getPort();
        String url = "http://localhost:" + serverPort + "/swagger-ui.html"; // корневой путь
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