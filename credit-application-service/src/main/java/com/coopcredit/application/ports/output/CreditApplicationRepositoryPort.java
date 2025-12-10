package com.coopcredit.application.ports.output;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.CreditApplicationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de solicitudes de crédito
 */
public interface CreditApplicationRepositoryPort {
    
    CreditApplication save(CreditApplication creditApplication);
    
    Optional<CreditApplication> findById(Long id);
    
    List<CreditApplication> findByAffiliateId(Long affiliateId);
    
    List<CreditApplication> findAll();
    
    List<CreditApplication> findByStatus(CreditApplicationStatus status);
    
    void deleteById(Long id);
}

