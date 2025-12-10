package com.coopcredit.domain.model;

import java.time.LocalDateTime;

/**
 * Modelo de dominio puro para Evaluación de Riesgo
 * POJO sin anotaciones de framework
 */
public class RiskEvaluation {
    
    private Long id;
    private Long creditApplicationId;
    private String documentNumber;
    private Integer score;
    private RiskLevel riskLevel;
    private String recommendation;
    private LocalDateTime evaluatedAt;
    
    // Constructor vacío
    public RiskEvaluation() {}
    
    // Constructor completo
    public RiskEvaluation(Long id, Long creditApplicationId, String documentNumber,
                         Integer score, RiskLevel riskLevel, String recommendation,
                         LocalDateTime evaluatedAt) {
        this.id = id;
        this.creditApplicationId = creditApplicationId;
        this.documentNumber = documentNumber;
        this.score = score;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
        this.evaluatedAt = evaluatedAt;
    }
    
    // Reglas de negocio
    public boolean isLowRisk() {
        return riskLevel == RiskLevel.LOW;
    }
    
    public boolean isMediumRisk() {
        return riskLevel == RiskLevel.MEDIUM;
    }
    
    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH;
    }
    
    public boolean meetsMinimumScore(int minimumScore) {
        return score != null && score >= minimumScore;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCreditApplicationId() { return creditApplicationId; }
    public void setCreditApplicationId(Long creditApplicationId) { this.creditApplicationId = creditApplicationId; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}

