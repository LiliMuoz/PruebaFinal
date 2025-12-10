package com.coopcredit.infrastructure.adapters.input.rest.dto;

import com.coopcredit.domain.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    String username,
    
    @NotNull(message = "El rol es obligatorio")
    Role role
) {}

