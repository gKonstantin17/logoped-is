package ops.bffforangular.websocket.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Base64;
import java.util.Map;

public class WebSocketAuthHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String token = (String) attributes.get("accessToken");
        if (token == null) {
            return () -> "anonymous"; // fallback
        }

        try {
            // JWT: header.payload.signature → берем payload
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return () -> "anonymous";
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Вытаскиваем "sub": "...userId..."
            String sub = payloadJson.split("\"sub\":\"")[1].split("\"")[0];

            return () -> sub; // Principal с userId
        } catch (Exception e) {
            return () -> "anonymous";
        }
    }
}