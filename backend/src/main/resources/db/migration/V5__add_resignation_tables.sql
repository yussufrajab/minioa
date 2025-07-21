-- Add resignation tables to support the Resignation module
-- This script adds the tables for resignation requests and documents

-- Create resignation_requests table
CREATE TABLE resignation_requests (
    id VARCHAR(36) PRIMARY KEY,
    resignation_type VARCHAR(50) NOT NULL CHECK (resignation_type IN ('THREE_MONTH_NOTICE', 'TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT')),
    resignation_date DATE,
    last_working_date DATE,
    reason TEXT,
    payment_amount DECIMAL(19, 2),
    payment_confirmed BOOLEAN DEFAULT false,
    clearance_completed BOOLEAN DEFAULT false,
    handover_completed BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create resignation_documents table
CREATE TABLE resignation_documents (
    id VARCHAR(36) PRIMARY KEY,
    resignation_request_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL CHECK (document_type IN ('REQUEST_LETTER', 'EMPLOYEE_RESIGNATION_LETTER', 'PAYMENT_RECEIPT')),
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
    FOREIGN KEY (resignation_request_id) REFERENCES resignation_requests(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_resignation_requests_resignation_type ON resignation_requests(resignation_type);
CREATE INDEX idx_resignation_requests_resignation_date ON resignation_requests(resignation_date);
CREATE INDEX idx_resignation_requests_last_working_date ON resignation_requests(last_working_date);
CREATE INDEX idx_resignation_requests_payment_amount ON resignation_requests(payment_amount);
CREATE INDEX idx_resignation_requests_payment_confirmed ON resignation_requests(payment_confirmed);
CREATE INDEX idx_resignation_requests_clearance_completed ON resignation_requests(clearance_completed);
CREATE INDEX idx_resignation_requests_handover_completed ON resignation_requests(handover_completed);

CREATE INDEX idx_resignation_documents_request_id ON resignation_documents(resignation_request_id);
CREATE INDEX idx_resignation_documents_type ON resignation_documents(document_type);
CREATE INDEX idx_resignation_documents_verified ON resignation_documents(is_verified);
CREATE INDEX idx_resignation_documents_mandatory ON resignation_documents(is_mandatory);
CREATE INDEX idx_resignation_documents_uploaded_by ON resignation_documents(uploaded_by);
CREATE INDEX idx_resignation_documents_verified_by ON resignation_documents(verified_by);