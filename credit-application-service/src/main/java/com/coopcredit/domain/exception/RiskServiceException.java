package com.coopcredit.domain.exception;

/**
 * Excepción para errores del servicio de riesgo
 */
public class RiskServiceException extends DomainException {
    
    public RiskServiceException(String message) {
        super(message);
    }
    
    public RiskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

