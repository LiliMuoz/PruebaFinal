package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:02:07-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class RiskEvaluationPersistenceMapperImpl implements RiskEvaluationPersistenceMapper {

    @Override
    public RiskEvaluationEntity toEntity(RiskEvaluation riskEvaluation) {
        if ( riskEvaluation == null ) {
            return null;
        }

        RiskEvaluationEntity.RiskEvaluationEntityBuilder riskEvaluationEntity = RiskEvaluationEntity.builder();

        riskEvaluationEntity.documentNumber( riskEvaluation.getDocumentNumber() );
        riskEvaluationEntity.evaluatedAt( riskEvaluation.getEvaluatedAt() );
        riskEvaluationEntity.id( riskEvaluation.getId() );
        riskEvaluationEntity.recommendation( riskEvaluation.getRecommendation() );
        riskEvaluationEntity.riskLevel( riskEvaluation.getRiskLevel() );
        riskEvaluationEntity.score( riskEvaluation.getScore() );

        return riskEvaluationEntity.build();
    }

    @Override
    public RiskEvaluation toDomain(RiskEvaluationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RiskEvaluation riskEvaluation = new RiskEvaluation();

        riskEvaluation.setCreditApplicationId( entityCreditApplicationId( entity ) );
        riskEvaluation.setId( entity.getId() );
        riskEvaluation.setDocumentNumber( entity.getDocumentNumber() );
        riskEvaluation.setScore( entity.getScore() );
        riskEvaluation.setRiskLevel( entity.getRiskLevel() );
        riskEvaluation.setRecommendation( entity.getRecommendation() );
        riskEvaluation.setEvaluatedAt( entity.getEvaluatedAt() );

        return riskEvaluation;
    }

    private Long entityCreditApplicationId(RiskEvaluationEntity riskEvaluationEntity) {
        if ( riskEvaluationEntity == null ) {
            return null;
        }
        CreditApplicationEntity creditApplication = riskEvaluationEntity.getCreditApplication();
        if ( creditApplication == null ) {
            return null;
        }
        Long id = creditApplication.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
