package de.szut.lf8_starter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI api() {
        Info info = new Info()
                .title("LF8-Starter")
                .description("Zuerst GetToken.http in IntelliJ ausführen (client_credentials an http://localhost:9001/default/token). "
                        + "In Swagger oben rechts Authorize wählen und das access_token einfügen; Swagger setzt Bearer automatisch.")
                .version("1.0");
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerScheme));
    }
}
