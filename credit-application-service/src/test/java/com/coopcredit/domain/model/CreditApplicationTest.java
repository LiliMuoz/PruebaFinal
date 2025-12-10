package com.coopcredit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreditApplication Domain Tests")
class CreditApplicationTest {
    
    @Nested
    @DisplayName("Validación de monto")
    class AmountValidation {
        
        @Test
        @DisplayName("Debe aceptar monto dentro del rango válido")
        void shouldAcceptValidAmount() {
            CreditApplication application = new CreditApplication();
            application.setRequestedAmount(new BigDecimal("500000"));
            
            assertTrue(application.isValidAmount());
        }
        
        @Test
        @DisplayName("Debe rechazar monto menor al mínimo")
        void shouldRejectAmountBelowMinimum() {
            CreditApplication application = new CreditApplication();
            application.setRequestedAmount(new BigDecimal("50000"));
            
            assertFalse(application.isValidAmount());
        }
        
        @Test
        @DisplayName("Debe rechazar monto mayor al máximo")
        void shouldRejectAmountAboveMaximum() {
            CreditApplication application = new CreditApplication();
            application.setRequestedAmount(new BigDecimal("100000000"));
            
            assertFalse(application.isValidAmount());
        }
        
        @Test
        @DisplayName("Debe rechazar monto nulo")
        void shouldRejectNullAmount() {
            CreditApplication application = new CreditApplication();
            application.setRequestedAmount(null);
            
            assertFalse(application.isValidAmount());
        }
    }
    
    @Nested
    @DisplayName("Validación de plazo")
    class TermValidation {
        
        @Test
        @DisplayName("Debe aceptar plazo dentro del rango válido")
        void shouldAcceptValidTerm() {
            CreditApplication application = new CreditApplication();
            application.setTermMonths(24);
            
            assertTrue(application.isValidTerm());
        }
        
        @Test
        @DisplayName("Debe rechazar plazo menor al mínimo")
        void shouldRejectTermBelowMinimum() {
            CreditApplication application = new CreditApplication();
            application.setTermMonths(3);
            
            assertFalse(application.isValidTerm());
        }
        
        @Test
        @DisplayName("Debe rechazar plazo mayor al máximo")
        void shouldRejectTermAboveMaximum() {
            CreditApplication application = new CreditApplication();
            application.setTermMonths(72);
            
            assertFalse(application.isValidTerm());
        }
    }
    
    @Nested
    @DisplayName("Reglas de evaluación")
    class EvaluationRules {
        
        @Test
        @DisplayName("Debe poder evaluarse si está pendiente")
        void shouldBeEvaluableWhenPending() {
            CreditApplication application = new CreditApplication();
            application.setStatus(CreditApplicationStatus.PENDING);
            
            assertTrue(application.canBeEvaluated());
        }
        
        @Test
        @DisplayName("No debe poder evaluarse si ya está aprobada")
        void shouldNotBeEvaluableWhenApproved() {
            CreditApplication application = new CreditApplication();
            application.setStatus(CreditApplicationStatus.APPROVED);
            
            assertFalse(application.canBeEvaluated());
        }
        
        @Test
        @DisplayName("Debe aprobarse con score suficiente")
        void shouldBeApprovableWithSufficientScore() {
            CreditApplication application = new CreditApplication();
            
            RiskEvaluation riskEvaluation = new RiskEvaluation();
            riskEvaluation.setScore(700);
            application.setRiskEvaluation(riskEvaluation);
            
            assertTrue(application.canBeApproved());
        }
        
        @Test
        @DisplayName("No debe aprobarse con score insuficiente")
        void shouldNotBeApprovableWithInsufficientScore() {
            CreditApplication application = new CreditApplication();
            
            RiskEvaluation riskEvaluation = new RiskEvaluation();
            riskEvaluation.setScore(500);
            application.setRiskEvaluation(riskEvaluation);
            
            assertFalse(application.canBeApproved());
        }
    }
    
    @Nested
    @DisplayName("Cálculo de cuota mensual")
    class MonthlyPaymentCalculation {
        
        @Test
        @DisplayName("Debe calcular cuota mensual correctamente")
        void shouldCalculateMonthlyPaymentCorrectly() {
            CreditApplication application = new CreditApplication();
            application.setRequestedAmount(new BigDecimal("1000000"));
            application.setInterestRate(new BigDecimal("12"));
            application.setTermMonths(12);
            
            BigDecimal monthlyPayment = application.calculateMonthlyPayment();
            
            // La cuota debería ser aproximadamente 88,849
            assertTrue(monthlyPayment.compareTo(new BigDecimal("88000")) > 0);
            assertTrue(monthlyPayment.compareTo(new BigDecimal("90000")) < 0);
        }
        
        @Test
        @DisplayName("Debe retornar cero si faltan datos")
        void shouldReturnZeroWhenDataMissing() {
            CreditApplication application = new CreditApplication();
            
            BigDecimal monthlyPayment = application.calculateMonthlyPayment();
            
            assertEquals(BigDecimal.ZERO, monthlyPayment);
        }
    }
    
    @Nested
    @DisplayName("Cambios de estado")
    class StatusChanges {
        
        @Test
        @DisplayName("Debe aprobar correctamente")
        void shouldApproveCorrectly() {
            CreditApplication application = new CreditApplication();
            application.setStatus(CreditApplicationStatus.PENDING);
            
            application.approve("analista1");
            
            assertEquals(CreditApplicationStatus.APPROVED, application.getStatus());
            assertEquals("analista1", application.getEvaluatedBy());
            assertNotNull(application.getEvaluatedAt());
        }
        
        @Test
        @DisplayName("Debe rechazar correctamente")
        void shouldRejectCorrectly() {
            CreditApplication application = new CreditApplication();
            application.setStatus(CreditApplicationStatus.PENDING);
            
            application.reject("analista1", "Score insuficiente");
            
            assertEquals(CreditApplicationStatus.REJECTED, application.getStatus());
            assertEquals("analista1", application.getEvaluatedBy());
            assertEquals("Score insuficiente", application.getRejectionReason());
            assertNotNull(application.getEvaluatedAt());
        }
    }
}

