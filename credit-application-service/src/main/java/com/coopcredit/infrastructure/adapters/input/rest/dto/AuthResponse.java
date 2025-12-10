package com.coopcredit.infrastructure.adapters.input.rest.dto;

import com.coopcredit.domain.model.Role;

public record AuthResponse(
    String token,
    String username,
    String email,
    Role role,
    String tokenType
) {
    public AuthResponse(String token, String username, String email, Role role) {
        this(token, username, email, role, "Bearer");
    }
}

