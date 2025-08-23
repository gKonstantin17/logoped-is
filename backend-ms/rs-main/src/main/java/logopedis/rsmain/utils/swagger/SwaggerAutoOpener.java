package logopedis.rsmain.utils.swagger;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoOpener {

    private final WebServerApplicationContext webServerAppCtxt;

    public SwaggerAutoOpener(WebServerApplicationContext webServerAppCtxt) {
        this.webServerAppCtxt = webServerAppCtxt;
    }

    @PostConstruct
    public void openSwaggerUi() {
        int serverPort = webServerAppCtxt.getWebServer().getPort();
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
