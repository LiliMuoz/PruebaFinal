package com.coopcredit.risk.service;

import com.coopcredit.risk.dto.RiskEvaluationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de evaluación de riesgo simulado.
 * Genera un score basado en el hash del documento para simular
 * un servicio de central de riesgo real.
 */
@Service
public class RiskEvaluationService {
    
    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationService.class);
    
    /**
     * Evalúa el riesgo crediticio basado en el número de documento.
     * El score se genera de forma determinística usando el hash del documento,
     * garantizando que el mismo documento siempre reciba el mismo score.
     */
    public RiskEvaluationResponse evaluateRisk(String documentNumber) {
        log.info("Evaluando riesgo para documento: {}", documentNumber);
        
        // Generar score basado en el hash del documento (determinístico)
        int score = generateScoreFromDocument(documentNumber);
        
        // Determinar nivel de riesgo y recomendación
        String riskLevel;
        String recommendation;
        
        if (score >= 700) {
            riskLevel = "LOW";
            recommendation = "Cliente con excelente historial crediticio. Se recomienda aprobación.";
        } else if (score >= 600) {
            riskLevel = "MEDIUM";
            recommendation = "Cliente con historial crediticio aceptable. Evaluar condiciones adicionales.";
        } else {
            riskLevel = "HIGH";
            recommendation = "Cliente con historial crediticio deficiente. Se recomienda rechazar o solicitar garantías.";
        }
        
        log.info("Evaluación completada - Documento: {}, Score: {}, Nivel: {}", 
                documentNumber, score, riskLevel);
        
        return new RiskEvaluationResponse(documentNumber, score, riskLevel, recommendation);
    }
    
    /**
     * Genera un score crediticio basado en el hash del documento.
     * El score está en el rango 300-850 (similar a scores FICO).
     */
    private int generateScoreFromDocument(String documentNumber) {
        // Usar el hash del documento para generar un score determinístico
        int hash = Math.abs(documentNumber.hashCode());
        
        // Mapear el hash a un rango de score crediticio (300-850)
        int minScore = 300;
        int maxScore = 850;
        int range = maxScore - minScore;
        
        return minScore + (hash % (range + 1));
    }
}

