package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.input.rest.dto.RegisterRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRestMapper {
    
    @Mapping(target = "role", expression = "java(com.coopcredit.domain.model.Role.AFILIADO)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toDomain(RegisterRequest request);
    
    UserResponse toResponse(User user);
}

