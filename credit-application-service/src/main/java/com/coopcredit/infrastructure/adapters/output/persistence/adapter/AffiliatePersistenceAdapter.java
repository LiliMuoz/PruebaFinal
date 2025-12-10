package com.coopcredit.infrastructure.adapters.output.persistence.adapter;

import com.coopcredit.application.ports.output.AffiliateRepositoryPort;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.mapper.AffiliatePersistenceMapper;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.AffiliateJpaRepository;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AffiliatePersistenceAdapter implements AffiliateRepositoryPort {
    
    private final AffiliateJpaRepository affiliateRepository;
    private final UserJpaRepository userRepository;
    private final AffiliatePersistenceMapper mapper;
    
    public AffiliatePersistenceAdapter(
            AffiliateJpaRepository affiliateRepository,
            UserJpaRepository userRepository,
            AffiliatePersistenceMapper mapper) {
        this.affiliateRepository = affiliateRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        
        // Asociar usuario si existe
        if (affiliate.getUserId() != null) {
            userRepository.findById(affiliate.getUserId())
                    .ifPresent(entity::setUser);
        }
        
        AffiliateEntity savedEntity = affiliateRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Affiliate> findById(Long id) {
        return affiliateRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        return affiliateRepository.findByDocumentNumber(documentNumber)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Affiliate> findByUserId(Long userId) {
        return affiliateRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return affiliateRepository.existsByDocumentNumber(documentNumber);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return affiliateRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteById(Long id) {
        affiliateRepository.deleteById(id);
    }
    
    @Override
    public List<Affiliate> findAll() {
        return affiliateRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

