package com.coopcredit.domain.exception;

/**
 * Excepción para operaciones inválidas
 */
public class InvalidOperationException extends DomainException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
}

