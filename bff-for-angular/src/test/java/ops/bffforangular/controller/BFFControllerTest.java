package ops.bffforangular.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import ops.bffforangular.contoller.BFFContoller;
import ops.bffforangular.utils.CookieUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BFFControllerTest {

    @Mock
    private CookieUtils cookieUtils;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BFFContoller controller;

    @BeforeEach
    void setup() {
        // Подставляем реальные значения из app.props
        ReflectionTestUtils.setField(controller, "keyCloakURI", "http://localhost:8180/realms/logoped-realm/protocol/openid-connect");
        ReflectionTestUtils.setField(controller, "clientId", "logoped-client");
        ReflectionTestUtils.setField(controller, "clientSecret", "vXQaukDUTP6bDBA7NYUI9JDuwQPI2Akw");
        ReflectionTestUtils.setField(controller, "clientURL", "http://localhost:4200");
        ReflectionTestUtils.setField(controller, "grantTypeCode", "authorization_code");
        ReflectionTestUtils.setField(controller, "grantTypeRefresh", "refresh_token");
        ReflectionTestUtils.setField(controller, "resourceServerURL", "http://localhost:8280");
    }

    @Test
    void token_ShouldReturnCookies_WhenKeycloakReturnsTokens() throws Exception {
        String code = "fake-code";

        // Формируем мокированный JWT с корректным payload
        String payloadJson = "{\"sub\":\"12345\",\"given_name\":\"John\",\"family_name\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone_number\":\"1234567890\",\"realm_access\":{\"roles\":[\"user\"]}}";
        String payloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String fakeJwt = "header." + payloadBase64 + ".signature";

        // Мокированный JSON ответа Keycloak
        String keycloakResponse = """
            {
              "access_token": "%s",
              "id_token": "id123",
              "refresh_token": "refresh123",
              "expires_in": 3600,
              "refresh_expires_in": 7200
            }
            """.formatted(fakeJwt);

        // Мокаем RestTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>(keycloakResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Мокаем CookieUtils
        when(cookieUtils.createCookie(anyString(), anyString(), anyInt()))
                .thenAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    String value = invocation.getArgument(1);
                    int durationInSeconds = invocation.getArgument(2);
                    return ResponseCookie.from(name, value)
                            .maxAge(Duration.ofSeconds(durationInSeconds)) // <- тут Duration, а не int
                            .domain("localhost")
                            .path("/")
                            .httpOnly(true)
                            .sameSite("Strict")
                            .build();
                });
        // Вызываем метод
        ResponseEntity<String> response = controller.token(code);

        // Проверяем
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(cookieUtils, times(3)).createCookie(anyString(), anyString(), anyInt());
    }

    @Test
    void logout_ShouldClearCookies() {
        String idToken = "id123";

        // Мокаем удаление куков
        when(cookieUtils.deleteCookie(anyString()))
                .thenAnswer(invocation -> ResponseCookie.from(
                                invocation.getArgument(0), "")
                        .maxAge(0)
                        .path("/")
                        .build()
                );

        ResponseEntity<String> response = controller.logout(idToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(cookieUtils, times(3)).deleteCookie(anyString());
    }

    @Test
    void exchangeRefreshToken_ShouldReturnNewCookies() {
        String oldRefreshToken = "refresh123";

        String payloadJson = "{\"sub\":\"12345\",\"given_name\":\"John\",\"family_name\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone_number\":\"1234567890\",\"realm_access\":{\"roles\":[\"user\"]}}";
        String payloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String fakeJwt = "header." + payloadBase64 + ".signature";

        String keycloakResponse = """
        {
          "access_token": "%s",
          "id_token": "id123",
          "refresh_token": "refresh456",
          "expires_in": 3600,
          "refresh_expires_in": 7200
        }
        """.formatted(fakeJwt);

        ResponseEntity<String> kcResponse = new ResponseEntity<>(keycloakResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(kcResponse);

        when(cookieUtils.createCookie(anyString(), anyString(), anyInt()))
                .thenAnswer(invocation -> ResponseCookie.from(
                                invocation.getArgument(0),  // имя
                                invocation.getArgument(1))  // значение
                        .maxAge(((Integer) invocation.getArgument(2)).longValue()) // <- приведение к long
                        .path("/")
                        .build()
                );

        ResponseEntity<String> response = controller.exchangeRefreshToken(oldRefreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(cookieUtils, times(3)).createCookie(anyString(), anyString(), anyInt());
    }

    @Test
    void profile_ShouldReturnUserProfile_WhenIdTokenExists() throws Exception {
        // Устанавливаем payload вручную
        String payloadJson = "{\"sub\":\"12345\",\"given_name\":\"John\",\"family_name\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone_number\":\"1234567890\",\"realm_access\":{\"roles\":[\"user\"]}}";
        controller.payload = new JSONObject(payloadJson);
        ReflectionTestUtils.setField(controller, "idToken", "header.payload.signature");

        ResponseEntity<?> response = controller.profile();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("John"));
    }

    @Test
    void operation_ShouldCallRestTemplateAndReturnResponse() throws Exception {
        // Мокаем входной JSON запроса
        String reqJson = "{"
                + "\"httpMethod\":1,"
                + "\"url\":\"http://localhost:8280/test\","
                + "\"body\":\"{\\\"data\\\":\\\"value\\\"}\""
                + "}";





        String accessToken = "access123";

        // Мокаем ответ RestTemplate (возвращаем Map, как будет после десериализации JSON)
        Map<String, String> mockBody = Map.of("result", "ok");
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(mockBody, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Object.class))
        ).thenReturn(mockResponse);

        // Вызываем метод
        ResponseEntity<Object> response = controller.operation(reqJson, accessToken);

        // Проверяем статус
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем тело ответа
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("ok", responseBody.get("result"));

        // Проверяем, что RestTemplate был вызван с правильными заголовками
        ArgumentCaptor<HttpEntity<?>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(
                eq("http://localhost:8280/test"),
                any(HttpMethod.class),
                captor.capture(),
                eq(Object.class)
        );


        HttpEntity<?> capturedRequest = captor.getValue();
        HttpHeaders headers = capturedRequest.getHeaders();
        assertEquals("Bearer " + accessToken, headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());

        // Проверяем тело запроса
        assertEquals("{\"data\":\"value\"}", capturedRequest.getBody());
    }

}
