package com.filmcatalog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Konfiguracija za OpenAPI/Swagger dokumentaciju
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Glavna OpenAPI konfiguracija
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .components(securityComponents());
    }

    /**
     * Informacije o API-ju
     */
    private Info apiInfo() {
        return new Info()
                .title("Film Catalog API")
                .version(appVersion)
                .description("""
                    **CRUD API za upravljanje katalogom filmova i glumaca**
                    
                    Ova aplikacija omogućuje:
                    - 🎬 Upravljanje filmovima (CRUD operacije)
                    - 🎭 Upravljanje glumcima (CRUD operacije)
                    - 🔗 Many-to-Many veze između filmova i glumaca
                    - 🔍 Napredne mogućnosti filtriranja
                    - 📊 Paginaciju i sortiranje
                    - 📈 Osnovne statistike
                    
                    **Tehnologije:**
                    - Spring Boot 3.x
                    - PostgreSQL
                    - Docker & Docker Compose
                    - Flyway za migracije baze podataka
                    """)
                .contact(contactInfo())
                .license(licenseInfo());
    }

    /**
     * Kontakt informacije
     */
    private Contact contactInfo() {
        return new Contact()
                .name("Film Catalog Development Team")
                .email("support@filmcatalog.com")
                .url("https://github.com/your-username/film-catalog");
    }

    /**
     * Licencne informacije
     */
    private License licenseInfo() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Lista servera
     */
    private java.util.List<Server> serverList() {
        return Arrays.asList(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Development server (lokalni)"),
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Docker server (lokalni container)"),
                new Server()
                        .url("https://api.filmcatalog.com")
                        .description("Production server")
        );
    }

    /**
     * Security komponente (za buduće proširenje s autentifikacijom)
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token autentifikacija"))
                .addSecuritySchemes("apiKey",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("API Key autentifikacija"));
    }
}