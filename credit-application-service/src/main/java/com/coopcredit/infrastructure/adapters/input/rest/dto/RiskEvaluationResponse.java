package com.coopcredit.infrastructure.adapters.input.rest.dto;

import com.coopcredit.domain.model.RiskLevel;

import java.time.LocalDateTime;

public record RiskEvaluationResponse(
    Long id,
    Integer score,
    RiskLevel riskLevel,
    String recommendation,
    LocalDateTime evaluatedAt
) {}

