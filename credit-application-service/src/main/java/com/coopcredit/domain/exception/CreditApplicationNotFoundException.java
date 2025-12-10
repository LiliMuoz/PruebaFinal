package com.coopcredit.domain.exception;

/**
 * Excepción cuando no se encuentra una solicitud de crédito
 */
public class CreditApplicationNotFoundException extends DomainException {
    
    public CreditApplicationNotFoundException(Long id) {
        super("Solicitud de crédito no encontrada con ID: " + id);
    }
}

