package com.coopcredit.application.usecases;

import com.coopcredit.application.ports.input.RegisterAffiliateUseCase;
import com.coopcredit.application.ports.output.AffiliateRepositoryPort;
import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.DuplicateResourceException;
import com.coopcredit.domain.exception.InvalidOperationException;
import com.coopcredit.domain.model.Affiliate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del caso de uso de registro de afiliados
 */
@Service
@Transactional
public class RegisterAffiliateService implements RegisterAffiliateUseCase {
    
    private final AffiliateRepositoryPort affiliateRepository;
    
    public RegisterAffiliateService(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }
    
    @Override
    public Affiliate registerAffiliate(Affiliate affiliate) {
        // Validaciones de negocio
        if (!affiliate.hasValidDocument()) {
            throw new InvalidOperationException("El documento del afiliado es inválido");
        }
        
        if (!affiliate.isAdult()) {
            throw new InvalidOperationException("El afiliado debe ser mayor de edad");
        }
        
        // Verificar duplicados
        if (affiliateRepository.existsByDocumentNumber(affiliate.getDocumentNumber())) {
            throw new DuplicateResourceException("Ya existe un afiliado con el documento: " + affiliate.getDocumentNumber());
        }
        
        if (affiliateRepository.existsByEmail(affiliate.getEmail())) {
            throw new DuplicateResourceException("Ya existe un afiliado con el email: " + affiliate.getEmail());
        }
        
        // Establecer timestamps
        affiliate.setCreatedAt(LocalDateTime.now());
        affiliate.setUpdatedAt(LocalDateTime.now());
        
        return affiliateRepository.save(affiliate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Affiliate getAffiliateById(Long id) {
        return affiliateRepository.findById(id)
                .orElseThrow(() -> new AffiliateNotFoundException(id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Affiliate getAffiliateByDocumentNumber(String documentNumber) {
        return affiliateRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Affiliate getAffiliateByUserId(Long userId) {
        return affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new AffiliateNotFoundException("Usuario no tiene afiliado asociado"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Affiliate> findAffiliateByUserId(Long userId) {
        return affiliateRepository.findByUserId(userId);
    }
    
    @Override
    public Affiliate updateAffiliate(Long id, Affiliate affiliate) {
        Affiliate existingAffiliate = getAffiliateById(id);
        
        // Actualizar campos permitidos
        existingAffiliate.setFirstName(affiliate.getFirstName());
        existingAffiliate.setLastName(affiliate.getLastName());
        existingAffiliate.setPhone(affiliate.getPhone());
        existingAffiliate.setAddress(affiliate.getAddress());
        existingAffiliate.setUpdatedAt(LocalDateTime.now());
        
        return affiliateRepository.save(existingAffiliate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Affiliate> getAllAffiliates() {
        return affiliateRepository.findAll();
    }
}

