package com.coopcredit.application.ports.output;

import com.coopcredit.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de usuarios
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void deleteById(Long id);
    
    List<User> findAll();
}

