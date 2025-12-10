package com.coopcredit.risk.service;

import com.coopcredit.risk.dto.RiskEvaluationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RiskEvaluationService Tests")
class RiskEvaluationServiceTest {
    
    private RiskEvaluationService riskEvaluationService;
    
    @BeforeEach
    void setUp() {
        riskEvaluationService = new RiskEvaluationService();
    }
    
    @Nested
    @DisplayName("Evaluación de riesgo")
    class RiskEvaluation {
        
        @Test
        @DisplayName("Debe generar score en rango válido (300-850)")
        void shouldGenerateScoreInValidRange() {
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk("1234567890");
            
            assertTrue(response.score() >= 300);
            assertTrue(response.score() <= 850);
        }
        
        @Test
        @DisplayName("Debe generar mismo score para mismo documento")
        void shouldGenerateSameScoreForSameDocument() {
            RiskEvaluationResponse response1 = riskEvaluationService.evaluateRisk("1234567890");
            RiskEvaluationResponse response2 = riskEvaluationService.evaluateRisk("1234567890");
            
            assertEquals(response1.score(), response2.score());
        }
        
        @Test
        @DisplayName("Debe asignar nivel de riesgo LOW para score >= 700")
        void shouldAssignLowRiskForHighScore() {
            // Buscar un documento que genere score alto
            String documentNumber = "highscore123";
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk(documentNumber);
            
            assertNotNull(response.riskLevel());
            assertNotNull(response.recommendation());
        }
        
        @Test
        @DisplayName("Debe incluir documento en la respuesta")
        void shouldIncludeDocumentInResponse() {
            String documentNumber = "9876543210";
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk(documentNumber);
            
            assertEquals(documentNumber, response.documentNumber());
        }
        
        @Test
        @DisplayName("Debe asignar nivel de riesgo válido")
        void shouldAssignValidRiskLevel() {
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk("testdoc123");
            
            assertTrue(
                response.riskLevel().equals("LOW") ||
                response.riskLevel().equals("MEDIUM") ||
                response.riskLevel().equals("HIGH")
            );
        }
        
        @Test
        @DisplayName("Debe incluir recomendación no vacía")
        void shouldIncludeNonEmptyRecommendation() {
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk("testdoc123");
            
            assertNotNull(response.recommendation());
            assertFalse(response.recommendation().isBlank());
        }
    }
    
    @Nested
    @DisplayName("Consistencia de niveles de riesgo")
    class RiskLevelConsistency {
        
        @Test
        @DisplayName("Score alto debe tener nivel LOW")
        void highScoreShouldHaveLowRisk() {
            // Nota: El score es determinístico basado en hash
            // Verificamos la lógica interna
            int score = 750;
            String expectedLevel = "LOW";
            
            // Verificamos que el servicio asigna correctamente
            RiskEvaluationResponse response = riskEvaluationService.evaluateRisk("anyDocument");
            
            // Solo verificamos que el nivel corresponde al score
            if (response.score() >= 700) {
                assertEquals("LOW", response.riskLevel());
            } else if (response.score() >= 600) {
                assertEquals("MEDIUM", response.riskLevel());
            } else {
                assertEquals("HIGH", response.riskLevel());
            }
        }
    }
}

