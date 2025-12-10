package com.coopcredit.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Clase base abstracta para pruebas de integración con MockMvc.
 * Usa H2 en memoria para las pruebas, lo que permite ejecutar
 * sin necesidad de Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.flyway.enabled=false",
    "risk-service.base-url=http://localhost:8081",
    "risk-service.timeout=5000",
    "jwt.secret=TestSecretKeyForJWTTokenGeneration2024SecureKeyMinimum256BitsForTesting",
    "jwt.expiration=86400000"
})
public abstract class AbstractMockMvcTest {
}
