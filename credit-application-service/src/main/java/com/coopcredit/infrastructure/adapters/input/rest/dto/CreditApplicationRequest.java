package com.coopcredit.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreditApplicationRequest(
    @NotNull(message = "El monto solicitado es obligatorio")
    @DecimalMin(value = "100000", message = "El monto mínimo es 100,000")
    @DecimalMax(value = "50000000", message = "El monto máximo es 50,000,000")
    BigDecimal requestedAmount,
    
    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 6, message = "El plazo mínimo es de 6 meses")
    @Max(value = 60, message = "El plazo máximo es de 60 meses")
    Integer termMonths,
    
    @Size(max = 500, message = "El propósito no puede exceder 500 caracteres")
    String purpose
) {}

