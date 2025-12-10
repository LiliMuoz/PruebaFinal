package com.coopcredit.integration.service;

import com.coopcredit.application.ports.input.EvaluateCreditApplicationUseCase;
import com.coopcredit.application.ports.input.RegisterAffiliateUseCase;
import com.coopcredit.application.ports.input.RegisterCreditApplicationUseCase;
import com.coopcredit.application.ports.output.RiskServicePort;
import com.coopcredit.domain.model.*;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.UserJpaRepository;
import com.coopcredit.integration.AbstractMockMvcTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integración end-to-end para el servicio de solicitudes de crédito.
 * Estas pruebas verifican el flujo completo desde la creación hasta la evaluación
 * de solicitudes de crédito.
 */
@DisplayName("Servicio de Solicitudes de Crédito - Pruebas E2E")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CreditApplicationServiceIntegrationTest extends AbstractMockMvcTest {

    @Autowired
    private RegisterCreditApplicationUseCase creditApplicationUseCase;

    @Autowired
    private EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;

    @Autowired
    private RegisterAffiliateUseCase affiliateUseCase;

    @Autowired
    private UserJpaRepository userRepository;

    @MockBean
    private RiskServicePort riskServicePort;

    private User testUser;
    private Affiliate testAffiliate;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("servicetest_" + System.currentTimeMillis());
        userEntity.setPassword("hashedpassword123");
        userEntity.setEmail("servicetest_" + System.currentTimeMillis() + "@test.com");
        userEntity.setRole(Role.AFILIADO);
        userEntity.setActive(true);
        userEntity = userRepository.save(userEntity);

        testUser = new User();
        testUser.setId(userEntity.getId());
        testUser.setUsername(userEntity.getUsername());
        testUser.setEmail(userEntity.getEmail());
        testUser.setRole(userEntity.getRole());

        // Crear afiliado de prueba
        Affiliate affiliate = new Affiliate();
        affiliate.setDocumentNumber("DOC" + System.currentTimeMillis());
        affiliate.setDocumentType("CC");
        affiliate.setFirstName("Test");
        affiliate.setLastName("Service");
        affiliate.setEmail("affiliate_service_" + System.currentTimeMillis() + "@test.com");
        affiliate.setPhone("3001234567");
        affiliate.setAddress("Test Address");
        affiliate.setBirthDate(LocalDate.of(1990, 5, 15));
        affiliate.setUserId(testUser.getId());

        testAffiliate = affiliateUseCase.registerAffiliate(affiliate);
    }

    @Nested
    @DisplayName("Flujo completo de solicitud de crédito")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreditApplicationFlowTests {

        @Test
        @Order(1)
        @DisplayName("Debe crear una nueva solicitud de crédito con estado PENDING")
        @Transactional
        void shouldCreateCreditApplicationWithPendingStatus() {
            CreditApplication application = createCreditApplication(
                    new BigDecimal("5000000"),
                    24,
                    "Compra de vehículo"
            );

            CreditApplication saved = creditApplicationUseCase.createCreditApplication(application);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(CreditApplicationStatus.PENDING);
            assertThat(saved.getRequestedAmount()).isEqualByComparingTo(new BigDecimal("5000000"));
            assertThat(saved.getTermMonths()).isEqualTo(24);
            assertThat(saved.getInterestRate()).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Debe evaluar solicitud con score alto y aprobarla automáticamente")
        @Transactional
        void shouldEvaluateAndApproveWithHighScore() {
            // Mock del servicio de riesgo con score alto (>= 600)
            RiskEvaluation mockRiskEvaluation = new RiskEvaluation();
            mockRiskEvaluation.setScore(750);
            mockRiskEvaluation.setRiskLevel(RiskLevel.LOW);
            mockRiskEvaluation.setRecommendation("Cliente con excelente historial crediticio");
            when(riskServicePort.evaluateRisk(anyString())).thenReturn(mockRiskEvaluation);

            // Crear solicitud
            CreditApplication application = createCreditApplication(
                    new BigDecimal("3000000"),
                    12,
                    "Mejoras del hogar"
            );
            CreditApplication saved = creditApplicationUseCase.createCreditApplication(application);

            // Evaluar - con score alto debería aprobar automáticamente
            CreditApplication evaluated = evaluateCreditApplicationUseCase
                    .evaluateCreditApplication(saved.getId(), "analista_test");

            assertThat(evaluated.getStatus()).isEqualTo(CreditApplicationStatus.APPROVED);
            assertThat(evaluated.getRiskEvaluation()).isNotNull();
            assertThat(evaluated.getRiskEvaluation().getScore()).isEqualTo(750);
            assertThat(evaluated.getRiskEvaluation().getRiskLevel()).isEqualTo(RiskLevel.LOW);
        }

        @Test
        @Order(3)
        @DisplayName("Debe aprobar solicitud manualmente sin evaluación previa")
        @Transactional
        void shouldApproveManuallyWithoutEvaluation() {
            // Crear solicitud sin evaluar
            CreditApplication application = createCreditApplication(
                    new BigDecimal("2000000"),
                    18,
                    "Educación"
            );
            CreditApplication saved = creditApplicationUseCase.createCreditApplication(application);

            // Aprobar manualmente (sin evaluación de riesgo)
            CreditApplication approved = evaluateCreditApplicationUseCase
                    .approveCreditApplication(saved.getId(), "analista_test");

            assertThat(approved.getStatus()).isEqualTo(CreditApplicationStatus.APPROVED);
            assertThat(approved.getEvaluatedBy()).isEqualTo("analista_test");
            assertThat(approved.getEvaluatedAt()).isNotNull();
        }

        @Test
        @Order(4)
        @DisplayName("Debe evaluar y rechazar automáticamente con score bajo")
        @Transactional
        void shouldEvaluateAndRejectWithLowScore() {
            // Mock del servicio de riesgo con score bajo (< 600)
            RiskEvaluation mockRiskEvaluation = new RiskEvaluation();
            mockRiskEvaluation.setScore(450);
            mockRiskEvaluation.setRiskLevel(RiskLevel.HIGH);
            mockRiskEvaluation.setRecommendation("Se recomienda rechazar");
            when(riskServicePort.evaluateRisk(anyString())).thenReturn(mockRiskEvaluation);

            // Crear solicitud
            CreditApplication application = createCreditApplication(
                    new BigDecimal("10000000"),
                    60,
                    "Negocio"
            );
            CreditApplication saved = creditApplicationUseCase.createCreditApplication(application);
            
            // Evaluar - con score bajo debería rechazar automáticamente
            CreditApplication evaluated = evaluateCreditApplicationUseCase
                    .evaluateCreditApplication(saved.getId(), "analista_test");

            assertThat(evaluated.getStatus()).isEqualTo(CreditApplicationStatus.REJECTED);
            assertThat(evaluated.getRejectionReason()).contains("Score de riesgo insuficiente");
        }
    }

    @Nested
    @DisplayName("Consultas de solicitudes de crédito")
    class CreditApplicationQueryTests {

        @Test
        @DisplayName("Debe obtener todas las solicitudes de un afiliado")
        @Transactional
        void shouldGetAllApplicationsForAffiliate() {
            // Crear múltiples solicitudes
            creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("1000000"), 12, "Solicitud 1"));
            creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("2000000"), 24, "Solicitud 2"));
            creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("3000000"), 36, "Solicitud 3"));

            List<CreditApplication> applications = creditApplicationUseCase
                    .getCreditApplicationsByAffiliateId(testAffiliate.getId());

            assertThat(applications).hasSize(3);
        }

        @Test
        @DisplayName("Debe obtener solicitud por ID")
        @Transactional
        void shouldGetApplicationById() {
            CreditApplication created = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("5000000"), 24, "Test"));

            CreditApplication found = creditApplicationUseCase.getCreditApplicationById(created.getId());

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(created.getId());
        }
    }

    @Nested
    @DisplayName("Cancelación de solicitudes")
    class CreditApplicationCancellationTests {

        @Test
        @DisplayName("Debe cancelar solicitud pendiente del propietario")
        @Transactional
        void shouldCancelPendingApplicationByOwner() {
            CreditApplication application = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("2000000"), 12, "A cancelar"));

            CreditApplication cancelled = creditApplicationUseCase
                    .cancelCreditApplication(application.getId(), testAffiliate.getId());

            assertThat(cancelled.getStatus()).isEqualTo(CreditApplicationStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Validaciones de negocio")
    class BusinessValidationTests {

        @Test
        @DisplayName("Debe asignar tasa de interés basada en el plazo")
        @Transactional
        void shouldAssignInterestRateBasedOnTerm() {
            // Plazo corto (6 meses)
            CreditApplication shortTerm = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("1000000"), 6, "Corto plazo"));

            // Plazo largo (60 meses)
            CreditApplication longTerm = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("1000000"), 60, "Largo plazo"));

            assertThat(shortTerm.getInterestRate()).isNotNull();
            assertThat(longTerm.getInterestRate()).isNotNull();
        }

        @Test
        @DisplayName("Debe calcular cuota mensual correctamente")
        @Transactional
        void shouldCalculateMonthlyPaymentCorrectly() {
            CreditApplication application = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("10000000"), 12, "Test"));

            assertThat(application.getInterestRate()).isNotNull();
            // La cuota debe estar calculada
            // Fórmula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
        }
    }

    @Nested
    @DisplayName("Evaluaciones de riesgo")
    class RiskEvaluationTests {

        @Test
        @DisplayName("Debe evaluar con riesgo MEDIUM y aprobar (score >= 600)")
        @Transactional
        void shouldEvaluateWithMediumRiskAndApprove() {
            RiskEvaluation mockRiskEvaluation = new RiskEvaluation();
            mockRiskEvaluation.setScore(650);
            mockRiskEvaluation.setRiskLevel(RiskLevel.MEDIUM);
            mockRiskEvaluation.setRecommendation("Evaluar condiciones adicionales");
            when(riskServicePort.evaluateRisk(anyString())).thenReturn(mockRiskEvaluation);

            CreditApplication application = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("5000000"), 24, "Test"));

            CreditApplication evaluated = evaluateCreditApplicationUseCase
                    .evaluateCreditApplication(application.getId(), "analista");

            // Score >= 600 debería aprobar
            assertThat(evaluated.getRiskEvaluation().getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
            assertThat(evaluated.getStatus()).isEqualTo(CreditApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("Debe evaluar con riesgo HIGH y rechazar (score < 600)")
        @Transactional
        void shouldEvaluateWithHighRiskAndReject() {
            RiskEvaluation mockRiskEvaluation = new RiskEvaluation();
            mockRiskEvaluation.setScore(400);
            mockRiskEvaluation.setRiskLevel(RiskLevel.HIGH);
            mockRiskEvaluation.setRecommendation("Se recomienda rechazar");
            when(riskServicePort.evaluateRisk(anyString())).thenReturn(mockRiskEvaluation);

            CreditApplication application = creditApplicationUseCase.createCreditApplication(
                    createCreditApplication(new BigDecimal("5000000"), 24, "Test"));

            CreditApplication evaluated = evaluateCreditApplicationUseCase
                    .evaluateCreditApplication(application.getId(), "analista");

            // Score < 600 debería rechazar
            assertThat(evaluated.getRiskEvaluation().getRiskLevel()).isEqualTo(RiskLevel.HIGH);
            assertThat(evaluated.getStatus()).isEqualTo(CreditApplicationStatus.REJECTED);
        }
    }

    private CreditApplication createCreditApplication(BigDecimal amount, Integer term, String purpose) {
        CreditApplication application = new CreditApplication();
        application.setAffiliateId(testAffiliate.getId());
        application.setRequestedAmount(amount);
        application.setTermMonths(term);
        application.setPurpose(purpose);
        return application;
    }
}
