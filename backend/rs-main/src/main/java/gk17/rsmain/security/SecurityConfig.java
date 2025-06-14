package gk17.rsmain.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Value("${swagger.enabled}")
    private boolean swaggerEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (swaggerEnabled) {
            http
                    .cors(withDefaults())
                    .csrf(csrf -> csrf.disable())
//                    .authorizeHttpRequests(auth -> auth
//                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated() // Swagger требует аутентификации
//                            .anyRequest().authenticated() // все остальные тоже требуют
//                    )
//                    .httpBasic(withDefaults()); // включаем basic auth
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        } else {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Разрешаем Angular
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*")); // Разрешаем все заголовки
        config.setAllowCredentials(true); // Для куков / авторизации

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Применить ко всем endpoint'ам
        return source;
    }
}