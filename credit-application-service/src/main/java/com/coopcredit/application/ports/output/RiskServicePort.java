package com.coopcredit.application.ports.output;

import com.coopcredit.domain.model.RiskEvaluation;

/**
 * Puerto de salida para el servicio externo de riesgo
 */
public interface RiskServicePort {
    
    RiskEvaluation evaluateRisk(String documentNumber);
}

