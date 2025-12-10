package com.coopcredit.infrastructure.config;

import com.coopcredit.application.ports.output.UserRepositoryPort;
import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inicializa usuarios por defecto al arrancar la aplicación.
 * Crea usuarios admin, analista y afiliado si no existen.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Verificando usuarios por defecto...");
        
        createUserIfNotExists("admin", "admin123", "admin@coopcredit.com", Role.ADMIN);
        createUserIfNotExists("analista", "analista123", "analista@coopcredit.com", Role.ANALISTA);
        createUserIfNotExists("afiliado", "afiliado123", "afiliado@coopcredit.com", Role.AFILIADO);
        
        log.info("Verificación de usuarios completada.");
    }

    private void createUserIfNotExists(String username, String password, String email, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole(role);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            log.info(" Usuario creado: {} con rol {}", username, role);
        } else {
            log.info("Usuario {} ya existe, omitiendo creación", username);
        }
    }
}

