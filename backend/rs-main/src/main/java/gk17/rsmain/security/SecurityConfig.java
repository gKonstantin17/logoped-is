package gk17.rsmain.security;

import gk17.rsmain.utils.keycloak.KCRoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Value("${swagger.enabled}")
    private boolean swaggerEnabled;
    @Value("${client.url}")
    private String clientURL;

    // old
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        if (swaggerEnabled) {
//            http
//                    .cors(withDefaults())
//                    .csrf(csrf -> csrf.disable())
////                    .authorizeHttpRequests(auth -> auth
////                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated() // Swagger требует аутентификации
////                            .anyRequest().authenticated() // все остальные тоже требуют
////                    )
////                    .httpBasic(withDefaults()); // включаем basic auth
//                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        } else {
//            http
//                    .csrf(csrf -> csrf.disable())
//                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//        }
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:4200")); // Разрешаем Angular
//        config.setAllowedMethods(List.of("*"));
//        config.setAllowedHeaders(List.of("*")); // Разрешаем все заголовки
//        config.setAllowCredentials(true); // Для куков / авторизации
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // Применить ко всем endpoint'ам
//        return source;
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

        http.authorizeHttpRequests(authz -> authz // доступ авторизованным, без проверки полей
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/swagger-ui.html",
                                        "/webjars/**"
                                ).permitAll()
//                        .requestMatchers("/admin/*").hasRole("admin")
//                        .requestMatchers("/user/*").hasRole("user") // какие доступны
                                .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable()) // отключить встроенную защиту от csrf т.к. исп из oauth2

                .cors(cors -> cors.configurationSource(request -> { // чтобы разрешить options запросы от клиента
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of(clientURL)); // Разрешенные источники
                    corsConfiguration.setAllowedMethods(List.of("*")); // Разрешенные методы
                    corsConfiguration.setAllowedHeaders(List.of("*")); // Разрешенные заголовки
//corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                );


        return http.build();
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHeaderNames((header)-> true);
        firewall.setAllowedHeaderValues((header)-> true);
        firewall.setAllowedParameterNames((parameter) -> true);
        return firewall;
    }
}