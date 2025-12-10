package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RiskEvaluationPersistenceMapper {
    
    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluationEntity toEntity(RiskEvaluation riskEvaluation);
    
    @Mapping(target = "creditApplicationId", source = "creditApplication.id")
    RiskEvaluation toDomain(RiskEvaluationEntity entity);
}

