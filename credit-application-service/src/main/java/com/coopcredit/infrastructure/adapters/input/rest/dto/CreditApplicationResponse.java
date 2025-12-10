package com.coopcredit.infrastructure.adapters.input.rest.dto;

import com.coopcredit.domain.model.CreditApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditApplicationResponse(
    Long id,
    Long affiliateId,
    String affiliateName,
    BigDecimal requestedAmount,
    Integer termMonths,
    BigDecimal interestRate,
    BigDecimal monthlyPayment,
    String purpose,
    CreditApplicationStatus status,
    LocalDateTime createdAt,
    LocalDateTime evaluatedAt,
    String evaluatedBy,
    String rejectionReason,
    RiskEvaluationResponse riskEvaluation
) {}

