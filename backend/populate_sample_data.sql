-- CSMS Sample Data Population Script
-- This script populates the database with sample data adapted from mfano.sql

-- Start transaction
BEGIN;

-- Insert Institutions first (referenced by employees)
INSERT INTO institutions (id, name, created_at, updated_at) VALUES
('cmd06nn7n0001e67w2h5rf86x', 'OFISI YA RAIS, FEDHA NA MIPANGO', NOW(), NOW()),
('cmd06nn7r0002e67w8df8thtn', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI', NOW(), NOW()),
('cmd06nn7s0003e67wkj9r2btz', 'WIZARA YA AFYA', NOW(), NOW()),
('cmd06nn7t0004e67wm4k5x8vw', 'WIZARA YA KILIMO UMWAGILIAJI MALIASILI NA MIFUGO', NOW(), NOW()),
('cmd06nn7u0005e67wn6l9f3qx', 'WIZARA YA UJENZI MAWASILIANO NA UCHUKUZI', NOW(), NOW());

-- Insert sample employees
INSERT INTO employees (
    id, full_name, profile_image, date_of_birth, place_of_birth, region, country_of_birth,
    payroll_number, zanzibar_id, zssf_number, rank, ministry, institution_id, department,
    appointment_type, contract_type, recent_title_date, current_reporting_office, current_workplace,
    employment_date, confirmation_date, retirement_date, employment_status, phone_number, gender,
    contact_address, created_at, updated_at
) VALUES
('emp_001', 'Mwalimu Hassan Khamis', 'https://placehold.co/150x150.png?text=MHK', 
 '1987-08-03', 'Kizimbani', 'Kaskazini Unguja', 'Tanzania',
 'PAY0001', '1905800010', 'ZSSF001', 'Principal Secretary', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'cmd06nn7n0001e67w2h5rf86x', 'Administration', 'Permanent', 'Full-time',
 '2022-02-13', 'Director of Administration', 'Head Office',
 '2022-02-13', '2023-02-13', '2047-08-03', 'ACTIVE', '0777-757808', 'Male',
 'P.O. Box 3445, Wete, Zanzibar', NOW(), NOW()),

('emp_003', 'Said Juma Nassor', 'https://placehold.co/150x150.png?text=SJN',
 '1970-08-28', 'Stone Town', 'Kaskazini Pemba', 'Tanzania',
 'PAY0003', '1905850030', 'ZSSF003', 'Director of Finance', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'cmd06nn7n0001e67w2h5rf86x', 'Finance', 'Permanent', 'Full-time',
 '2003-12-04', 'Director of Finance', 'Head Office',
 '2003-12-04', '2004-12-03', '2030-08-28', 'ACTIVE', '0777-817032', 'Male',
 'P.O. Box 3765, Mkoani, Zanzibar', NOW(), NOW()),

('emp_004', 'Mwanasha Saleh Omar', 'https://placehold.co/150x150.png?text=MSO',
 '1986-10-07', 'Mkoani', 'Kusini Unguja', 'Tanzania',
 'PAY0004', '1906900040', 'ZSSF004', 'Afisa Muandamizi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'cmd06nn7n0001e67w2h5rf86x', 'Budget', 'Permanent', 'Full-time',
 '2006-05-05', 'Director of Budget', 'Head Office',
 '2006-05-05', '2007-05-05', '2046-10-07', 'ACTIVE', '0777-207026', 'Female',
 'P.O. Box 3536, Vitongoji, Zanzibar', NOW(), NOW()),

('emp_005', 'Ahmed Khamis Vuai', 'https://placehold.co/150x150.png?text=AKV',
 '1973-02-06', 'Bububu', 'Mjini Magharibi', 'Tanzania',
 'PAY0005', '1907880050', 'ZSSF005', 'Senior Administrative Officer', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'cmd06nn7n0001e67w2h5rf86x', 'Human Resources', 'Permanent', 'Full-time',
 '2008-05-20', 'Director of Human Resources', 'Head Office',
 '2008-05-20', NULL, '2033-02-06', 'ACTIVE', '0777-275399', 'Male',
 'P.O. Box 5695, Mkoani, Zanzibar', NOW(), NOW()),

('emp_006', 'Zeinab Mohammed Ali', 'https://placehold.co/150x150.png?text=ZMA',
 '1969-06-17', 'Mkoani', 'Kaskazini Pemba', 'Tanzania',
 'PAY0006', '1908920060', 'ZSSF006', 'Afisa Mkuu Daraja la II', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'cmd06nn7n0001e67w2h5rf86x', 'Registry', 'Permanent', 'Full-time',
 '2012-10-03', 'Director of Registry', 'Head Office',
 '2012-10-03', '2013-10-03', '2029-06-17', 'ACTIVE', '0777-450591', 'Female',
 'P.O. Box 6525, Stone Town, Zanzibar', NOW(), NOW()),

('emp_007', 'Prof. Omar Juma Khamis', 'https://placehold.co/150x150.png?text=POJK',
 '1974-10-24', 'Mkoani', 'Kaskazini Pemba', 'Tanzania',
 'PAY0007', '1905650070', 'ZSSF007', 'Principal Secretary', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI',
 'cmd06nn7r0002e67w8df8thtn', 'Policy', 'Permanent', 'Full-time',
 '2008-08-24', 'Director of Policy', 'Head Office',
 '2008-08-24', '2009-08-24', '2034-10-24', 'ACTIVE', '0777-696363', 'Male',
 'P.O. Box 7280, Mkoani, Zanzibar', NOW(), NOW()),

('emp_008', 'Dr. Amina Hassan Said', 'https://placehold.co/150x150.png?text=DAHS',
 '1962-05-05', 'Chake Chake', 'Mjini Magharibi', 'Tanzania',
 'PAY0008', '1906720080', 'ZSSF008', 'Director of Education', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI',
 'cmd06nn7r0002e67w8df8thtn', 'Primary Education', 'Permanent', 'Full-time',
 '2004-11-23', 'Director of Primary Education', 'Head Office',
 '2004-11-23', '2005-11-23', '2022-05-05', 'RETIRED', '0777-140697', 'Female',
 'P.O. Box 4560, Mkoani, Zanzibar', NOW(), NOW()),

('emp_009', 'Hamad Ali Khamis', 'https://placehold.co/150x150.png?text=HAK',
 '1989-07-21', 'Wete', 'Mjini Magharibi', 'Tanzania',
 'PAY0009', '1907800090', 'ZSSF009', 'Senior Education Officer', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI',
 'cmd06nn7r0002e67w8df8thtn', 'Secondary Education', 'Permanent', 'Full-time',
 '2001-04-11', 'Director of Secondary Education', 'Head Office',
 '2001-04-11', '2002-04-11', '2049-07-21', 'ACTIVE', '0777-792099', 'Male',
 'P.O. Box 2350, Mkoani, Zanzibar', NOW(), NOW()),

('emp8', 'Khadija Nassor', 'https://placehold.co/150x150.png?text=KN',
 '1985-03-21', 'Stone Town', 'Mjini Magharibi', 'Tanzania',
 'PAY008', '987654321', 'ZSSF008', 'Senior Manager', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI',
 'cmd06nn7r0002e67w8df8thtn', 'Education', 'Permanent', 'Full-time',
 '2020-01-16', 'Director of Education', 'Education Office',
 '2020-01-16', '2021-01-16', '2045-03-21', 'ACTIVE', '0777-888-999', 'Female',
 'P.O. Box 888, Stone Town, Zanzibar', NOW(), NOW()),

('emp9', 'Yussuf Makame', 'https://placehold.co/150x150.png?text=YM',
 '1982-07-11', 'Wete', 'Kaskazini Pemba', 'Tanzania',
 'PAY009', '123987654', 'ZSSF009', 'Technical Officer', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI',
 'cmd06nn7r0002e67w8df8thtn', 'Technical Services', 'Permanent', 'Full-time',
 '2019-06-02', 'Director of Technical Services', 'Technical Office',
 '2019-06-02', '2020-06-02', '2042-07-11', 'ACTIVE', '0777-999-111', 'Male',
 'P.O. Box 999, Wete, Pemba', NOW(), NOW());

-- Insert sample users
INSERT INTO users (
    id, username, email, password_hash, first_name, last_name, role, is_active,
    institution_id, employee_id, created_at, updated_at
) VALUES
('cmd06nnbn000le67wtg41s3su', 'hro001', 'hro001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Hassan', 'Khamis', 'HRO', true, 'cmd06nn7n0001e67w2h5rf86x', 'emp_001', NOW(), NOW()),
('cmd06nnbb000be67wwgil78yv', 'hhrmd001', 'hhrmd001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Said', 'Nassor', 'HHRMD', true, 'cmd06nn7n0001e67w2h5rf86x', 'emp_003', NOW(), NOW()),
('cmd06nnbd000de67wb6e6ild5', 'hrmo001', 'hrmo001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Mwanasha', 'Omar', 'HRMO', true, 'cmd06nn7n0001e67w2h5rf86x', 'emp_004', NOW(), NOW()),
('cmd06nnbq000ne67wwmiwxuo8', 'do001', 'do001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Ahmed', 'Vuai', 'DO', true, 'cmd06nn7n0001e67w2h5rf86x', 'emp_005', NOW(), NOW()),
('cmd06nnbu000re67wdeax0fwp', 'emp001', 'emp001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Zeinab', 'Ali', 'EMP', true, 'cmd06nn7n0001e67w2h5rf86x', 'emp_006', NOW(), NOW()),
('cmd06nnbx000te67ww4cbaug7', 'admin001', 'admin001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Omar', 'Khamis', 'ADMIN', true, 'cmd06nn7r0002e67w8df8thtn', 'emp_007', NOW(), NOW()),
('cmd06nnbz000ve67wncnv4etg', 'cscs001', 'cscs001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Amina', 'Said', 'CSCS', true, 'cmd06nn7r0002e67w8df8thtn', 'emp_008', NOW(), NOW()),
('cmd06nnbl000je67wtl28pk42', 'po001', 'po001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Hamad', 'Khamis', 'PO', true, 'cmd06nn7r0002e67w8df8thtn', 'emp_009', NOW(), NOW()),
('cmd06nnbs000pe67woh62ey8r', 'hrrp001', 'hrrp001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Khadija', 'Nassor', 'HRRP', true, 'cmd06nn7r0002e67w8df8thtn', 'emp8', NOW(), NOW()),
('cmd059ir10002e6d86l802ljc', 'test001', 'test001@csms.go.tz', '$2a$10$N.zmdr9k7uOsaQsuvh3V5l9mSBwEXaOMBpnr..O7l9RHG.jO06he6', 'Yussuf', 'Makame', 'EMP', true, 'cmd06nn7r0002e67w8df8thtn', 'emp9', NOW(), NOW());

-- Insert sample requests (base request entries)
INSERT INTO requests (
    id, request_number, employee_id, submitter_id, status, request_type, priority, description,
    submission_date, created_by, created_at, updated_at
) VALUES
('req_001', 'CONF-2025-001', 'emp_005', 'cmd06nnbn000le67wtg41s3su', 'SUBMITTED', 'CONFIRMATION', 'MEDIUM', 'Employment confirmation request for Ahmed Khamis Vuai', '2025-01-15', 'cmd06nnbn000le67wtg41s3su', NOW(), NOW()),
('req_002', 'PROM-2025-001', 'emp_006', 'cmd06nnbn000le67wtg41s3su', 'APPROVED', 'PROMOTION', 'HIGH', 'Promotion request for Zeinab Mohammed Ali', '2025-01-10', 'cmd06nnbn000le67wtg41s3su', NOW(), NOW()),
('req_003', 'LWOP-2025-001', 'emp_004', 'cmd06nnbn000le67wtg41s3su', 'UNDER_REVIEW', 'LWOP', 'LOW', 'Leave without pay request for personal reasons', '2025-01-12', 'cmd06nnbn000le67wtg41s3su', NOW(), NOW()),
('req_004', 'CADRE-2025-001', 'emp_003', 'cmd06nnbn000le67wtg41s3su', 'PENDING', 'CADRE_CHANGE', 'MEDIUM', 'Cadre change request for career advancement', '2025-01-14', 'cmd06nnbn000le67wtg41s3su', NOW(), NOW()),
('req_005', 'RET-2025-001', 'emp_008', 'cmd06nnbn000le67wtg41s3su', 'APPROVED', 'RETIREMENT', 'HIGH', 'Voluntary retirement request', '2025-01-08', 'cmd06nnbn000le67wtg41s3su', NOW(), NOW());

-- Insert confirmation requests
INSERT INTO confirmation_requests (
    request_id, probation_start_date, probation_end_date, performance_rating, supervisor_recommendation,
    hr_assessment, proposed_confirmation_date, current_salary, proposed_salary, created_at, updated_at
) VALUES
('req_001', '2008-05-20', '2009-05-20', 'EXCELLENT', 'STRONGLY_RECOMMENDED', 'SATISFACTORY', '2025-02-01', 450000.00, 480000.00, NOW(), NOW());

-- Insert promotion requests
INSERT INTO promotion_requests (
    request_id, current_position, proposed_position, current_grade, proposed_grade, current_salary,
    proposed_salary, promotion_type, years_in_current_position, performance_rating, qualifications_met,
    supervisor_recommendation, justification, effective_date, created_at, updated_at
) VALUES
('req_002', 'Afisa Mkuu Daraja la II', 'Afisa Mkuu Daraja la I', 'ZPS 4.1', 'ZPS 5.1', 380000.00, 450000.00, 'PERFORMANCE', 12, 'EXCELLENT', true, 'STRONGLY_RECOMMENDED', 'Excellent performance and leadership skills demonstrated', '2025-02-01', NOW(), NOW());

-- Insert leave without pay requests
INSERT INTO leave_without_pay_requests (
    id, reason, leave_start_date, leave_end_date, has_loan_guarantee, created_at, updated_at
) VALUES
('req_003', 'Personal family matters requiring extended leave', '2025-03-01', '2025-09-01', false, NOW(), NOW());

-- Insert cadre change requests
INSERT INTO cadre_change_requests (
    id, current_cadre, proposed_cadre, current_salary_scale, proposed_salary_scale, education_level,
    qualification_obtained, institution_attended, education_completion_year, justification,
    tcu_verification_required, tcu_verification_status, effective_date, created_at, updated_at
) VALUES
('req_004', 'Director of Finance', 'Principal Secretary', 'ZPS 6.1', 'ZPS 8.1', 'Master Degree', 'Master of Public Administration', 'University of Dar es Salaam', 2018, 'Career advancement and enhanced qualifications', true, 'PENDING', '2025-03-01', NOW(), NOW());

-- Insert retirement requests
INSERT INTO retirement_requests (
    id, retirement_type, retirement_date, last_working_date, pension_eligibility_confirmed, clearance_completed, created_at, updated_at
) VALUES
('req_005', 'VOLUNTARY', '2022-05-05', '2022-04-30', true, true, NOW(), NOW());

-- Insert sample complaints
INSERT INTO complaints (
    id, complaint_number, complainant_name, complainant_phone, complainant_id, complaint_type,
    complaint_subject, complaint_details, complaint_date, status, assigned_officer_id,
    created_at, updated_at
) VALUES
('comp_001', 'COMP-2025-001', 'Fatma Ali Hassan', '0777-123456', 'emp_004', 'WORKPLACE_HARASSMENT', 'Unfair Treatment at Workplace', 'I am experiencing unfair treatment from my supervisor including excessive workload and discriminatory behavior. This has affected my performance and well-being at work.', '2025-01-16', 'SUBMITTED', 'cmd06nnbq000ne67wwmiwxuo8', NOW(), NOW()),
('comp_002', 'COMP-2025-002', 'Mohammed Khamis Said', '0777-654321', 'emp9', 'DISCRIMINATION', 'Discrimination in Promotion Process', 'I believe I was unfairly passed over for promotion due to discriminatory practices. Despite meeting all qualifications, less qualified candidates were selected.', '2025-01-18', 'UNDER_REVIEW', 'cmd06nnbq000ne67wwmiwxuo8', NOW(), NOW()),
('comp_003', 'COMP-2025-003', 'Salma Juma Omar', '0777-789012', 'emp8', 'SERVICE_DELIVERY', 'Poor Service Delivery in HR Department', 'The HR department has been providing poor service delivery with delayed responses to employee queries and inadequate support for staff development programs.', '2025-01-20', 'PENDING', 'cmd06nnbq000ne67wwmiwxuo8', NOW(), NOW());

-- Insert sample audit logs
INSERT INTO audit_logs (
    id, user_id, action, entity_type, entity_id, before_value, after_value, ip_address,
    user_agent, session_id, created_at, updated_at
) VALUES
('audit_001', 'cmd06nnbn000le67wtg41s3su', 'CREATE', 'Request', 'req_001', '{}', '{"status": "SUBMITTED", "type": "CONFIRMATION"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'session_001', NOW(), NOW()),
('audit_002', 'cmd06nnbb000be67wwgil78yv', 'UPDATE', 'Request', 'req_002', '{"status": "SUBMITTED"}', '{"status": "APPROVED"}', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'session_002', NOW(), NOW()),
('audit_003', 'cmd06nnbd000de67wb6e6ild5', 'UPDATE', 'Request', 'req_003', '{"status": "SUBMITTED"}', '{"status": "UNDER_REVIEW"}', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'session_003', NOW(), NOW());

-- Insert sample employee documents
INSERT INTO employee_documents (
    id, employee_id, document_type, document_name, file_path, file_size, created_at, updated_at
) VALUES
('doc_001', 'emp_001', 'ARDHIL_HALI', 'Ardhil Hali - Hassan Khamis', '/documents/ardhil-hali-1.pdf', 1024000, NOW(), NOW()),
('doc_002', 'emp_001', 'CONFIRMATION_LETTER', 'Confirmation Letter - Hassan Khamis', '/documents/confirmation-1.pdf', 512000, NOW(), NOW()),
('doc_003', 'emp_003', 'JOB_CONTRACT', 'Job Contract - Said Nassor', '/documents/contract-3.pdf', 768000, NOW(), NOW()),
('doc_004', 'emp_004', 'BIRTH_CERTIFICATE', 'Birth Certificate - Mwanasha Omar', '/documents/birth-cert-4.pdf', 256000, NOW(), NOW());

-- Insert sample employee certificates
INSERT INTO employee_certificates (
    id, employee_id, certificate_type, certificate_name, institution_name, completion_date,
    file_path, created_at, updated_at
) VALUES
('cert_001', 'emp_001', 'BACHELOR', 'Bachelor of Education', 'University of Dar es Salaam', '2010-11-15', '/certificates/bachelor-1.pdf', NOW(), NOW()),
('cert_002', 'emp_003', 'MASTER', 'Master of Business Administration', 'Open University of Tanzania', '2015-12-20', '/certificates/master-3.pdf', NOW(), NOW()),
('cert_003', 'emp_007', 'PHD', 'Doctor of Philosophy in Education', 'University of Dodoma', '2018-06-30', '/certificates/phd-7.pdf', NOW(), NOW()),
('cert_004', 'emp_008', 'MASTER', 'Master of Arts in Education', 'Sokoine University of Agriculture', '2012-09-15', '/certificates/master-8.pdf', NOW(), NOW());

-- Insert sample SLA trackers
INSERT INTO sla_trackers (
    id, request_id, sla_start_date, due_date, current_stage, is_breached, breach_date,
    escalation_level, escalated_to_id, created_at, updated_at
) VALUES
('sla_001', 'req_001', '2025-01-15', '2025-02-14', 'PENDING_REVIEW', false, NULL, 0, NULL, NOW(), NOW()),
('sla_002', 'req_002', '2025-01-10', '2025-02-09', 'COMPLETED', false, NULL, 0, NULL, NOW(), NOW()),
('sla_003', 'req_003', '2025-01-12', '2025-02-11', 'IN_PROGRESS', false, NULL, 1, 'cmd06nnbb000be67wwgil78yv', NOW(), NOW());

-- Insert sample request workflow
INSERT INTO request_workflow (
    id, request_id, stage, assignee_id, status, assigned_date, completed_date, comments,
    created_at, updated_at
) VALUES
('wf_001', 'req_001', 'HRO_REVIEW', 'cmd06nnbn000le67wtg41s3su', 'COMPLETED', '2025-01-15', '2025-01-16', 'Initial review completed successfully', NOW(), NOW()),
('wf_002', 'req_001', 'HRMO_REVIEW', 'cmd06nnbd000de67wb6e6ild5', 'IN_PROGRESS', '2025-01-16', NULL, 'Under review by HRMO', NOW(), NOW()),
('wf_003', 'req_002', 'FINAL_APPROVAL', 'cmd06nnbb000be67wwgil78yv', 'COMPLETED', '2025-01-10', '2025-01-15', 'Approved by HHRMD', NOW(), NOW());

-- Commit transaction
COMMIT;

-- Display summary
SELECT 'Data population completed successfully!' AS message;
SELECT 'Summary of inserted records:' AS summary;
SELECT COUNT(*) as institutions FROM institutions;
SELECT COUNT(*) as employees FROM employees;
SELECT COUNT(*) as users FROM users;
SELECT COUNT(*) as requests FROM requests;
SELECT COUNT(*) as complaints FROM complaints;
SELECT COUNT(*) as audit_logs FROM audit_logs;