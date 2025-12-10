package com.coopcredit.infrastructure.adapters.input.rest;

import com.coopcredit.application.ports.input.AuthenticationUseCase;
import com.coopcredit.application.ports.input.RegisterAffiliateUseCase;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateResponse;
import com.coopcredit.infrastructure.adapters.input.rest.mapper.AffiliateRestMapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/affiliates")
@Tag(name = "Afiliados", description = "Gestión de afiliados")
@SecurityRequirement(name = "bearerAuth")
public class AffiliateController {
    
    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final AuthenticationUseCase authenticationUseCase;
    private final AffiliateRestMapper affiliateMapper;
    
    public AffiliateController(RegisterAffiliateUseCase registerAffiliateUseCase,
                              AuthenticationUseCase authenticationUseCase,
                              AffiliateRestMapper affiliateMapper) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.authenticationUseCase = authenticationUseCase;
        this.affiliateMapper = affiliateMapper;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los afiliados", description = "Solo administradores pueden ver la lista completa")
    public ResponseEntity<List<AffiliateResponse>> getAllAffiliates() {
        List<Affiliate> affiliates = registerAffiliateUseCase.getAllAffiliates();
        List<AffiliateResponse> responses = affiliates.stream()
                .map(affiliateMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    @Operation(summary = "Registrar mi perfil de afiliado", description = "Registra el perfil de afiliado del usuario actual")
    public ResponseEntity<AffiliateResponse> createAffiliate(@Valid @RequestBody AffiliateRequest request) {
        User currentUser = getCurrentUser();
        Affiliate affiliate = affiliateMapper.toDomain(request);
        affiliate.setUserId(currentUser.getId()); // Vincular con el usuario autenticado
        Affiliate savedAffiliate = registerAffiliateUseCase.registerAffiliate(affiliate);
        return ResponseEntity.status(HttpStatus.CREATED).body(affiliateMapper.toResponse(savedAffiliate));
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ANALISTA', 'ADMIN')")
    @Operation(summary = "Obtener mi perfil de afiliado", description = "Obtiene el perfil de afiliado del usuario actual")
    public ResponseEntity<AffiliateResponse> getMyAffiliate() {
        User currentUser = getCurrentUser();
        Optional<Affiliate> affiliate = registerAffiliateUseCase.findAffiliateByUserId(currentUser.getId());
        return affiliate
                .map(a -> ResponseEntity.ok(affiliateMapper.toResponse(a)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Obtener afiliado por ID", description = "Solo analistas y admins pueden ver afiliados por ID")
    public ResponseEntity<AffiliateResponse> getAffiliateById(@PathVariable Long id) {
        Affiliate affiliate = registerAffiliateUseCase.getAffiliateById(id);
        return ResponseEntity.ok(affiliateMapper.toResponse(affiliate));
    }
    
    @GetMapping("/document/{documentNumber}")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Obtener afiliado por documento", description = "Obtiene la información de un afiliado por su número de documento")
    public ResponseEntity<AffiliateResponse> getAffiliateByDocument(@PathVariable String documentNumber) {
        Affiliate affiliate = registerAffiliateUseCase.getAffiliateByDocumentNumber(documentNumber);
        return ResponseEntity.ok(affiliateMapper.toResponse(affiliate));
    }
    
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    @Operation(summary = "Actualizar mi perfil", description = "Actualiza el perfil de afiliado del usuario actual")
    public ResponseEntity<AffiliateResponse> updateMyAffiliate(@Valid @RequestBody AffiliateRequest request) {
        User currentUser = getCurrentUser();
        Affiliate existingAffiliate = registerAffiliateUseCase.getAffiliateByUserId(currentUser.getId());
        Affiliate affiliate = affiliateMapper.toDomain(request);
        Affiliate updatedAffiliate = registerAffiliateUseCase.updateAffiliate(existingAffiliate.getId(), affiliate);
        return ResponseEntity.ok(affiliateMapper.toResponse(updatedAffiliate));
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authenticationUseCase.getUserByUsername(username);
    }
}

