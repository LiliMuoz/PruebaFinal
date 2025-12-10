package com.coopcredit.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "CoopCredit API",
        version = "1.0.0",
        description = "API REST para el sistema de gestión de solicitudes de crédito de CoopCredit. " +
                      "Este servicio permite la gestión de afiliados, solicitudes de crédito y evaluación de riesgo.",
        contact = @Contact(
            name = "CoopCredit Team",
            email = "support@coopcredit.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor de desarrollo"),
        @Server(url = "http://credit-application-service:8080", description = "Servidor Docker")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Ingrese el token JWT obtenido del endpoint /api/auth/login"
)
public class OpenApiConfig {
}

