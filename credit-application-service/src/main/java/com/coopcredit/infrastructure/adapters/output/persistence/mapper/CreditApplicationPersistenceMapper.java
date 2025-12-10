package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RiskEvaluationPersistenceMapper.class})
public interface CreditApplicationPersistenceMapper {
    
    @Mapping(target = "affiliate", ignore = true)
    CreditApplicationEntity toEntity(CreditApplication creditApplication);
    
    @Mapping(target = "affiliateId", source = "affiliate.id")
    CreditApplication toDomain(CreditApplicationEntity entity);
}

