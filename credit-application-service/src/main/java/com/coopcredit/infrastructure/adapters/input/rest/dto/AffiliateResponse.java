package com.coopcredit.infrastructure.adapters.input.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AffiliateResponse(
    Long id,
    String documentNumber,
    String documentType,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String phone,
    String address,
    LocalDate birthDate,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

