package com.coopcredit.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio puro para Solicitud de Crédito
 * POJO sin anotaciones de framework
 */
public class CreditApplication {
    
    private Long id;
    private Long affiliateId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal interestRate;
    private String purpose;
    private CreditApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime evaluatedAt;
    private String evaluatedBy;
    private String rejectionReason;
    private RiskEvaluation riskEvaluation;
    
    // Constantes de negocio
    public static final BigDecimal MIN_AMOUNT = new BigDecimal("100000");
    public static final BigDecimal MAX_AMOUNT = new BigDecimal("50000000");
    public static final int MIN_TERM_MONTHS = 6;
    public static final int MAX_TERM_MONTHS = 60;
    public static final int MINIMUM_SCORE_FOR_APPROVAL = 600;
    
    // Constructor vacío
    public CreditApplication() {}
    
    // Constructor completo
    public CreditApplication(Long id, Long affiliateId, BigDecimal requestedAmount, 
                            Integer termMonths, BigDecimal interestRate, String purpose,
                            CreditApplicationStatus status, LocalDateTime createdAt, 
                            LocalDateTime updatedAt, LocalDateTime evaluatedAt,
                            String evaluatedBy, String rejectionReason, RiskEvaluation riskEvaluation) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.purpose = purpose;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.evaluatedAt = evaluatedAt;
        this.evaluatedBy = evaluatedBy;
        this.rejectionReason = rejectionReason;
        this.riskEvaluation = riskEvaluation;
    }
    
    // Reglas de negocio
    public boolean isValidAmount() {
        if (requestedAmount == null) return false;
        return requestedAmount.compareTo(MIN_AMOUNT) >= 0 && 
               requestedAmount.compareTo(MAX_AMOUNT) <= 0;
    }
    
    public boolean isValidTerm() {
        if (termMonths == null) return false;
        return termMonths >= MIN_TERM_MONTHS && termMonths <= MAX_TERM_MONTHS;
    }
    
    public boolean canBeEvaluated() {
        return status == CreditApplicationStatus.PENDING;
    }
    
    public boolean canBeApproved() {
        if (riskEvaluation == null) return false;
        return riskEvaluation.getScore() >= MINIMUM_SCORE_FOR_APPROVAL;
    }
    
    public void approve(String evaluator) {
        this.status = CreditApplicationStatus.APPROVED;
        this.evaluatedAt = LocalDateTime.now();
        this.evaluatedBy = evaluator;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void reject(String evaluator, String reason) {
        this.status = CreditApplicationStatus.REJECTED;
        this.evaluatedAt = LocalDateTime.now();
        this.evaluatedBy = evaluator;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal calculateMonthlyPayment() {
        if (requestedAmount == null || interestRate == null || termMonths == null) {
            return BigDecimal.ZERO;
        }
        // Fórmula de cuota mensual: P * (r * (1+r)^n) / ((1+r)^n - 1)
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(termMonths);
        BigDecimal numerator = requestedAmount.multiply(monthlyRate).multiply(onePlusRPowN);
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, java.math.RoundingMode.HALF_UP);
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAffiliateId() { return affiliateId; }
    public void setAffiliateId(Long affiliateId) { this.affiliateId = affiliateId; }
    
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public CreditApplicationStatus getStatus() { return status; }
    public void setStatus(CreditApplicationStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
    
    public String getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(String evaluatedBy) { this.evaluatedBy = evaluatedBy; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public RiskEvaluation getRiskEvaluation() { return riskEvaluation; }
    public void setRiskEvaluation(RiskEvaluation riskEvaluation) { this.riskEvaluation = riskEvaluation; }
}

