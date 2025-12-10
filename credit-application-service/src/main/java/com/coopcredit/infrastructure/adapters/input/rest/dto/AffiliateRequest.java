package com.coopcredit.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AffiliateRequest(
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres")
    String documentNumber,
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(CC|CE|NIT|PASAPORTE)$", message = "El tipo de documento debe ser CC, CE, NIT o PASAPORTE")
    String documentType,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String firstName,
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    String lastName,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    String email,
    
    @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener solo números (7-20 dígitos)")
    String phone,
    
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    String address,
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    LocalDate birthDate,
    
    Long userId
) {}

