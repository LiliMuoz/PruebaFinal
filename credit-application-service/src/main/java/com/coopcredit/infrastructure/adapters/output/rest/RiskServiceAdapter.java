package com.coopcredit.infrastructure.adapters.output.rest;

import com.coopcredit.application.ports.output.RiskServicePort;
import com.coopcredit.domain.exception.RiskServiceException;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.domain.model.RiskLevel;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class RiskServiceAdapter implements RiskServicePort {
    
    private static final Logger log = LoggerFactory.getLogger(RiskServiceAdapter.class);
    
    private final WebClient webClient;
    private final Timer riskServiceTimer;
    private final Counter riskServiceErrorCounter;
    
    public RiskServiceAdapter(
            @Value("${risk-service.base-url}") String baseUrl,
            @Value("${risk-service.timeout}") int timeout,
            MeterRegistry meterRegistry) {
        
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        
        this.riskServiceTimer = Timer.builder("risk.service.request.time")
                .description("Tiempo de respuesta del servicio de riesgo")
                .register(meterRegistry);
        
        this.riskServiceErrorCounter = Counter.builder("risk.service.errors")
                .description("Errores del servicio de riesgo")
                .register(meterRegistry);
    }
    
    @Override
    public RiskEvaluation evaluateRisk(String documentNumber) {
        log.info("Consultando servicio de riesgo para documento: {}", documentNumber);
        
        return riskServiceTimer.record(() -> {
            try {
                RiskEvaluationResponse response = webClient.post()
                        .uri("/risk-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new RiskEvaluationRequest(documentNumber))
                        .retrieve()
                        .bodyToMono(RiskEvaluationResponse.class)
                        .timeout(Duration.ofSeconds(5))
                        .block();
                
                if (response == null) {
                    throw new RiskServiceException("Respuesta vacía del servicio de riesgo");
                }
                
                log.info("Evaluación de riesgo recibida - Score: {}, Nivel: {}", 
                        response.score(), response.riskLevel());
                
                RiskEvaluation evaluation = new RiskEvaluation();
                evaluation.setDocumentNumber(documentNumber);
                evaluation.setScore(response.score());
                evaluation.setRiskLevel(RiskLevel.valueOf(response.riskLevel()));
                evaluation.setRecommendation(response.recommendation());
                evaluation.setEvaluatedAt(LocalDateTime.now());
                
                return evaluation;
                
            } catch (WebClientResponseException e) {
                riskServiceErrorCounter.increment();
                log.error("Error en servicio de riesgo: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new RiskServiceException("Error al consultar servicio de riesgo: " + e.getMessage(), e);
            } catch (Exception e) {
                riskServiceErrorCounter.increment();
                log.error("Error inesperado en servicio de riesgo", e);
                throw new RiskServiceException("Error inesperado al consultar servicio de riesgo: " + e.getMessage(), e);
            }
        });
    }
    
    // DTOs internos para la comunicación REST
    record RiskEvaluationRequest(String documentNumber) {}
    
    record RiskEvaluationResponse(
            Integer score,
            String riskLevel,
            String recommendation
    ) {}
}

