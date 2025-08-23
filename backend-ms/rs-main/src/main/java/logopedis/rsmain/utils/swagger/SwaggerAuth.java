package logopedis.rsmain.utils.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerAuth {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("keycloak", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:8180/realms/logoped-realm/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8180/realms/logoped-realm/protocol/openid-connect/token")
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect scope")
                                                )
                                        )
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }

}
