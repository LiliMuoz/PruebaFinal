package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.CreditApplicationStatus;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.domain.model.RiskLevel;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationResponse;
import com.coopcredit.infrastructure.adapters.input.rest.dto.RiskEvaluationResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T18:40:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class CreditApplicationRestMapperImpl implements CreditApplicationRestMapper {

    @Override
    public CreditApplication toDomain(CreditApplicationRequest request) {
        if ( request == null ) {
            return null;
        }

        CreditApplication creditApplication = new CreditApplication();

        creditApplication.setRequestedAmount( request.requestedAmount() );
        creditApplication.setTermMonths( request.termMonths() );
        creditApplication.setPurpose( request.purpose() );

        return creditApplication;
    }

    @Override
    public CreditApplicationResponse toResponse(CreditApplication creditApplication) {
        if ( creditApplication == null ) {
            return null;
        }

        Long id = null;
        Long affiliateId = null;
        BigDecimal requestedAmount = null;
        Integer termMonths = null;
        BigDecimal interestRate = null;
        String purpose = null;
        CreditApplicationStatus status = null;
        LocalDateTime createdAt = null;
        LocalDateTime evaluatedAt = null;
        String evaluatedBy = null;
        String rejectionReason = null;
        RiskEvaluationResponse riskEvaluation = null;

        id = creditApplication.getId();
        affiliateId = creditApplication.getAffiliateId();
        requestedAmount = creditApplication.getRequestedAmount();
        termMonths = creditApplication.getTermMonths();
        interestRate = creditApplication.getInterestRate();
        purpose = creditApplication.getPurpose();
        status = creditApplication.getStatus();
        createdAt = creditApplication.getCreatedAt();
        evaluatedAt = creditApplication.getEvaluatedAt();
        evaluatedBy = creditApplication.getEvaluatedBy();
        rejectionReason = creditApplication.getRejectionReason();
        riskEvaluation = toRiskResponse( creditApplication.getRiskEvaluation() );

        String affiliateName = null;
        BigDecimal monthlyPayment = creditApplication.calculateMonthlyPayment();

        CreditApplicationResponse creditApplicationResponse = new CreditApplicationResponse( id, affiliateId, affiliateName, requestedAmount, termMonths, interestRate, monthlyPayment, purpose, status, createdAt, evaluatedAt, evaluatedBy, rejectionReason, riskEvaluation );

        return creditApplicationResponse;
    }

    @Override
    public RiskEvaluationResponse toRiskResponse(RiskEvaluation riskEvaluation) {
        if ( riskEvaluation == null ) {
            return null;
        }

        Long id = null;
        Integer score = null;
        RiskLevel riskLevel = null;
        String recommendation = null;
        LocalDateTime evaluatedAt = null;

        id = riskEvaluation.getId();
        score = riskEvaluation.getScore();
        riskLevel = riskEvaluation.getRiskLevel();
        recommendation = riskEvaluation.getRecommendation();
        evaluatedAt = riskEvaluation.getEvaluatedAt();

        RiskEvaluationResponse riskEvaluationResponse = new RiskEvaluationResponse( id, score, riskLevel, recommendation, evaluatedAt );

        return riskEvaluationResponse;
    }
}
