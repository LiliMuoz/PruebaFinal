package com.coopcredit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Affiliate Domain Tests")
class AffiliateTest {
    
    @Nested
    @DisplayName("Validación de mayoría de edad")
    class AgeValidation {
        
        @Test
        @DisplayName("Debe ser mayor de edad si tiene más de 18 años")
        void shouldBeAdultWhenOlderThan18() {
            Affiliate affiliate = new Affiliate();
            affiliate.setBirthDate(LocalDate.now().minusYears(25));
            
            assertTrue(affiliate.isAdult());
        }
        
        @Test
        @DisplayName("Debe ser mayor de edad si tiene exactamente 18 años")
        void shouldBeAdultWhenExactly18() {
            Affiliate affiliate = new Affiliate();
            affiliate.setBirthDate(LocalDate.now().minusYears(18));
            
            assertTrue(affiliate.isAdult());
        }
        
        @Test
        @DisplayName("No debe ser mayor de edad si tiene menos de 18 años")
        void shouldNotBeAdultWhenYoungerThan18() {
            Affiliate affiliate = new Affiliate();
            affiliate.setBirthDate(LocalDate.now().minusYears(17));
            
            assertFalse(affiliate.isAdult());
        }
        
        @Test
        @DisplayName("No debe ser adulto si fecha de nacimiento es nula")
        void shouldNotBeAdultWhenBirthDateIsNull() {
            Affiliate affiliate = new Affiliate();
            affiliate.setBirthDate(null);
            
            assertFalse(affiliate.isAdult());
        }
    }
    
    @Nested
    @DisplayName("Nombre completo")
    class FullName {
        
        @Test
        @DisplayName("Debe generar nombre completo correctamente")
        void shouldGenerateFullNameCorrectly() {
            Affiliate affiliate = new Affiliate();
            affiliate.setFirstName("Juan");
            affiliate.setLastName("Pérez");
            
            assertEquals("Juan Pérez", affiliate.getFullName());
        }
    }
    
    @Nested
    @DisplayName("Validación de documento")
    class DocumentValidation {
        
        @Test
        @DisplayName("Documento válido cuando tiene número y tipo")
        void shouldBeValidWhenHasNumberAndType() {
            Affiliate affiliate = new Affiliate();
            affiliate.setDocumentNumber("123456789");
            affiliate.setDocumentType("CC");
            
            assertTrue(affiliate.hasValidDocument());
        }
        
        @Test
        @DisplayName("Documento inválido cuando falta número")
        void shouldBeInvalidWhenMissingNumber() {
            Affiliate affiliate = new Affiliate();
            affiliate.setDocumentNumber(null);
            affiliate.setDocumentType("CC");
            
            assertFalse(affiliate.hasValidDocument());
        }
        
        @Test
        @DisplayName("Documento inválido cuando falta tipo")
        void shouldBeInvalidWhenMissingType() {
            Affiliate affiliate = new Affiliate();
            affiliate.setDocumentNumber("123456789");
            affiliate.setDocumentType(null);
            
            assertFalse(affiliate.hasValidDocument());
        }
        
        @Test
        @DisplayName("Documento inválido cuando número está vacío")
        void shouldBeInvalidWhenNumberIsBlank() {
            Affiliate affiliate = new Affiliate();
            affiliate.setDocumentNumber("   ");
            affiliate.setDocumentType("CC");
            
            assertFalse(affiliate.hasValidDocument());
        }
    }
}

