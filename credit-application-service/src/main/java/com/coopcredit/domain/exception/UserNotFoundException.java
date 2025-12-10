package com.coopcredit.domain.exception;

/**
 * Excepción cuando no se encuentra un usuario
 */
public class UserNotFoundException extends DomainException {
    
    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }
    
    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
    }
}

