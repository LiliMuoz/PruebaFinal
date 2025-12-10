package com.coopcredit.infrastructure.adapters.output.persistence.adapter;

import com.coopcredit.application.ports.output.RiskEvaluationRepositoryPort;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.mapper.RiskEvaluationPersistenceMapper;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.CreditApplicationJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RiskEvaluationPersistenceAdapter implements RiskEvaluationRepositoryPort {
    
    private final RiskEvaluationJpaRepository riskEvaluationRepository;
    private final CreditApplicationJpaRepository creditApplicationRepository;
    private final RiskEvaluationPersistenceMapper mapper;
    
    public RiskEvaluationPersistenceAdapter(
            RiskEvaluationJpaRepository riskEvaluationRepository,
            CreditApplicationJpaRepository creditApplicationRepository,
            RiskEvaluationPersistenceMapper mapper) {
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.creditApplicationRepository = creditApplicationRepository;
        this.mapper = mapper;
    }
    
    @Override
    public RiskEvaluation save(RiskEvaluation riskEvaluation) {
        RiskEvaluationEntity entity = mapper.toEntity(riskEvaluation);
        
        // Asociar solicitud de crédito
        if (riskEvaluation.getCreditApplicationId() != null) {
            creditApplicationRepository.findById(riskEvaluation.getCreditApplicationId())
                    .ifPresent(entity::setCreditApplication);
        }
        
        RiskEvaluationEntity savedEntity = riskEvaluationRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<RiskEvaluation> findById(Long id) {
        return riskEvaluationRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId) {
        return riskEvaluationRepository.findByCreditApplicationId(creditApplicationId)
                .map(mapper::toDomain);
    }
}

