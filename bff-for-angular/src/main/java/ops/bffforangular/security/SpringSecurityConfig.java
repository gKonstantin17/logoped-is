package ops.bffforangular.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Value("${client.url}")
    private String clientURL;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/bff/*").permitAll()
                        .requestMatchers("/ws/**").permitAll() // для websocket /forevent
                        .requestMatchers("/forevent/*").permitAll()
                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of(clientURL));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
//                    var source = new UrlBasedCorsConfigurationSource();
//                    source.registerCorsConfiguration("/**",corsConfiguration);
                    return corsConfiguration;
                }));

        // NEW
        // обяз исп https
        // http.requiresChannel(channel -> channel.anyRequest().requiresSecure());

        // отключить создание cookie для сессии
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
