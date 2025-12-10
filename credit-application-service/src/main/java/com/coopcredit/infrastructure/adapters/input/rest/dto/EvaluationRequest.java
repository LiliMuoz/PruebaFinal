package com.coopcredit.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EvaluationRequest(
    @NotBlank(message = "La razón es obligatoria para rechazos")
    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    String reason
) {}

