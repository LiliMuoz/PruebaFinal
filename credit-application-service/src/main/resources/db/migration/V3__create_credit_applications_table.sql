-- V3: Creación de tabla de solicitudes de crédito
CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    term_months INTEGER NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    purpose VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    evaluated_at TIMESTAMP,
    evaluated_by VARCHAR(100),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_credit_applications_affiliate FOREIGN KEY (affiliate_id) 
        REFERENCES affiliates(id) ON DELETE CASCADE,
    CONSTRAINT chk_requested_amount CHECK (requested_amount > 0),
    CONSTRAINT chk_term_months CHECK (term_months > 0),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
);

-- Índices
CREATE INDEX idx_credit_applications_affiliate_id ON credit_applications(affiliate_id);
CREATE INDEX idx_credit_applications_status ON credit_applications(status);
CREATE INDEX idx_credit_applications_created_at ON credit_applications(created_at);

-- Comentarios
COMMENT ON TABLE credit_applications IS 'Tabla de solicitudes de crédito';
COMMENT ON COLUMN credit_applications.status IS 'Estado: PENDING, APPROVED, REJECTED, CANCELLED';

-- V3.1: Creación de tabla de evaluaciones de riesgo
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_application_id BIGINT NOT NULL UNIQUE,
    document_number VARCHAR(20) NOT NULL,
    score INTEGER NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    recommendation VARCHAR(500),
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_risk_evaluations_credit_application FOREIGN KEY (credit_application_id) 
        REFERENCES credit_applications(id) ON DELETE CASCADE,
    CONSTRAINT chk_score CHECK (score >= 0 AND score <= 1000),
    CONSTRAINT chk_risk_level CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Índices
CREATE INDEX idx_risk_evaluations_credit_application_id ON risk_evaluations(credit_application_id);
CREATE INDEX idx_risk_evaluations_document_number ON risk_evaluations(document_number);

-- Comentarios
COMMENT ON TABLE risk_evaluations IS 'Tabla de evaluaciones de riesgo crediticio';
COMMENT ON COLUMN risk_evaluations.risk_level IS 'Nivel de riesgo: LOW, MEDIUM, HIGH';

