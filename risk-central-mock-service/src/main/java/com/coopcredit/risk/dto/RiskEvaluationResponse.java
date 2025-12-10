package com.coopcredit.risk.dto;

public record RiskEvaluationResponse(
    String documentNumber,
    Integer score,
    String riskLevel,
    String recommendation
) {}

