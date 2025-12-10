package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AffiliatePersistenceMapper {
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "creditApplications", ignore = true)
    AffiliateEntity toEntity(Affiliate affiliate);
    
    @Mapping(target = "userId", source = "user.id")
    Affiliate toDomain(AffiliateEntity entity);
}

