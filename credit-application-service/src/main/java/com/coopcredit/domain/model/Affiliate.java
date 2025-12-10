package com.coopcredit.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de dominio puro para Afiliado
 * POJO sin anotaciones de framework
 */
public class Affiliate {
    
    private Long id;
    private String documentNumber;
    private String documentType;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    
    // Constructor vacío
    public Affiliate() {}
    
    // Constructor completo
    public Affiliate(Long id, String documentNumber, String documentType, String firstName, 
                    String lastName, String email, String phone, String address, 
                    LocalDate birthDate, LocalDateTime createdAt, LocalDateTime updatedAt, Long userId) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }
    
    // Reglas de negocio
    public boolean isAdult() {
        if (birthDate == null) return false;
        return LocalDate.now().minusYears(18).isAfter(birthDate) || 
               LocalDate.now().minusYears(18).isEqual(birthDate);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean hasValidDocument() {
        return documentNumber != null && !documentNumber.isBlank() &&
               documentType != null && !documentType.isBlank();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

