-- Add retirement tables to support the Retirement module
-- This script adds the tables for retirement requests and documents

-- Create retirement_requests table
CREATE TABLE retirement_requests (
    id VARCHAR(36) PRIMARY KEY,
    retirement_type VARCHAR(20) NOT NULL CHECK (retirement_type IN ('COMPULSORY', 'VOLUNTARY', 'ILLNESS')),
    retirement_date DATE,
    last_working_date DATE,
    pension_eligibility_confirmed BOOLEAN DEFAULT false,
    clearance_completed BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create retirement_documents table
CREATE TABLE retirement_documents (
    id VARCHAR(36) PRIMARY KEY,
    retirement_request_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL CHECK (document_type IN ('REQUEST_LETTER', 'ARDHIL_HALI', 'BIRTH_CERTIFICATE', 'HEALTH_BOARD_REPORT', 'SICK_LEAVE_RECORDS')),
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
    FOREIGN KEY (retirement_request_id) REFERENCES retirement_requests(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_retirement_requests_retirement_type ON retirement_requests(retirement_type);
CREATE INDEX idx_retirement_requests_retirement_date ON retirement_requests(retirement_date);
CREATE INDEX idx_retirement_requests_last_working_date ON retirement_requests(last_working_date);
CREATE INDEX idx_retirement_requests_pension_eligibility ON retirement_requests(pension_eligibility_confirmed);
CREATE INDEX idx_retirement_requests_clearance_completed ON retirement_requests(clearance_completed);

CREATE INDEX idx_retirement_documents_request_id ON retirement_documents(retirement_request_id);
CREATE INDEX idx_retirement_documents_type ON retirement_documents(document_type);
CREATE INDEX idx_retirement_documents_verified ON retirement_documents(is_verified);
CREATE INDEX idx_retirement_documents_mandatory ON retirement_documents(is_mandatory);
CREATE INDEX idx_retirement_documents_uploaded_by ON retirement_documents(uploaded_by);
CREATE INDEX idx_retirement_documents_verified_by ON retirement_documents(verified_by);