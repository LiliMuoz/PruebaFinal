package com.coopcredit.infrastructure.adapters.output.persistence.entity;

import com.coopcredit.domain.model.CreditApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
    name = "CreditApplication.withAffiliateAndRisk",
    attributeNodes = {
        @NamedAttributeNode("affiliate"),
        @NamedAttributeNode("riskEvaluation")
    }
)
public class CreditApplicationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;
    
    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;
    
    @Column(name = "term_months", nullable = false)
    private Integer termMonths;
    
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;
    
    @Column(length = 500)
    private String purpose;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditApplicationStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;
    
    @Column(name = "evaluated_by", length = 100)
    private String evaluatedBy;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskEvaluationEntity riskEvaluation;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

