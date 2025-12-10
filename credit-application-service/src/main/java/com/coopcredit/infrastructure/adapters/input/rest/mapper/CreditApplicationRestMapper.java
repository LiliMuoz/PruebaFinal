package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationResponse;
import com.coopcredit.infrastructure.adapters.input.rest.dto.RiskEvaluationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditApplicationRestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "affiliateId", ignore = true)
    @Mapping(target = "interestRate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "evaluatedAt", ignore = true)
    @Mapping(target = "evaluatedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "riskEvaluation", ignore = true)
    CreditApplication toDomain(CreditApplicationRequest request);
    
    @Mapping(target = "affiliateName", ignore = true)
    @Mapping(target = "monthlyPayment", expression = "java(creditApplication.calculateMonthlyPayment())")
    CreditApplicationResponse toResponse(CreditApplication creditApplication);
    
    RiskEvaluationResponse toRiskResponse(RiskEvaluation riskEvaluation);
}

