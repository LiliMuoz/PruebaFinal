package com.coopcredit.domain.model;

/**
 * Enum de estados de la solicitud de crédito
 */
public enum CreditApplicationStatus {
    PENDING,    // Pendiente de evaluación
    APPROVED,   // Aprobada
    REJECTED,   // Rechazada
    CANCELLED   // Cancelada por el afiliado
}

