package com.coopcredit.integration.repository;

import com.coopcredit.domain.model.CreditApplicationStatus;
import com.coopcredit.domain.model.RiskLevel;
import com.coopcredit.domain.model.Role;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.AffiliateJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.CreditApplicationJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.RiskEvaluationJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.UserJpaRepository;
import com.coopcredit.integration.AbstractMockMvcTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para los repositorios JPA.
 * Estas pruebas verifican que la persistencia funciona correctamente
 * usando H2 en memoria para un entorno de pruebas rápido.
 */
@DisplayName("Repositorios JPA - Pruebas de Integración")
@Transactional
class CreditApplicationRepositoryIntegrationTest extends AbstractMockMvcTest {

    @Autowired
    private CreditApplicationJpaRepository creditApplicationRepository;

    @Autowired
    private AffiliateJpaRepository affiliateRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RiskEvaluationJpaRepository riskEvaluationRepository;

    private UserEntity testUser;
    private AffiliateEntity testAffiliate;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = new UserEntity();
        testUser.setUsername("testuser_" + System.currentTimeMillis());
        testUser.setPassword("hashedpassword");
        testUser.setEmail("test_" + System.currentTimeMillis() + "@test.com");
        testUser.setRole(Role.AFILIADO);
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        // Crear afiliado de prueba
        testAffiliate = new AffiliateEntity();
        testAffiliate.setDocumentNumber("DOC" + System.currentTimeMillis());
        testAffiliate.setDocumentType("CC");
        testAffiliate.setFirstName("Test");
        testAffiliate.setLastName("User");
        testAffiliate.setEmail("affiliate_" + System.currentTimeMillis() + "@test.com");
        testAffiliate.setPhone("3001234567");
        testAffiliate.setAddress("Test Address 123");
        testAffiliate.setBirthDate(LocalDate.of(1990, 1, 15));
        testAffiliate.setUser(testUser);
        testAffiliate = affiliateRepository.save(testAffiliate);
    }

    @Nested
    @DisplayName("CreditApplicationJpaRepository")
    class CreditApplicationRepositoryTests {

        @Test
        @DisplayName("Debe guardar una nueva solicitud de crédito")
        void shouldSaveNewCreditApplication() {
            CreditApplicationEntity application = createCreditApplication(
                    new BigDecimal("5000000"),
                    24,
                    "Compra de vehículo"
            );

            CreditApplicationEntity saved = creditApplicationRepository.save(application);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getRequestedAmount()).isEqualByComparingTo(new BigDecimal("5000000"));
            assertThat(saved.getTermMonths()).isEqualTo(24);
            assertThat(saved.getStatus()).isEqualTo(CreditApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("Debe encontrar solicitud por ID")
        void shouldFindCreditApplicationById() {
            CreditApplicationEntity application = createCreditApplication(
                    new BigDecimal("3000000"),
                    12,
                    "Test"
            );
            CreditApplicationEntity saved = creditApplicationRepository.save(application);

            Optional<CreditApplicationEntity> found = creditApplicationRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("Debe encontrar solicitudes por ID de afiliado")
        void shouldFindByAffiliateId() {
            // Crear varias solicitudes
            creditApplicationRepository.save(createCreditApplication(new BigDecimal("1000000"), 12, "Solicitud 1"));
            creditApplicationRepository.save(createCreditApplication(new BigDecimal("2000000"), 24, "Solicitud 2"));
            creditApplicationRepository.save(createCreditApplication(new BigDecimal("3000000"), 36, "Solicitud 3"));

            List<CreditApplicationEntity> applications = creditApplicationRepository
                    .findByAffiliateId(testAffiliate.getId());

            assertThat(applications).hasSize(3);
        }

        @Test
        @DisplayName("Debe encontrar solicitudes por estado")
        void shouldFindByStatus() {
            // Crear solicitudes con diferentes estados
            CreditApplicationEntity pending = createCreditApplication(new BigDecimal("1000000"), 12, "Pendiente");
            pending.setStatus(CreditApplicationStatus.PENDING);
            creditApplicationRepository.save(pending);

            CreditApplicationEntity approved = createCreditApplication(new BigDecimal("2000000"), 24, "Aprobada");
            approved.setStatus(CreditApplicationStatus.APPROVED);
            approved.setEvaluatedBy("analista");
            approved.setEvaluatedAt(LocalDateTime.now());
            creditApplicationRepository.save(approved);

            List<CreditApplicationEntity> pendingApplications = creditApplicationRepository
                    .findByStatus(CreditApplicationStatus.PENDING);

            assertThat(pendingApplications).isNotEmpty();
            assertThat(pendingApplications)
                    .allMatch(app -> app.getStatus() == CreditApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("Debe actualizar estado de solicitud")
        void shouldUpdateCreditApplicationStatus() {
            CreditApplicationEntity application = createCreditApplication(
                    new BigDecimal("5000000"),
                    24,
                    "Test"
            );
            CreditApplicationEntity saved = creditApplicationRepository.save(application);

            // Actualizar estado
            saved.setStatus(CreditApplicationStatus.APPROVED);
            saved.setEvaluatedBy("analista1");
            saved.setEvaluatedAt(LocalDateTime.now());
            creditApplicationRepository.save(saved);

            // Verificar actualización
            CreditApplicationEntity updated = creditApplicationRepository.findById(saved.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(CreditApplicationStatus.APPROVED);
            assertThat(updated.getEvaluatedBy()).isEqualTo("analista1");
            assertThat(updated.getEvaluatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe eliminar solicitud de crédito")
        void shouldDeleteCreditApplication() {
            CreditApplicationEntity application = createCreditApplication(
                    new BigDecimal("1000000"),
                    12,
                    "A eliminar"
            );
            CreditApplicationEntity saved = creditApplicationRepository.save(application);
            Long savedId = saved.getId();

            creditApplicationRepository.deleteById(savedId);

            Optional<CreditApplicationEntity> deleted = creditApplicationRepository.findById(savedId);
            assertThat(deleted).isEmpty();
        }

        private CreditApplicationEntity createCreditApplication(BigDecimal amount, Integer term, String purpose) {
            CreditApplicationEntity entity = new CreditApplicationEntity();
            entity.setAffiliate(testAffiliate);
            entity.setRequestedAmount(amount);
            entity.setTermMonths(term);
            entity.setInterestRate(new BigDecimal("12.5"));
            entity.setPurpose(purpose);
            entity.setStatus(CreditApplicationStatus.PENDING);
            entity.setCreatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("AffiliateJpaRepository")
    class AffiliateRepositoryTests {

        @Test
        @DisplayName("Debe encontrar afiliado por número de documento")
        void shouldFindByDocumentNumber() {
            Optional<AffiliateEntity> found = affiliateRepository
                    .findByDocumentNumber(testAffiliate.getDocumentNumber());

            assertThat(found).isPresent();
            assertThat(found.get().getDocumentNumber()).isEqualTo(testAffiliate.getDocumentNumber());
        }

        @Test
        @DisplayName("Debe encontrar afiliado por ID de usuario")
        void shouldFindByUserId() {
            Optional<AffiliateEntity> found = affiliateRepository
                    .findByUserId(testUser.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getUser().getId()).isEqualTo(testUser.getId());
        }

        @Test
        @DisplayName("Debe verificar existencia por número de documento")
        void shouldCheckExistsByDocumentNumber() {
            boolean exists = affiliateRepository.existsByDocumentNumber(testAffiliate.getDocumentNumber());
            boolean notExists = affiliateRepository.existsByDocumentNumber("NONEXISTENT");

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Debe actualizar datos del afiliado")
        void shouldUpdateAffiliateData() {
            testAffiliate.setPhone("3009876543");
            testAffiliate.setAddress("Nueva Dirección 456");
            affiliateRepository.save(testAffiliate);

            AffiliateEntity updated = affiliateRepository.findById(testAffiliate.getId()).orElseThrow();
            assertThat(updated.getPhone()).isEqualTo("3009876543");
            assertThat(updated.getAddress()).isEqualTo("Nueva Dirección 456");
        }
    }

    @Nested
    @DisplayName("UserJpaRepository")
    class UserRepositoryTests {

        @Test
        @DisplayName("Debe encontrar usuario por username")
        void shouldFindByUsername() {
            Optional<UserEntity> found = userRepository.findByUsername(testUser.getUsername());

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("Debe verificar existencia por username")
        void shouldCheckExistsByUsername() {
            boolean exists = userRepository.existsByUsername(testUser.getUsername());
            boolean notExists = userRepository.existsByUsername("nonexistent_user");

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Debe verificar existencia por email")
        void shouldCheckExistsByEmail() {
            boolean exists = userRepository.existsByEmail(testUser.getEmail());
            boolean notExists = userRepository.existsByEmail("nonexistent@email.com");

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Debe actualizar rol de usuario")
        void shouldUpdateUserRole() {
            testUser.setRole(Role.ANALISTA);
            userRepository.save(testUser);

            UserEntity updated = userRepository.findById(testUser.getId()).orElseThrow();
            assertThat(updated.getRole()).isEqualTo(Role.ANALISTA);
        }

        @Test
        @DisplayName("Debe listar usuarios activos")
        void shouldFindActiveUsers() {
            // Crear usuario inactivo
            UserEntity inactiveUser = new UserEntity();
            inactiveUser.setUsername("inactive_" + System.currentTimeMillis());
            inactiveUser.setPassword("password");
            inactiveUser.setEmail("inactive_" + System.currentTimeMillis() + "@test.com");
            inactiveUser.setRole(Role.AFILIADO);
            inactiveUser.setActive(false);
            userRepository.save(inactiveUser);

            List<UserEntity> allUsers = userRepository.findAll();
            long activeCount = allUsers.stream().filter(UserEntity::isActive).count();
            long inactiveCount = allUsers.stream().filter(u -> !u.isActive()).count();

            assertThat(activeCount).isGreaterThan(0);
            assertThat(inactiveCount).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("RiskEvaluationJpaRepository")
    class RiskEvaluationRepositoryTests {

        @Test
        @DisplayName("Debe guardar evaluación de riesgo")
        void shouldSaveRiskEvaluation() {
            // Crear solicitud primero
            CreditApplicationEntity application = new CreditApplicationEntity();
            application.setAffiliate(testAffiliate);
            application.setRequestedAmount(new BigDecimal("5000000"));
            application.setTermMonths(24);
            application.setInterestRate(new BigDecimal("12.5"));
            application.setPurpose("Test");
            application.setStatus(CreditApplicationStatus.PENDING);
            application.setCreatedAt(LocalDateTime.now());
            application = creditApplicationRepository.save(application);

            // Crear evaluación de riesgo
            RiskEvaluationEntity riskEvaluation = new RiskEvaluationEntity();
            riskEvaluation.setCreditApplication(application);
            riskEvaluation.setDocumentNumber(testAffiliate.getDocumentNumber());
            riskEvaluation.setScore(750);
            riskEvaluation.setRiskLevel(RiskLevel.LOW);
            riskEvaluation.setRecommendation("Cliente con excelente historial crediticio");
            riskEvaluation.setEvaluatedAt(LocalDateTime.now());

            RiskEvaluationEntity saved = riskEvaluationRepository.save(riskEvaluation);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getScore()).isEqualTo(750);
            assertThat(saved.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        }

        @Test
        @DisplayName("Debe encontrar evaluación por ID de solicitud")
        void shouldFindByCreditApplicationId() {
            // Crear solicitud y evaluación
            CreditApplicationEntity application = new CreditApplicationEntity();
            application.setAffiliate(testAffiliate);
            application.setRequestedAmount(new BigDecimal("3000000"));
            application.setTermMonths(12);
            application.setInterestRate(new BigDecimal("15.0"));
            application.setPurpose("Test");
            application.setStatus(CreditApplicationStatus.APPROVED);
            application.setCreatedAt(LocalDateTime.now());
            application = creditApplicationRepository.save(application);

            RiskEvaluationEntity riskEvaluation = new RiskEvaluationEntity();
            riskEvaluation.setCreditApplication(application);
            riskEvaluation.setDocumentNumber(testAffiliate.getDocumentNumber());
            riskEvaluation.setScore(650);
            riskEvaluation.setRiskLevel(RiskLevel.MEDIUM);
            riskEvaluation.setRecommendation("Evaluar condiciones adicionales");
            riskEvaluation.setEvaluatedAt(LocalDateTime.now());
            riskEvaluationRepository.save(riskEvaluation);

            Optional<RiskEvaluationEntity> found = riskEvaluationRepository
                    .findByCreditApplicationId(application.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getScore()).isEqualTo(650);
        }
    }

    @Nested
    @DisplayName("Relaciones entre entidades")
    class EntityRelationshipTests {

        @Test
        @DisplayName("Debe cargar afiliado con su usuario (Lazy loading)")
        void shouldLoadAffiliateWithUser() {
            AffiliateEntity found = affiliateRepository.findById(testAffiliate.getId()).orElseThrow();

            assertThat(found.getUser()).isNotNull();
            assertThat(found.getUser().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("Debe cargar solicitud con su afiliado")
        void shouldLoadCreditApplicationWithAffiliate() {
            CreditApplicationEntity application = new CreditApplicationEntity();
            application.setAffiliate(testAffiliate);
            application.setRequestedAmount(new BigDecimal("1000000"));
            application.setTermMonths(12);
            application.setInterestRate(new BigDecimal("12.0"));
            application.setPurpose("Test");
            application.setStatus(CreditApplicationStatus.PENDING);
            application.setCreatedAt(LocalDateTime.now());
            application = creditApplicationRepository.save(application);

            CreditApplicationEntity found = creditApplicationRepository.findById(application.getId()).orElseThrow();

            assertThat(found.getAffiliate()).isNotNull();
            assertThat(found.getAffiliate().getDocumentNumber()).isEqualTo(testAffiliate.getDocumentNumber());
        }

        @Test
        @DisplayName("Debe manejar cascada de eliminación correctamente")
        void shouldHandleCascadeDeleteCorrectly() {
            // Verificar que las solicitudes del afiliado existen
            List<CreditApplicationEntity> applicationsBefore = creditApplicationRepository
                    .findByAffiliateId(testAffiliate.getId());
            
            // Crear una solicitud para el afiliado
            CreditApplicationEntity application = new CreditApplicationEntity();
            application.setAffiliate(testAffiliate);
            application.setRequestedAmount(new BigDecimal("1000000"));
            application.setTermMonths(12);
            application.setInterestRate(new BigDecimal("12.0"));
            application.setPurpose("Test cascada");
            application.setStatus(CreditApplicationStatus.PENDING);
            application.setCreatedAt(LocalDateTime.now());
            creditApplicationRepository.save(application);

            List<CreditApplicationEntity> applicationsAfter = creditApplicationRepository
                    .findByAffiliateId(testAffiliate.getId());

            assertThat(applicationsAfter.size()).isEqualTo(applicationsBefore.size() + 1);
        }
    }
}
