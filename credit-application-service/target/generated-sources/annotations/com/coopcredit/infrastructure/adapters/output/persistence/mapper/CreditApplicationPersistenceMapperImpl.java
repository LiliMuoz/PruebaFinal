package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T18:40:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class CreditApplicationPersistenceMapperImpl implements CreditApplicationPersistenceMapper {

    @Autowired
    private RiskEvaluationPersistenceMapper riskEvaluationPersistenceMapper;

    @Override
    public CreditApplicationEntity toEntity(CreditApplication creditApplication) {
        if ( creditApplication == null ) {
            return null;
        }

        CreditApplicationEntity.CreditApplicationEntityBuilder creditApplicationEntity = CreditApplicationEntity.builder();

        creditApplicationEntity.id( creditApplication.getId() );
        creditApplicationEntity.requestedAmount( creditApplication.getRequestedAmount() );
        creditApplicationEntity.termMonths( creditApplication.getTermMonths() );
        creditApplicationEntity.interestRate( creditApplication.getInterestRate() );
        creditApplicationEntity.purpose( creditApplication.getPurpose() );
        creditApplicationEntity.status( creditApplication.getStatus() );
        creditApplicationEntity.createdAt( creditApplication.getCreatedAt() );
        creditApplicationEntity.updatedAt( creditApplication.getUpdatedAt() );
        creditApplicationEntity.evaluatedAt( creditApplication.getEvaluatedAt() );
        creditApplicationEntity.evaluatedBy( creditApplication.getEvaluatedBy() );
        creditApplicationEntity.rejectionReason( creditApplication.getRejectionReason() );
        creditApplicationEntity.riskEvaluation( riskEvaluationPersistenceMapper.toEntity( creditApplication.getRiskEvaluation() ) );

        return creditApplicationEntity.build();
    }

    @Override
    public CreditApplication toDomain(CreditApplicationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CreditApplication creditApplication = new CreditApplication();

        creditApplication.setAffiliateId( entityAffiliateId( entity ) );
        creditApplication.setId( entity.getId() );
        creditApplication.setRequestedAmount( entity.getRequestedAmount() );
        creditApplication.setTermMonths( entity.getTermMonths() );
        creditApplication.setInterestRate( entity.getInterestRate() );
        creditApplication.setPurpose( entity.getPurpose() );
        creditApplication.setStatus( entity.getStatus() );
        creditApplication.setCreatedAt( entity.getCreatedAt() );
        creditApplication.setUpdatedAt( entity.getUpdatedAt() );
        creditApplication.setEvaluatedAt( entity.getEvaluatedAt() );
        creditApplication.setEvaluatedBy( entity.getEvaluatedBy() );
        creditApplication.setRejectionReason( entity.getRejectionReason() );
        creditApplication.setRiskEvaluation( riskEvaluationPersistenceMapper.toDomain( entity.getRiskEvaluation() ) );

        return creditApplication;
    }

    private Long entityAffiliateId(CreditApplicationEntity creditApplicationEntity) {
        if ( creditApplicationEntity == null ) {
            return null;
        }
        AffiliateEntity affiliate = creditApplicationEntity.getAffiliate();
        if ( affiliate == null ) {
            return null;
        }
        Long id = affiliate.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
