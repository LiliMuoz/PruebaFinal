package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    
    UserEntity toEntity(User user);
    
    User toDomain(UserEntity entity);
}

