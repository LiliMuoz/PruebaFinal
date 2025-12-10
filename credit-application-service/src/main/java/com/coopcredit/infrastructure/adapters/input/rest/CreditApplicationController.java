package com.coopcredit.infrastructure.adapters.input.rest;

import com.coopcredit.application.ports.input.AuthenticationUseCase;
import com.coopcredit.application.ports.input.EvaluateCreditApplicationUseCase;
import com.coopcredit.application.ports.input.RegisterAffiliateUseCase;
import com.coopcredit.application.ports.input.RegisterCreditApplicationUseCase;
import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.InvalidOperationException;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.CreditApplicationResponse;
import com.coopcredit.infrastructure.adapters.input.rest.dto.EvaluationRequest;
import com.coopcredit.infrastructure.adapters.input.rest.mapper.CreditApplicationRestMapper;
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
@RequestMapping("/api/credit-applications")
@Tag(name = "Solicitudes de Crédito", description = "Gestión de solicitudes de crédito")
@SecurityRequirement(name = "bearerAuth")
public class CreditApplicationController {
    
    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final AuthenticationUseCase authenticationUseCase;
    private final CreditApplicationRestMapper creditApplicationMapper;
    
    public CreditApplicationController(
            RegisterCreditApplicationUseCase registerCreditApplicationUseCase,
            EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase,
            RegisterAffiliateUseCase registerAffiliateUseCase,
            AuthenticationUseCase authenticationUseCase,
            CreditApplicationRestMapper creditApplicationMapper) {
        this.registerCreditApplicationUseCase = registerCreditApplicationUseCase;
        this.evaluateCreditApplicationUseCase = evaluateCreditApplicationUseCase;
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.authenticationUseCase = authenticationUseCase;
        this.creditApplicationMapper = creditApplicationMapper;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    @Operation(summary = "Crear mi solicitud de crédito", description = "Crea una nueva solicitud vinculada al usuario actual")
    public ResponseEntity<CreditApplicationResponse> createCreditApplication(
            @Valid @RequestBody CreditApplicationRequest request) {
        // Obtener el afiliado del usuario autenticado
        User currentUser = getCurrentUser();
        Affiliate affiliate = registerAffiliateUseCase.findAffiliateByUserId(currentUser.getId())
                .orElseThrow(() -> new InvalidOperationException(
                        "Debe completar su perfil de afiliado antes de solicitar un crédito"));
        
        CreditApplication creditApplication = creditApplicationMapper.toDomain(request);
        creditApplication.setAffiliateId(affiliate.getId()); // Vincular automáticamente
        CreditApplication savedApplication = registerCreditApplicationUseCase.createCreditApplication(creditApplication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponseWithAffiliate(savedApplication));
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    @Operation(summary = "Obtener mis solicitudes", description = "Obtiene las solicitudes del usuario actual")
    public ResponseEntity<List<CreditApplicationResponse>> getMyCreditApplications() {
        User currentUser = getCurrentUser();
        Optional<Affiliate> affiliateOpt = registerAffiliateUseCase.findAffiliateByUserId(currentUser.getId());
        
        if (affiliateOpt.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Sin afiliado, sin solicitudes
        }
        
        List<CreditApplication> applications = registerCreditApplicationUseCase
                .getCreditApplicationsByAffiliateId(affiliateOpt.get().getId());
        List<CreditApplicationResponse> responses = applications.stream()
                .map(this::mapToResponseWithAffiliate)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AFILIADO', 'ANALISTA', 'ADMIN')")
    @Operation(summary = "Obtener solicitud por ID", description = "Obtiene una solicitud de crédito por su ID")
    public ResponseEntity<CreditApplicationResponse> getCreditApplicationById(@PathVariable Long id) {
        CreditApplication creditApplication = registerCreditApplicationUseCase.getCreditApplicationById(id);
        
        // Verificar que el afiliado pueda ver solo sus propias solicitudes
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.AFILIADO) {
            Optional<Affiliate> affiliateOpt = registerAffiliateUseCase.findAffiliateByUserId(currentUser.getId());
            if (affiliateOpt.isEmpty() || !affiliateOpt.get().getId().equals(creditApplication.getAffiliateId())) {
                throw new InvalidOperationException("No tiene permisos para ver esta solicitud");
            }
        }
        
        return ResponseEntity.ok(mapToResponseWithAffiliate(creditApplication));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Obtener todas las solicitudes", description = "Solo analistas y admins pueden ver todas las solicitudes")
    public ResponseEntity<List<CreditApplicationResponse>> getAllCreditApplications() {
        List<CreditApplication> applications = registerCreditApplicationUseCase.getAllCreditApplications();
        List<CreditApplicationResponse> responses = applications.stream()
                .map(this::mapToResponseWithAffiliate)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Evaluar solicitud", description = "Evalúa una solicitud de crédito llamando al servicio de riesgo")
    public ResponseEntity<CreditApplicationResponse> evaluateCreditApplication(@PathVariable Long id) {
        String evaluator = getCurrentUsername();
        CreditApplication evaluatedApplication = evaluateCreditApplicationUseCase
                .evaluateCreditApplication(id, evaluator);
        return ResponseEntity.ok(mapToResponseWithAffiliate(evaluatedApplication));
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Aprobar solicitud manualmente", description = "Aprueba manualmente una solicitud de crédito")
    public ResponseEntity<CreditApplicationResponse> approveCreditApplication(@PathVariable Long id) {
        String evaluator = getCurrentUsername();
        CreditApplication approvedApplication = evaluateCreditApplicationUseCase
                .approveCreditApplication(id, evaluator);
        return ResponseEntity.ok(mapToResponseWithAffiliate(approvedApplication));
    }
    
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    @Operation(summary = "Rechazar solicitud", description = "Rechaza una solicitud de crédito con una razón")
    public ResponseEntity<CreditApplicationResponse> rejectCreditApplication(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationRequest request) {
        String evaluator = getCurrentUsername();
        CreditApplication rejectedApplication = evaluateCreditApplicationUseCase
                .rejectCreditApplication(id, evaluator, request.reason());
        return ResponseEntity.ok(mapToResponseWithAffiliate(rejectedApplication));
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Cancelar mi solicitud", description = "Cancela una solicitud de crédito propia")
    public ResponseEntity<CreditApplicationResponse> cancelCreditApplication(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Affiliate affiliate = registerAffiliateUseCase.getAffiliateByUserId(currentUser.getId());
        
        CreditApplication cancelledApplication = registerCreditApplicationUseCase
                .cancelCreditApplication(id, affiliate.getId());
        return ResponseEntity.ok(mapToResponseWithAffiliate(cancelledApplication));
    }
    
    private CreditApplicationResponse mapToResponseWithAffiliate(CreditApplication creditApplication) {
        Affiliate affiliate = registerAffiliateUseCase.getAffiliateById(creditApplication.getAffiliateId());
        CreditApplicationResponse baseResponse = creditApplicationMapper.toResponse(creditApplication);
        
        return new CreditApplicationResponse(
                baseResponse.id(),
                baseResponse.affiliateId(),
                affiliate.getFullName(),
                baseResponse.requestedAmount(),
                baseResponse.termMonths(),
                baseResponse.interestRate(),
                baseResponse.monthlyPayment(),
                baseResponse.purpose(),
                baseResponse.status(),
                baseResponse.createdAt(),
                baseResponse.evaluatedAt(),
                baseResponse.evaluatedBy(),
                baseResponse.rejectionReason(),
                creditApplication.getRiskEvaluation() != null 
                        ? creditApplicationMapper.toRiskResponse(creditApplication.getRiskEvaluation())
                        : null
        );
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
    
    private User getCurrentUser() {
        String username = getCurrentUsername();
        return authenticationUseCase.getUserByUsername(username);
    }
}

