package com.coopcredit.infrastructure.adapters.output.persistence.mapper;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapters.output.persistence.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:02:08-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AffiliatePersistenceMapperImpl implements AffiliatePersistenceMapper {

    @Override
    public AffiliateEntity toEntity(Affiliate affiliate) {
        if ( affiliate == null ) {
            return null;
        }

        AffiliateEntity.AffiliateEntityBuilder affiliateEntity = AffiliateEntity.builder();

        affiliateEntity.address( affiliate.getAddress() );
        affiliateEntity.birthDate( affiliate.getBirthDate() );
        affiliateEntity.createdAt( affiliate.getCreatedAt() );
        affiliateEntity.documentNumber( affiliate.getDocumentNumber() );
        affiliateEntity.documentType( affiliate.getDocumentType() );
        affiliateEntity.email( affiliate.getEmail() );
        affiliateEntity.firstName( affiliate.getFirstName() );
        affiliateEntity.id( affiliate.getId() );
        affiliateEntity.lastName( affiliate.getLastName() );
        affiliateEntity.phone( affiliate.getPhone() );
        affiliateEntity.updatedAt( affiliate.getUpdatedAt() );

        return affiliateEntity.build();
    }

    @Override
    public Affiliate toDomain(AffiliateEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Affiliate affiliate = new Affiliate();

        affiliate.setUserId( entityUserId( entity ) );
        affiliate.setId( entity.getId() );
        affiliate.setDocumentNumber( entity.getDocumentNumber() );
        affiliate.setDocumentType( entity.getDocumentType() );
        affiliate.setFirstName( entity.getFirstName() );
        affiliate.setLastName( entity.getLastName() );
        affiliate.setEmail( entity.getEmail() );
        affiliate.setPhone( entity.getPhone() );
        affiliate.setAddress( entity.getAddress() );
        affiliate.setBirthDate( entity.getBirthDate() );
        affiliate.setCreatedAt( entity.getCreatedAt() );
        affiliate.setUpdatedAt( entity.getUpdatedAt() );

        return affiliate;
    }

    private Long entityUserId(AffiliateEntity affiliateEntity) {
        if ( affiliateEntity == null ) {
            return null;
        }
        UserEntity user = affiliateEntity.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
