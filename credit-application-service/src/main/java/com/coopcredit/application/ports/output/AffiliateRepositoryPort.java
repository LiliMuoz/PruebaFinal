package com.coopcredit.application.ports.output;

import com.coopcredit.domain.model.Affiliate;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de afiliados
 */
public interface AffiliateRepositoryPort {
    
    Affiliate save(Affiliate affiliate);
    
    Optional<Affiliate> findById(Long id);
    
    Optional<Affiliate> findByDocumentNumber(String documentNumber);
    
    Optional<Affiliate> findByUserId(Long userId);
    
    boolean existsByDocumentNumber(String documentNumber);
    
    boolean existsByEmail(String email);
    
    void deleteById(Long id);
    
    List<Affiliate> findAll();
}

