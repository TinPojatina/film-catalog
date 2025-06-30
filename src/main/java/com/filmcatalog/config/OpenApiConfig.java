package com.filmcatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracija za OpenAPI/Swagger dokumentaciju - OPTIMIZED FOR DOCKER
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("film-catalog-api")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * Informacije o API-ju - SIMPLIFIED
     */
    private Info apiInfo() {
        return new Info()
                .title("Film Catalog API")
                .version(appVersion)
                .description("""
                    **CRUD API za upravljanje katalogom filmova i glumaca**
                    """);
    }
}