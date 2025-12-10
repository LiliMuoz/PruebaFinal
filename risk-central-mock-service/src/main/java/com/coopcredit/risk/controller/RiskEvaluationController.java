package com.coopcredit.risk.controller;

import com.coopcredit.risk.dto.RiskEvaluationRequest;
import com.coopcredit.risk.dto.RiskEvaluationResponse;
import com.coopcredit.risk.service.RiskEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk-evaluation")
@CrossOrigin(origins = "*")
public class RiskEvaluationController {
    
    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationController.class);
    
    private final RiskEvaluationService riskEvaluationService;
    
    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }
    
    @PostMapping
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(
            @Valid @RequestBody RiskEvaluationRequest request) {
        log.info("Solicitud de evaluación de riesgo recibida para documento: {}", request.documentNumber());
        
        // Simular latencia de un servicio externo real (100-500ms)
        simulateLatency();
        
        RiskEvaluationResponse response = riskEvaluationService.evaluateRisk(request.documentNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Risk Central Mock Service is running");
    }
    
    private void simulateLatency() {
        try {
            // Simular latencia entre 100-500ms
            long latency = 100 + (long) (Math.random() * 400);
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

