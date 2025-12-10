package com.coopcredit.application.usecases;

import com.coopcredit.application.ports.input.AuthenticationUseCase;
import com.coopcredit.application.ports.output.UserRepositoryPort;
import com.coopcredit.domain.exception.DuplicateResourceException;
import com.coopcredit.domain.exception.InvalidOperationException;
import com.coopcredit.domain.exception.UserNotFoundException;
import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.config.security.JwtTokenProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del caso de uso de autenticación
 */
@Service
@Transactional
public class AuthenticationService implements AuthenticationUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter registrationCounter;
    
    public AuthenticationService(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        
        // Métricas personalizadas
        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Logins exitosos")
                .register(meterRegistry);
        
        this.loginFailureCounter = Counter.builder("auth.login.failure")
                .description("Logins fallidos")
                .register(meterRegistry);
        
        this.registrationCounter = Counter.builder("auth.registration.total")
                .description("Registros de usuarios")
                .register(meterRegistry);
    }
    
    @Override
    public User register(User user) {
        // Verificar duplicados
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("El nombre de usuario ya existe: " + user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + user.getEmail());
        }
        
        // Encriptar contraseña y asignar rol por defecto
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.AFILIADO); // Rol por defecto
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        registrationCounter.increment();
        
        log.info("Usuario registrado exitosamente: {} con rol AFILIADO", user.getUsername());
        
        return savedUser;
    }
    
    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    loginFailureCounter.increment();
                    log.warn("Intento de login fallido - usuario no encontrado: {}", username);
                    return new InvalidOperationException("Credenciales inválidas");
                });
        
        if (!user.isActive()) {
            loginFailureCounter.increment();
            log.warn("Intento de login fallido - usuario inactivo: {}", username);
            throw new InvalidOperationException("Usuario inactivo");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginFailureCounter.increment();
            log.warn("Intento de login fallido - contraseña incorrecta: {}", username);
            throw new InvalidOperationException("Credenciales inválidas");
        }
        
        String token = jwtTokenProvider.generateToken(user);
        loginSuccessCounter.increment();
        
        log.info("Login exitoso para usuario: {}", username);
        
        return token;
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @Override
    public User changeUserRole(String username, Role newRole) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("Rol actualizado para usuario {}: {}", username, newRole);
        
        return updatedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

