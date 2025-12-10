package com.coopcredit.application.usecases;

import com.coopcredit.application.ports.input.RegisterCreditApplicationUseCase;
import com.coopcredit.application.ports.output.AffiliateRepositoryPort;
import com.coopcredit.application.ports.output.CreditApplicationRepositoryPort;
import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.CreditApplicationNotFoundException;
import com.coopcredit.domain.exception.InvalidOperationException;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.CreditApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del caso de uso de registro de solicitudes de crédito
 */
@Service
@Transactional
public class RegisterCreditApplicationService implements RegisterCreditApplicationUseCase {
    
    private final CreditApplicationRepositoryPort creditApplicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    
    // Tasa de interés por defecto
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("12.5");
    
    public RegisterCreditApplicationService(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
    }
    
    @Override
    public CreditApplication createCreditApplication(CreditApplication creditApplication) {
        // Verificar que el afiliado existe
        if (!affiliateRepository.findById(creditApplication.getAffiliateId()).isPresent()) {
            throw new AffiliateNotFoundException(creditApplication.getAffiliateId());
        }
        
        // Validaciones de negocio
        if (!creditApplication.isValidAmount()) {
            throw new InvalidOperationException(
                    String.format("El monto solicitado debe estar entre %s y %s",
                            CreditApplication.MIN_AMOUNT, CreditApplication.MAX_AMOUNT));
        }
        
        if (!creditApplication.isValidTerm()) {
            throw new InvalidOperationException(
                    String.format("El plazo debe estar entre %d y %d meses",
                            CreditApplication.MIN_TERM_MONTHS, CreditApplication.MAX_TERM_MONTHS));
        }
        
        // Establecer valores iniciales
        creditApplication.setStatus(CreditApplicationStatus.PENDING);
        creditApplication.setInterestRate(DEFAULT_INTEREST_RATE);
        creditApplication.setCreatedAt(LocalDateTime.now());
        creditApplication.setUpdatedAt(LocalDateTime.now());
        
        return creditApplicationRepository.save(creditApplication);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CreditApplication getCreditApplicationById(Long id) {
        return creditApplicationRepository.findById(id)
                .orElseThrow(() -> new CreditApplicationNotFoundException(id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> getCreditApplicationsByAffiliateId(Long affiliateId) {
        return creditApplicationRepository.findByAffiliateId(affiliateId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> getAllCreditApplications() {
        return creditApplicationRepository.findAll();
    }
    
    @Override
    public CreditApplication cancelCreditApplication(Long id, Long affiliateId) {
        CreditApplication creditApplication = getCreditApplicationById(id);
        
        // Verificar que pertenece al afiliado
        if (!creditApplication.getAffiliateId().equals(affiliateId)) {
            throw new InvalidOperationException("No tiene permiso para cancelar esta solicitud");
        }
        
        // Solo se puede cancelar si está pendiente
        if (creditApplication.getStatus() != CreditApplicationStatus.PENDING) {
            throw new InvalidOperationException("Solo se pueden cancelar solicitudes pendientes");
        }
        
        creditApplication.setStatus(CreditApplicationStatus.CANCELLED);
        creditApplication.setUpdatedAt(LocalDateTime.now());
        
        return creditApplicationRepository.save(creditApplication);
    }
}

