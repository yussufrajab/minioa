-- Add cadre change tables to support the Change of Cadre module
-- This script adds the tables for cadre change requests and documents

-- Create cadre_change_requests table
CREATE TABLE cadre_change_requests (
    id VARCHAR(36) PRIMARY KEY,
    current_cadre VARCHAR(100) NOT NULL,
    proposed_cadre VARCHAR(100) NOT NULL,
    education_level VARCHAR(100),
    education_completion_year INTEGER,
    institution_attended VARCHAR(255),
    qualification_obtained VARCHAR(255),
    justification TEXT NOT NULL,
    current_salary_scale VARCHAR(50),
    proposed_salary_scale VARCHAR(50),
    years_of_experience INTEGER,
    relevant_experience TEXT,
    training_completed TEXT,
    skills_acquired TEXT,
    performance_rating VARCHAR(50),
    supervisor_recommendation TEXT,
    effective_date DATE,
    tcu_verification_required BOOLEAN DEFAULT false,
    tcu_verification_status VARCHAR(50),
    tcu_verification_date DATE,
    hr_assessment TEXT,
    budgetary_implications TEXT,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create cadre_change_documents table
CREATE TABLE cadre_change_documents (
    id VARCHAR(36) PRIMARY KEY,
    cadre_change_request_id VARCHAR(36) NOT NULL,
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
    FOREIGN KEY (cadre_change_request_id) REFERENCES cadre_change_requests(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create dismissal_requests table if it doesn't exist
CREATE TABLE IF NOT EXISTS dismissal_requests (
    id VARCHAR(36) PRIMARY KEY,
    dismissal_type VARCHAR(50) NOT NULL,
    dismissal_reason TEXT NOT NULL,
    incident_date DATE,
    evidence_provided TEXT,
    witness_statements TEXT,
    disciplinary_action_taken TEXT,
    previous_warnings TEXT,
    hr_assessment TEXT,
    legal_implications TEXT,
    reinstatement_possibility BOOLEAN DEFAULT false,
    appeal_deadline DATE,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create dismissal_documents table if it doesn't exist
CREATE TABLE IF NOT EXISTS dismissal_documents (
    id VARCHAR(36) PRIMARY KEY,
    dismissal_request_id VARCHAR(36) NOT NULL,
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
    FOREIGN KEY (dismissal_request_id) REFERENCES dismissal_requests(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_cadre_change_requests_current_cadre ON cadre_change_requests(current_cadre);
CREATE INDEX idx_cadre_change_requests_proposed_cadre ON cadre_change_requests(proposed_cadre);
CREATE INDEX idx_cadre_change_requests_education_level ON cadre_change_requests(education_level);
CREATE INDEX idx_cadre_change_requests_tcu_verification ON cadre_change_requests(tcu_verification_required);

CREATE INDEX idx_cadre_change_documents_request_id ON cadre_change_documents(cadre_change_request_id);
CREATE INDEX idx_cadre_change_documents_type ON cadre_change_documents(document_type);
CREATE INDEX idx_cadre_change_documents_verified ON cadre_change_documents(is_verified);

-- Add indexes for dismissal tables if they don't exist
CREATE INDEX IF NOT EXISTS idx_dismissal_requests_type ON dismissal_requests(dismissal_type);
CREATE INDEX IF NOT EXISTS idx_dismissal_documents_request_id ON dismissal_documents(dismissal_request_id);
CREATE INDEX IF NOT EXISTS idx_dismissal_documents_type ON dismissal_documents(document_type);