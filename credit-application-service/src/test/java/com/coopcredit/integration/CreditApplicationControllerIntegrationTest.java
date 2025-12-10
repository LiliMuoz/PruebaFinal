package com.coopcredit.integration;

import com.coopcredit.application.ports.output.RiskServicePort;
import com.coopcredit.domain.model.CreditApplicationStatus;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.domain.model.RiskLevel;
import com.coopcredit.domain.model.Role;
import com.coopcredit.infrastructure.adapters.input.rest.dto.*;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CreditApplicationController.
 * Estas pruebas verifican el flujo completo de gestión de solicitudes de crédito
 * incluyendo creación, evaluación, aprobación y rechazo.
 */
@DisplayName("CreditApplicationController - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CreditApplicationControllerIntegrationTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RiskServicePort riskServicePort;

    private String affiliateToken;
    private String analistaToken;
    private Long createdApplicationId;

    @BeforeEach
    void setUp() throws Exception {
        // Configurar mock del servicio de riesgo
        RiskEvaluation mockRiskEvaluation = new RiskEvaluation();
        mockRiskEvaluation.setScore(750);
        mockRiskEvaluation.setRiskLevel(RiskLevel.LOW);
        mockRiskEvaluation.setRecommendation("Cliente con excelente historial crediticio");
        mockRiskEvaluation.setEvaluatedAt(LocalDateTime.now());
        when(riskServicePort.evaluateRisk(anyString())).thenReturn(mockRiskEvaluation);
    }

    @Nested
    @DisplayName("Flujo completo de solicitud de crédito")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreditApplicationFlowTests {

        private String flowToken;
        private String flowUniqueId;

        @Test
        @Order(1)
        @DisplayName("Flujo completo: Registrar, crear afiliado y solicitar crédito")
        void shouldCompleteFullCreditApplicationFlow() throws Exception {
            flowUniqueId = String.valueOf(System.currentTimeMillis());
            
            // Paso 1: Registrar usuario
            RegisterRequest registerRequest = new RegisterRequest(
                    "flowuser_" + flowUniqueId,
                    "password123",
                    "flow_" + flowUniqueId + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            // Paso 2: Login
            AuthRequest loginRequest = new AuthRequest("flowuser_" + flowUniqueId, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            flowToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Paso 3: Crear perfil de afiliado
            AffiliateRequest affiliateRequest = new AffiliateRequest(
                    "DOC" + flowUniqueId,
                    "CC",
                    "Juan",
                    "Pérez",
                    "juan_" + flowUniqueId + "@test.com",
                    "3001234567",
                    "Calle 123 #45-67",
                    LocalDate.of(1990, 1, 15),
                    null
            );

            mockMvc.perform(post("/api/affiliates")
                            .header("Authorization", "Bearer " + flowToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(affiliateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.documentNumber").value("DOC" + flowUniqueId))
                    .andExpect(jsonPath("$.firstName").value("Juan"))
                    .andExpect(jsonPath("$.lastName").value("Pérez"));

            // Paso 4: Crear solicitud de crédito
            CreditApplicationRequest creditRequest = new CreditApplicationRequest(
                    new BigDecimal("5000000"),
                    24,
                    "Compra de vehículo"
            );

            MvcResult result = mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + flowToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creditRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requestedAmount").value(5000000))
                    .andExpect(jsonPath("$.termMonths").value(24))
                    .andExpect(jsonPath("$.purpose").value("Compra de vehículo"))
                    .andExpect(jsonPath("$.status").value(CreditApplicationStatus.PENDING.name()))
                    .andReturn();

            createdApplicationId = objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("id").asLong();
        }
    }

    @Nested
    @DisplayName("Validaciones de solicitudes de crédito")
    class CreditApplicationValidationTests {

        private String testToken;
        private String testUniqueId;

        @BeforeEach
        void setUp() throws Exception {
            testUniqueId = String.valueOf(System.currentTimeMillis());

            // Registrar usuario
            RegisterRequest request = new RegisterRequest(
                    "validuser_" + testUniqueId,
                    "password123",
                    "valid_" + testUniqueId + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Login
            AuthRequest loginRequest = new AuthRequest("validuser_" + testUniqueId, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            testToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Crear perfil de afiliado
            AffiliateRequest affiliateRequest = new AffiliateRequest(
                    "DOC" + testUniqueId,
                    "CC",
                    "Test",
                    "User",
                    "testuser_" + testUniqueId + "@test.com",
                    "3001234567",
                    "Test Address",
                    LocalDate.of(1985, 5, 20),
                    null
            );

            mockMvc.perform(post("/api/affiliates")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(affiliateRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Debe rechazar monto menor al mínimo")
        void shouldRejectAmountBelowMinimum() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("50000"), // mínimo es 100,000
                    12,
                    "Test"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe rechazar monto mayor al máximo")
        void shouldRejectAmountAboveMaximum() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("100000000"), // máximo es 50,000,000
                    12,
                    "Test"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe rechazar plazo menor al mínimo")
        void shouldRejectTermBelowMinimum() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("1000000"),
                    3, // mínimo es 6 meses
                    "Test"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe rechazar plazo mayor al máximo")
        void shouldRejectTermAboveMaximum() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("1000000"),
                    72, // máximo es 60 meses
                    "Test"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe aceptar solicitud con valores válidos")
        void shouldAcceptValidCreditApplication() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("1000000"),
                    12,
                    "Propósito de prueba"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .header("Authorization", "Bearer " + testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(CreditApplicationStatus.PENDING.name()));
        }
    }

    @Nested
    @DisplayName("Control de acceso por roles")
    class RoleBasedAccessTests {

        @Test
        @DisplayName("Usuario AFILIADO no puede ver todas las solicitudes")
        void affiliateShouldNotAccessAllApplications() throws Exception {
            String uniqueId = String.valueOf(System.currentTimeMillis());

            // Registrar usuario
            RegisterRequest request = new RegisterRequest(
                    "roletest_" + uniqueId,
                    "password123",
                    "roletest_" + uniqueId + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Login
            AuthRequest loginRequest = new AuthRequest("roletest_" + uniqueId, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Intentar acceder a todas las solicitudes (solo ANALISTA/ADMIN)
            mockMvc.perform(get("/api/credit-applications")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Usuario ANALISTA puede ver todas las solicitudes")
        void analystShouldAccessAllApplications() throws Exception {
            String uniqueId = String.valueOf(System.currentTimeMillis());

            // Crear usuario directamente como ANALISTA
            UserEntity analistaEntity = new UserEntity();
            analistaEntity.setUsername("analista_" + uniqueId);
            analistaEntity.setPassword(passwordEncoder.encode("password123"));
            analistaEntity.setEmail("analista_" + uniqueId + "@test.com");
            analistaEntity.setRole(Role.ANALISTA);
            analistaEntity.setActive(true);
            userRepository.save(analistaEntity);

            // Login
            AuthRequest loginRequest = new AuthRequest("analista_" + uniqueId, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Acceder a todas las solicitudes
            mockMvc.perform(get("/api/credit-applications")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Endpoints sin autenticación")
    class UnauthenticatedAccessTests {

        @Test
        @DisplayName("Debe rechazar creación sin token")
        void shouldRejectCreationWithoutToken() throws Exception {
            CreditApplicationRequest request = new CreditApplicationRequest(
                    new BigDecimal("1000000"),
                    12,
                    "Test"
            );

            mockMvc.perform(post("/api/credit-applications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError()); // 401 o 403
        }

        @Test
        @DisplayName("Debe rechazar consulta sin token")
        void shouldRejectQueryWithoutToken() throws Exception {
            mockMvc.perform(get("/api/credit-applications"))
                    .andExpect(status().is4xxClientError()); // 401 o 403
        }
    }
}
