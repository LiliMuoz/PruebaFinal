package com.coopcredit.infrastructure.adapters.output.persistence.adapter;

import com.coopcredit.application.ports.output.UserRepositoryPort;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.coopcredit.infrastructure.adapters.output.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    
    private final UserJpaRepository userRepository;
    private final UserPersistenceMapper mapper;
    
    public UserPersistenceAdapter(UserJpaRepository userRepository, UserPersistenceMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }
    
    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = userRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

