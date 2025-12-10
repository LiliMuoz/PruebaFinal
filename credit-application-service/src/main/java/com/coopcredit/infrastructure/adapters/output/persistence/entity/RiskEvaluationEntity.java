package com.coopcredit.infrastructure.adapters.output.persistence.entity;

import com.coopcredit.domain.model.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_evaluations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false, unique = true)
    private CreditApplicationEntity creditApplication;
    
    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;
    
    @Column(nullable = false)
    private Integer score;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;
    
    @Column(length = 500)
    private String recommendation;
    
    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;
}

