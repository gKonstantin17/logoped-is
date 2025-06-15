package ops.bffforangular.websocket.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Извлекаем токен из cookies
        String token = extractTokenFromCookies(request);

        if (token != null) {
            // Сохраняем токен в атрибуты сессии WebSocket
            attributes.put("accessToken", token);
        }

        return true; // Разрешаем подключение
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
    private String extractTokenFromCookies(ServerHttpRequest request) {
        // Извлекаем cookies из заголовков запроса
        HttpHeaders headers = request.getHeaders();
        if (headers != null && headers.get("Cookie") != null) {
            String cookiesHeader = String.join(";", headers.get("Cookie"));
            String token = parseCookieValue(cookiesHeader, "AT");
            return token;
        }
        return null;
    }

    private String parseCookieValue(String cookiesHeader, String cookieName) {
        String[] cookies = cookiesHeader.split(";");
        for (String cookie : cookies) {
            String[] cookieParts = cookie.trim().split("=");
            if (cookieParts.length == 2 && cookieParts[0].equals(cookieName)) {
                return cookieParts[1]; // Возвращаем значение токена
            }
        }
        return null;
    }
}
