    package logopedis.rsmain.utils.keycloak;

    import com.fasterxml.jackson.databind.JsonNode;
    import logopedis.libentities.rsmain.dto.user.BaseUserDto;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.stereotype.Service;
    import org.springframework.util.LinkedMultiValueMap;
    import org.springframework.util.MultiValueMap;
    import org.springframework.web.reactive.function.BodyInserters;
    import org.springframework.web.reactive.function.client.WebClient;
    import org.springframework.web.reactive.function.client.WebClientResponseException;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;

    @Service
    public class KeycloakAdminService {
        public String getAdminAccessToken() {
            WebClient client = WebClient.create("http://localhost:8180/realms/logoped-realm/protocol/openid-connect/token");

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");
            formData.add("client_id", "admin-client");
            formData.add("client_secret", "NHxCrJRkAO3MKsm8DzsUu7w1XKIh17Xd");

            String token = client.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.get("access_token").asText())
                    .block();

            return token;
        }
        public void updateUserInKeycloak(UUID keycloakUserId, BaseUserDto dto) {
            String token = getAdminAccessToken();

            // Формируем JSON с обновлёнными полями
            Map<String, Object> updateData = new HashMap<>();
            if (dto.firstName() != null) updateData.put("firstName", dto.firstName());
            if (dto.lastName() != null) updateData.put("lastName", dto.lastName());
            if (dto.email() != null) updateData.put("email", dto.email());
            if (dto.phone() != null) updateData.put("attributes", Map.of("phone", List.of(dto.phone())));

            try {
                WebClient.create("http://localhost:8180")
                        .put()
                        .uri("/admin/realms/logoped-realm/users/{id}", keycloakUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updateData)
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (WebClientResponseException e) {
                // Логируй ошибку, кинь исключение или обрабатывай по своему
                System.err.println("Ошибка обновления Keycloak пользователя: " + e.getMessage());
            }
        }
    }
