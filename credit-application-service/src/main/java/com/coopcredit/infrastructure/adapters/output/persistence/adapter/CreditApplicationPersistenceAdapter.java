package com.coopcredit.infrastructure.adapters.output.persistence.adapter;

import com.coopcredit.application.ports.output.CreditApplicationRepositoryPort;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.CreditApplicationStatus;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.mapper.CreditApplicationPersistenceMapper;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.AffiliateJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.CreditApplicationJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CreditApplicationPersistenceAdapter implements CreditApplicationRepositoryPort {
    
    private final CreditApplicationJpaRepository creditApplicationRepository;
    private final AffiliateJpaRepository affiliateRepository;
    private final RiskEvaluationJpaRepository riskEvaluationRepository;
    private final CreditApplicationPersistenceMapper mapper;
    
    public CreditApplicationPersistenceAdapter(
            CreditApplicationJpaRepository creditApplicationRepository,
            AffiliateJpaRepository affiliateRepository,
            RiskEvaluationJpaRepository riskEvaluationRepository,
            CreditApplicationPersistenceMapper mapper) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.mapper = mapper;
    }
    
    @Override
    public CreditApplication save(CreditApplication creditApplication) {
        CreditApplicationEntity entity = mapper.toEntity(creditApplication);
        
        // Asociar afiliado
        if (creditApplication.getAffiliateId() != null) {
            affiliateRepository.findById(creditApplication.getAffiliateId())
                    .ifPresent(entity::setAffiliate);
        }
        
        // Si existe una RiskEvaluation, cargar la entidad existente para mantener la relación
        if (creditApplication.getRiskEvaluation() != null && creditApplication.getRiskEvaluation().getId() != null) {
            riskEvaluationRepository.findById(creditApplication.getRiskEvaluation().getId())
                    .ifPresent(existingRiskEval -> {
                        entity.setRiskEvaluation(existingRiskEval);
                        existingRiskEval.setCreditApplication(entity);
                    });
        }
        
        CreditApplicationEntity savedEntity = creditApplicationRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<CreditApplication> findById(Long id) {
        return creditApplicationRepository.findByIdWithRelations(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return creditApplicationRepository.findByAffiliateId(affiliateId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CreditApplication> findAll() {
        return creditApplicationRepository.findAllWithAffiliate()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CreditApplication> findByStatus(CreditApplicationStatus status) {
        return creditApplicationRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        creditApplicationRepository.deleteById(id);
    }
}

