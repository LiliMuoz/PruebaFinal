-- V2: Creación de tabla de afiliados
CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    document_number VARCHAR(20) NOT NULL UNIQUE,
    document_type VARCHAR(10) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    birth_date DATE NOT NULL,
    user_id BIGINT UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_affiliates_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

-- Índices
CREATE INDEX idx_affiliates_document_number ON affiliates(document_number);
CREATE INDEX idx_affiliates_email ON affiliates(email);
CREATE INDEX idx_affiliates_user_id ON affiliates(user_id);

-- Comentarios
COMMENT ON TABLE affiliates IS 'Tabla de afiliados de la cooperativa';
COMMENT ON COLUMN affiliates.document_type IS 'Tipo de documento: CC, CE, NIT, PASAPORTE';

