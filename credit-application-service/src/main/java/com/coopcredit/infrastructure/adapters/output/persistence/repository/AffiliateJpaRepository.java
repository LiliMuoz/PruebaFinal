package com.coopcredit.infrastructure.adapters.output.persistence.repository;

import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, Long> {
    
    Optional<AffiliateEntity> findByDocumentNumber(String documentNumber);
    
    @Query("SELECT a FROM AffiliateEntity a WHERE a.user.id = :userId")
    Optional<AffiliateEntity> findByUserId(@Param("userId") Long userId);
    
    boolean existsByDocumentNumber(String documentNumber);
    
    boolean existsByEmail(String email);
}

