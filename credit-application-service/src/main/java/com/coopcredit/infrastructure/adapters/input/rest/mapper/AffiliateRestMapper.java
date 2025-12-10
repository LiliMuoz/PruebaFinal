package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AffiliateRestMapper {
    
    Affiliate toDomain(AffiliateRequest request);
    
    @Mapping(target = "fullName", expression = "java(affiliate.getFullName())")
    AffiliateResponse toResponse(Affiliate affiliate);
}

