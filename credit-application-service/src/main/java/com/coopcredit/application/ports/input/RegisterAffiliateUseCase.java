package com.coopcredit.application.ports.input;

import com.coopcredit.domain.model.Affiliate;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para el caso de uso de registro de afiliados
 */
public interface RegisterAffiliateUseCase {
    
    Affiliate registerAffiliate(Affiliate affiliate);
    
    Affiliate getAffiliateById(Long id);
    
    Affiliate getAffiliateByDocumentNumber(String documentNumber);
    
    Affiliate getAffiliateByUserId(Long userId);
    
    Optional<Affiliate> findAffiliateByUserId(Long userId);
    
    Affiliate updateAffiliate(Long id, Affiliate affiliate);
    
    List<Affiliate> getAllAffiliates();
}

