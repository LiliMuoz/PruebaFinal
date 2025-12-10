package com.coopcredit.risk.dto;

import jakarta.validation.constraints.NotBlank;

public record RiskEvaluationRequest(
    @NotBlank(message = "El número de documento es obligatorio")
    String documentNumber
) {}

