package com.coopcredit.infrastructure.adapters.input.rest.mapper;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AffiliateResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T18:40:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class AffiliateRestMapperImpl implements AffiliateRestMapper {

    @Override
    public Affiliate toDomain(AffiliateRequest request) {
        if ( request == null ) {
            return null;
        }

        Affiliate affiliate = new Affiliate();

        affiliate.setDocumentNumber( request.documentNumber() );
        affiliate.setDocumentType( request.documentType() );
        affiliate.setFirstName( request.firstName() );
        affiliate.setLastName( request.lastName() );
        affiliate.setEmail( request.email() );
        affiliate.setPhone( request.phone() );
        affiliate.setAddress( request.address() );
        affiliate.setBirthDate( request.birthDate() );
        affiliate.setUserId( request.userId() );

        return affiliate;
    }

    @Override
    public AffiliateResponse toResponse(Affiliate affiliate) {
        if ( affiliate == null ) {
            return null;
        }

        Long id = null;
        String documentNumber = null;
        String documentType = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String phone = null;
        String address = null;
        LocalDate birthDate = null;
        Long userId = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = affiliate.getId();
        documentNumber = affiliate.getDocumentNumber();
        documentType = affiliate.getDocumentType();
        firstName = affiliate.getFirstName();
        lastName = affiliate.getLastName();
        email = affiliate.getEmail();
        phone = affiliate.getPhone();
        address = affiliate.getAddress();
        birthDate = affiliate.getBirthDate();
        userId = affiliate.getUserId();
        createdAt = affiliate.getCreatedAt();
        updatedAt = affiliate.getUpdatedAt();

        String fullName = affiliate.getFullName();

        AffiliateResponse affiliateResponse = new AffiliateResponse( id, documentNumber, documentType, firstName, lastName, fullName, email, phone, address, birthDate, userId, createdAt, updatedAt );

        return affiliateResponse;
    }
}
