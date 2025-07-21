-- Add service extension tables to support the Service Extension module
-- This script adds the tables for service extension requests and documents

-- Create service_extension_requests table
CREATE TABLE service_extension_requests (
    id VARCHAR(36) PRIMARY KEY,
    extension_duration_years INTEGER NOT NULL,
    retirement_eligibility_date DATE,
    notification_sent_date TIMESTAMP,
    expiration_warning_sent BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create service_extension_documents table
CREATE TABLE service_extension_documents (
    id VARCHAR(36) PRIMARY KEY,
    service_extension_request_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    description TEXT,
    is_mandatory BOOLEAN DEFAULT false,
    is_verified BOOLEAN DEFAULT false,
    verification_notes TEXT,
    uploaded_by VARCHAR(36),
    verified_by VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (service_extension_request_id) REFERENCES service_extension_requests(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_service_extension_requests_expiration_warning_sent ON service_extension_requests(expiration_warning_sent);

CREATE INDEX idx_service_extension_documents_request_id ON service_extension_documents(service_extension_request_id);
CREATE INDEX idx_service_extension_documents_type ON service_extension_documents(document_type);
CREATE INDEX idx_service_extension_documents_verified ON service_extension_documents(is_verified);
CREATE INDEX idx_service_extension_documents_mandatory ON service_extension_documents(is_mandatory);