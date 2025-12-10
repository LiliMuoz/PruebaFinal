package com.coopcredit.application.ports.input;

import com.coopcredit.domain.model.CreditApplication;

import java.util.List;

/**
 * Puerto de entrada para el caso de uso de registro de solicitudes de crédito
 */
public interface RegisterCreditApplicationUseCase {
    
    CreditApplication createCreditApplication(CreditApplication creditApplication);
    
    CreditApplication getCreditApplicationById(Long id);
    
    List<CreditApplication> getCreditApplicationsByAffiliateId(Long affiliateId);
    
    List<CreditApplication> getAllCreditApplications();
    
    CreditApplication cancelCreditApplication(Long id, Long affiliateId);
}

