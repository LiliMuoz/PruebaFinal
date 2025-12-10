package com.coopcredit.infrastructure.adapters.output.persistence.repository;

import com.coopcredit.domain.model.CreditApplicationStatus;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {
    
    @EntityGraph(value = "CreditApplication.withAffiliateAndRisk")
    @Query("SELECT c FROM CreditApplicationEntity c WHERE c.id = :id")
    Optional<CreditApplicationEntity> findByIdWithRelations(@Param("id") Long id);
    
    @Query("SELECT c FROM CreditApplicationEntity c JOIN FETCH c.affiliate WHERE c.affiliate.id = :affiliateId ORDER BY c.createdAt DESC")
    List<CreditApplicationEntity> findByAffiliateId(@Param("affiliateId") Long affiliateId);
    
    @Query("SELECT c FROM CreditApplicationEntity c JOIN FETCH c.affiliate ORDER BY c.createdAt DESC")
    List<CreditApplicationEntity> findAllWithAffiliate();
    
    List<CreditApplicationEntity> findByStatus(CreditApplicationStatus status);
}

