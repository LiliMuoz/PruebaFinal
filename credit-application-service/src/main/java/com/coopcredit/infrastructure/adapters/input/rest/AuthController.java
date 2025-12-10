package com.coopcredit.infrastructure.adapters.input.rest;

import com.coopcredit.application.ports.input.AuthenticationUseCase;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.input.rest.dto.*;
import com.coopcredit.infrastructure.adapters.input.rest.mapper.UserRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthController {
    
    private final AuthenticationUseCase authenticationUseCase;
    private final UserRestMapper userMapper;
    
    public AuthController(AuthenticationUseCase authenticationUseCase, UserRestMapper userMapper) {
        this.authenticationUseCase = authenticationUseCase;
        this.userMapper = userMapper;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario con rol AFILIADO por defecto")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userMapper.toDomain(request);
        User savedUser = authenticationUseCase.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(savedUser));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authenticationUseCase.login(request.username(), request.password());
        User user = authenticationUseCase.getUserByUsername(request.username());
        
        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String username = getCurrentUsername();
        User user = authenticationUseCase.getUserByUsername(username);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    
    @PutMapping("/users/{username}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar rol de usuario", description = "Solo administradores pueden cambiar roles")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable String username,
            @Valid @RequestBody ChangeRoleRequest request) {
        User updatedUser = authenticationUseCase.changeUserRole(username, request.role());
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios", description = "Solo administradores pueden ver la lista de usuarios")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = authenticationUseCase.getAllUsers();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}

