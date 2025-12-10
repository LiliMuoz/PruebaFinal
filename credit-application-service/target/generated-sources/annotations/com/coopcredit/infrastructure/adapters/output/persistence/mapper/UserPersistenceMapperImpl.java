package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:02:08-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserPersistenceMapperImpl implements UserPersistenceMapper {

    @Override
    public UserEntity toEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.active( user.isActive() );
        userEntity.createdAt( user.getCreatedAt() );
        userEntity.email( user.getEmail() );
        userEntity.id( user.getId() );
        userEntity.password( user.getPassword() );
        userEntity.role( user.getRole() );
        userEntity.updatedAt( user.getUpdatedAt() );
        userEntity.username( user.getUsername() );

        return userEntity.build();
    }

    @Override
    public User toDomain(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User user = new User();

        user.setId( entity.getId() );
        user.setUsername( entity.getUsername() );
        user.setPassword( entity.getPassword() );
        user.setEmail( entity.getEmail() );
        user.setRole( entity.getRole() );
        user.setActive( entity.isActive() );
        user.setCreatedAt( entity.getCreatedAt() );
        user.setUpdatedAt( entity.getUpdatedAt() );

        return user;
    }
}
