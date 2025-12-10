package com.coopcredit.application.ports.input;

import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;

import java.util.List;

/**
 * Puerto de entrada para el caso de uso de autenticación
 */
public interface AuthenticationUseCase {
    
    User register(User user);
    
    String login(String username, String password);
    
    User getUserByUsername(String username);
    
    User getUserById(Long id);
    
    User changeUserRole(String username, Role newRole);
    
    List<User> getAllUsers();
}

