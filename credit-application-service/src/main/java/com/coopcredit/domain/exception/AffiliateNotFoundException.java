package com.coopcredit.domain.exception;

/**
 * Excepción cuando no se encuentra un afiliado
 */
public class AffiliateNotFoundException extends DomainException {
    
    public AffiliateNotFoundException(Long id) {
        super("Afiliado no encontrado con ID: " + id);
    }
    
    public AffiliateNotFoundException(String documentNumber) {
        super("Afiliado no encontrado con documento: " + documentNumber);
    }
}

