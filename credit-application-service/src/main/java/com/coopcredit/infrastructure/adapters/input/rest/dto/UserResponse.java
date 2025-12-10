package com.coopcredit.infrastructure.adapters.input.rest.dto;

import com.coopcredit.domain.model.Role;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    Role role,
    boolean active,
    LocalDateTime createdAt
) {}

