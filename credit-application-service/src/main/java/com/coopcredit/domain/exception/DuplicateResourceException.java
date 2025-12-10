package com.coopcredit.domain.exception;

/**
 * Excepción para recursos duplicados
 */
public class DuplicateResourceException extends DomainException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}

