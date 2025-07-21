-- Create database schema for CSMS
-- This script creates the initial database structure

-- Create institutions table
CREATE TABLE institutions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255),
    address TEXT,
    telephone VARCHAR(20),
    vote_number VARCHAR(20),
    vote_description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true
);

-- Create users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    institution_id VARCHAR(36),
    is_account_non_expired BOOLEAN DEFAULT true,
    is_account_non_locked BOOLEAN DEFAULT true,
    is_credentials_non_expired BOOLEAN DEFAULT true,
    is_enabled BOOLEAN DEFAULT true,
    last_login_date TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    password_reset_token VARCHAR(255),
    password_reset_token_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (institution_id) REFERENCES institutions(id)
);

-- Create employees table
CREATE TABLE employees (
    id VARCHAR(36) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    profile_image VARCHAR(500),
    date_of_birth DATE NOT NULL,
    place_of_birth VARCHAR(255),
    region VARCHAR(100),
    country_of_birth VARCHAR(100),
    payroll_number VARCHAR(50) NOT NULL UNIQUE,
    zanzibar_id VARCHAR(20) NOT NULL UNIQUE,
    zssf_number VARCHAR(20) NOT NULL UNIQUE,
    rank VARCHAR(100),
    ministry VARCHAR(255),
    institution_id VARCHAR(36) NOT NULL,
    department VARCHAR(255),
    appointment_type VARCHAR(100),
    contract_type VARCHAR(100),
    recent_title_date DATE,
    current_reporting_office VARCHAR(255),
    current_workplace VARCHAR(255),
    employment_date DATE NOT NULL,
    confirmation_date DATE,
    loan_guarantee_status BOOLEAN DEFAULT false,
    employment_status VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20),
    gender VARCHAR(10),
    contact_address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (institution_id) REFERENCES institutions(id)
);

-- Create employee_documents table
CREATE TABLE employee_documents (
    id VARCHAR(36) PRIMARY KEY,
    employee_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Create employee_certificates table
CREATE TABLE employee_certificates (
    id VARCHAR(36) PRIMARY KEY,
    employee_id VARCHAR(36) NOT NULL,
    certificate_type VARCHAR(50) NOT NULL,
    institution_name VARCHAR(255),
    field_of_study VARCHAR(255),
    date_obtained DATE,
    certificate_number VARCHAR(100),
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Create requests table
CREATE TABLE requests (
    id VARCHAR(36) PRIMARY KEY,
    request_number VARCHAR(20) NOT NULL UNIQUE,
    employee_id VARCHAR(36) NOT NULL,
    submitted_by VARCHAR(36) NOT NULL,
    submission_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    request_type VARCHAR(20) NOT NULL,
    approver_id VARCHAR(36),
    approval_date TIMESTAMP,
    comments TEXT,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (submitted_by) REFERENCES users(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

-- Create request_documents table
CREATE TABLE request_documents (
    id VARCHAR(36) PRIMARY KEY,
    request_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (request_id) REFERENCES requests(id)
);

-- Create confirmation_requests table
CREATE TABLE confirmation_requests (
    id VARCHAR(36) PRIMARY KEY,
    probation_period_status VARCHAR(50),
    probation_end_date DATE,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create leave_without_pay_requests table
CREATE TABLE leave_without_pay_requests (
    id VARCHAR(36) PRIMARY KEY,
    reason TEXT NOT NULL,
    leave_start_date DATE NOT NULL,
    leave_end_date DATE NOT NULL,
    has_loan_guarantee BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create promotion_requests table
CREATE TABLE promotion_requests (
    id VARCHAR(36) PRIMARY KEY,
    promotion_type VARCHAR(50) NOT NULL,
    current_rank VARCHAR(100),
    proposed_rank VARCHAR(100),
    education_completion_date DATE,
    years_in_current_position INTEGER,
    FOREIGN KEY (id) REFERENCES requests(id)
);

-- Create complaints table
CREATE TABLE complaints (
    id VARCHAR(36) PRIMARY KEY,
    complaint_number VARCHAR(20) NOT NULL UNIQUE,
    employee_id VARCHAR(36) NOT NULL,
    complaint_type VARCHAR(50) NOT NULL,
    complainant_name VARCHAR(255),
    respondent_name VARCHAR(255),
    complaint_source VARCHAR(100),
    incident_date DATE,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    resolved_by VARCHAR(36),
    resolution TEXT,
    resolution_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (resolved_by) REFERENCES users(id)
);

-- Create audit_logs table
CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(36),
    ip_address VARCHAR(45),
    user_agent TEXT,
    before_value TEXT,
    after_value TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN,
    error_message TEXT
);

-- Create indexes for better performance
CREATE INDEX idx_employees_payroll_number ON employees(payroll_number);
CREATE INDEX idx_employees_zanzibar_id ON employees(zanzibar_id);
CREATE INDEX idx_employees_institution_id ON employees(institution_id);
CREATE INDEX idx_employees_employment_status ON employees(employment_status);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_institution_id ON users(institution_id);

CREATE INDEX idx_requests_employee_id ON requests(employee_id);
CREATE INDEX idx_requests_submitted_by ON requests(submitted_by);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_requests_request_type ON requests(request_type);
CREATE INDEX idx_requests_submission_date ON requests(submission_date);

CREATE INDEX idx_complaints_employee_id ON complaints(employee_id);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_complaint_type ON complaints(complaint_type);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_entity_type_id ON audit_logs(entity_type, entity_id);