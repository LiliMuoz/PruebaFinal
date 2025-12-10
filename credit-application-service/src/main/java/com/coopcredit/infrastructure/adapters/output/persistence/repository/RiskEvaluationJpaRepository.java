package com.coopcredit.infrastructure.adapters.output.persistence.repository;

import com.coopcredit.infrastructure.adapters.output.persistence.entity.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskEvaluationJpaRepository extends JpaRepository<RiskEvaluationEntity, Long> {
    
    @Query("SELECT r FROM RiskEvaluationEntity r WHERE r.creditApplication.id = :creditApplicationId")
    Optional<RiskEvaluationEntity> findByCreditApplicationId(@Param("creditApplicationId") Long creditApplicationId);
}

