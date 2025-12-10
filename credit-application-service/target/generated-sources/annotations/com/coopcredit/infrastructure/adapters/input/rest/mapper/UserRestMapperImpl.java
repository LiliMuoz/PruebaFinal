package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.Role;
import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.input.rest.dto.RegisterRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.UserResponse;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T18:40:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class UserRestMapperImpl implements UserRestMapper {

    @Override
    public User toDomain(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( request.username() );
        user.setPassword( request.password() );
        user.setEmail( request.email() );

        user.setRole( com.coopcredit.domain.model.Role.AFILIADO );

        return user;
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String email = null;
        Role role = null;
        boolean active = false;
        LocalDateTime createdAt = null;

        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        role = user.getRole();
        active = user.isActive();
        createdAt = user.getCreatedAt();

        UserResponse userResponse = new UserResponse( id, username, email, role, active, createdAt );

        return userResponse;
    }
}
