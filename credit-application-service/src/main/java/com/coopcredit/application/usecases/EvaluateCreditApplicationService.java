package com.coopcredit.application.usecases;

import com.coopcredit.application.ports.input.EvaluateCreditApplicationUseCase;
import com.coopcredit.application.ports.output.AffiliateRepositoryPort;
import com.coopcredit.application.ports.output.CreditApplicationRepositoryPort;
import com.coopcredit.application.ports.output.RiskEvaluationRepositoryPort;
import com.coopcredit.application.ports.output.RiskServicePort;
import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.CreditApplicationNotFoundException;
import com.coopcredit.domain.exception.InvalidOperationException;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.RiskEvaluation;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del caso de uso de evaluación de solicitudes de crédito
 */
@Service
@Transactional
public class EvaluateCreditApplicationService implements EvaluateCreditApplicationUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluateCreditApplicationService.class);
    
    private final CreditApplicationRepositoryPort creditApplicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final RiskServicePort riskService;
    private final RiskEvaluationRepositoryPort riskEvaluationRepository;
    
    private final Counter evaluationsCounter;
    private final Counter approvalsCounter;
    private final Counter rejectionsCounter;
    private final Timer evaluationTimer;
    
    public EvaluateCreditApplicationService(
            CreditApplicationRepositoryPort creditApplicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            RiskServicePort riskService,
            RiskEvaluationRepositoryPort riskEvaluationRepository,
            MeterRegistry meterRegistry) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.riskService = riskService;
        this.riskEvaluationRepository = riskEvaluationRepository;
        
        // Métricas personalizadas
        this.evaluationsCounter = Counter.builder("credit.evaluations.total")
                .description("Total de evaluaciones de crédito realizadas")
                .register(meterRegistry);
        
        this.approvalsCounter = Counter.builder("credit.approvals.total")
                .description("Total de créditos aprobados")
                .register(meterRegistry);
        
        this.rejectionsCounter = Counter.builder("credit.rejections.total")
                .description("Total de créditos rechazados")
                .register(meterRegistry);
        
        this.evaluationTimer = Timer.builder("credit.evaluation.time")
                .description("Tiempo de evaluación de crédito")
                .register(meterRegistry);
    }
    
    @Override
    public CreditApplication evaluateCreditApplication(Long creditApplicationId, String evaluator) {
        return evaluationTimer.record(() -> {
            log.info("Iniciando evaluación de solicitud de crédito ID: {}", creditApplicationId);
            
            CreditApplication creditApplication = getCreditApplication(creditApplicationId);
            
            // Verificar que se puede evaluar
            if (!creditApplication.canBeEvaluated()) {
                throw new InvalidOperationException("La solicitud no puede ser evaluada en su estado actual");
            }
            
            // Obtener afiliado para el documento
            Affiliate affiliate = affiliateRepository.findById(creditApplication.getAffiliateId())
                    .orElseThrow(() -> new AffiliateNotFoundException(creditApplication.getAffiliateId()));
            
            // Llamar al servicio de riesgo
            RiskEvaluation riskEvaluation = riskService.evaluateRisk(affiliate.getDocumentNumber());
            riskEvaluation.setCreditApplicationId(creditApplicationId);
            riskEvaluation.setDocumentNumber(affiliate.getDocumentNumber());
            riskEvaluation.setEvaluatedAt(LocalDateTime.now());
            
            // Guardar evaluación de riesgo
            riskEvaluation = riskEvaluationRepository.save(riskEvaluation);
            creditApplication.setRiskEvaluation(riskEvaluation);
            
            // Determinar aprobación o rechazo automático basado en el score
            if (creditApplication.canBeApproved()) {
                creditApplication.approve(evaluator);
                approvalsCounter.increment();
                log.info("Solicitud ID: {} APROBADA con score: {}", creditApplicationId, riskEvaluation.getScore());
            } else {
                creditApplication.reject(evaluator, "Score de riesgo insuficiente: " + riskEvaluation.getScore());
                rejectionsCounter.increment();
                log.info("Solicitud ID: {} RECHAZADA con score: {}", creditApplicationId, riskEvaluation.getScore());
            }
            
            evaluationsCounter.increment();
            
            return creditApplicationRepository.save(creditApplication);
        });
    }
    
    @Override
    public CreditApplication approveCreditApplication(Long creditApplicationId, String evaluator) {
        CreditApplication creditApplication = getCreditApplication(creditApplicationId);
        
        if (!creditApplication.canBeEvaluated()) {
            throw new InvalidOperationException("La solicitud no puede ser aprobada en su estado actual");
        }
        
        creditApplication.approve(evaluator);
        approvalsCounter.increment();
        
        log.info("Solicitud ID: {} aprobada manualmente por: {}", creditApplicationId, evaluator);
        
        return creditApplicationRepository.save(creditApplication);
    }
    
    @Override
    public CreditApplication rejectCreditApplication(Long creditApplicationId, String evaluator, String reason) {
        CreditApplication creditApplication = getCreditApplication(creditApplicationId);
        
        if (!creditApplication.canBeEvaluated()) {
            throw new InvalidOperationException("La solicitud no puede ser rechazada en su estado actual");
        }
        
        creditApplication.reject(evaluator, reason);
        rejectionsCounter.increment();
        
        log.info("Solicitud ID: {} rechazada manualmente por: {} - Razón: {}", creditApplicationId, evaluator, reason);
        
        return creditApplicationRepository.save(creditApplication);
    }
    
    private CreditApplication getCreditApplication(Long id) {
        return creditApplicationRepository.findById(id)
                .orElseThrow(() -> new CreditApplicationNotFoundException(id));
    }
}

