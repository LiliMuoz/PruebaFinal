package com.coopcredit.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Tests")
class UserTest {
    
    @Nested
    @DisplayName("Permisos por rol")
    class RolePermissions {
        
        @Test
        @DisplayName("Analista puede aprobar créditos")
        void analystCanApproveCredits() {
            User user = new User();
            user.setRole(Role.ANALISTA);
            
            assertTrue(user.canApproveCredits());
        }
        
        @Test
        @DisplayName("Admin puede aprobar créditos")
        void adminCanApproveCredits() {
            User user = new User();
            user.setRole(Role.ADMIN);
            
            assertTrue(user.canApproveCredits());
        }
        
        @Test
        @DisplayName("Afiliado no puede aprobar créditos")
        void affiliateCannotApproveCredits() {
            User user = new User();
            user.setRole(Role.AFILIADO);
            
            assertFalse(user.canApproveCredits());
        }
        
        @Test
        @DisplayName("Afiliado es identificado correctamente")
        void affiliateIsIdentifiedCorrectly() {
            User user = new User();
            user.setRole(Role.AFILIADO);
            
            assertTrue(user.isAffiliate());
            assertFalse(user.hasAdminPrivileges());
        }
        
        @Test
        @DisplayName("Admin tiene privilegios de administrador")
        void adminHasAdminPrivileges() {
            User user = new User();
            user.setRole(Role.ADMIN);
            
            assertTrue(user.hasAdminPrivileges());
        }
        
        @Test
        @DisplayName("Analista no tiene privilegios de administrador")
        void analystDoesNotHaveAdminPrivileges() {
            User user = new User();
            user.setRole(Role.ANALISTA);
            
            assertFalse(user.hasAdminPrivileges());
        }
    }
}

