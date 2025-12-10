package com.coopcredit.application.ports.input;

import com.coopcredit.domain.model.CreditApplication;

/**
 * Puerto de entrada para el caso de uso de evaluación de solicitudes de crédito
 */
public interface EvaluateCreditApplicationUseCase {
    
    CreditApplication evaluateCreditApplication(Long creditApplicationId, String evaluator);
    
    CreditApplication approveCreditApplication(Long creditApplicationId, String evaluator);
    
    CreditApplication rejectCreditApplication(Long creditApplicationId, String evaluator, String reason);
}

