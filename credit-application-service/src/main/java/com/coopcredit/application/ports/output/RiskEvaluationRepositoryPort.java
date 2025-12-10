package com.coopcredit.application.ports.output;

import com.coopcredit.domain.model.RiskEvaluation;

import java.util.Optional;

/**
 * Puerto de salida para la persistencia de evaluaciones de riesgo
 */
public interface RiskEvaluationRepositoryPort {
    
    RiskEvaluation save(RiskEvaluation riskEvaluation);
    
    Optional<RiskEvaluation> findById(Long id);
    
    Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId);
}

