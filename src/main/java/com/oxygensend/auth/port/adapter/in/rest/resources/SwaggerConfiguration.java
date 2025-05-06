package com.oxygensend.auth.port.adapter.in.rest.resources;

import com.oxygensend.auth.port.Ports;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Profile(Ports.REST)
@Configuration
public class SwaggerConfiguration {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                                                     addList("Bearer Authentication"))
                            .components(new Components().addSecuritySchemes
                                                                ("Bearer Authentication", createAPIKeyScheme()))
                            .info(new Info().title("Auth API")
                                            .description("Microservice for authentication and authorization management.")
                                            .version("1.0").contact(new Contact().name("Szymon Berdzik")
                                                                                 .email("szymon19314@gmail.com"))
                                            .license(new License().name("License of API")
                                                                  .url("API license URL")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .bearerFormat("JWT")
                                   .scheme("bearer");
    }

}
