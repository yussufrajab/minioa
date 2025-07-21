-- Insert sample data for CSMS application
-- This file contains sample data for development and testing

-- Insert sample institutions
INSERT INTO institutions (id, name, email, address, telephone, vote_number, vote_description, created_at, is_active) VALUES
('inst-001', 'KAMISHENI YA UTUMISHI WA UMMA', 'kamisheni.utumishi@zpsc.go.tz', 'MWANAKWEREKWE', '+255242230872', 'E07', 'Civil Service Commission', NOW(), true),
('inst-002', 'WIZARA YA ELIMU NA MAFUNZO YA AMALI', 'info@moez.go.tz', 'MAZIZINI', '+255777458878', 'K01', 'Ministry of Education', NOW(), true),
('inst-003', 'WIZARA YA AFYA', 'info@mohz.go.tz', 'MNAZI MMOJA', '+255242231614', 'H01', 'Ministry of Health', NOW(), true),
('inst-004', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'info@mofzanzibar.go.tz', 'VUGA', '+255 2477666664/5', 'F01', 'President Office Finance and Planning', NOW(), true),
('inst-005', 'WIZARA YA KILIMO UMWAGILIAJI MALIASILI NA MIFUGO', 'ps@kilimoznz.go.tz', 'MARUHUBI', '0777868306', 'L01', 'Ministry of Agriculture', NOW(), true);

-- Insert sample users (password is 'password123' encrypted with BCrypt)
INSERT INTO users (id, username, password, email, full_name, phone_number, role, institution_id, created_at, is_active) VALUES
('user-001', 'admin', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'admin@csms.go.tz', 'System Administrator', '+255777123456', 'ADMIN', 'inst-001', NOW(), true),
('user-002', 'safia.khamis', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'safia.khamis@csms.go.tz', 'Safia Khamis', '+255777234567', 'HHRMD', 'inst-001', NOW(), true),
('user-003', 'khamis.mnyonge', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'khamis.mnyonge@moez.go.tz', 'Khamis Mnyonge', '+255777345678', 'HRO', 'inst-002', NOW(), true),
('user-004', 'fauzia.iddi', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'fauzia.iddi@csms.go.tz', 'Fauzia Iddi', '+255777456789', 'HRMO', 'inst-001', NOW(), true),
('user-005', 'kabdul.rahman', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'kabdul.rahman@csms.go.tz', 'Kabdul Rahman', '+255777567890', 'DO', 'inst-001', NOW(), true),
('user-006', 'mwalimu.hassan', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'mwalimu.hassan@moez.go.tz', 'Mwalimu Hassan', '+255777678901', 'HRRP', 'inst-002', NOW(), true),
('user-007', 'hmohamed', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'hmohamed@moez.go.tz', 'Hassan Mohamed', '+255777789012', 'EMP', 'inst-002', NOW(), true),
('user-008', 'planning.officer', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'planning@csms.go.tz', 'Planning Officer', '+255777890123', 'PO', 'inst-001', NOW(), true),
('user-009', 'secretary.cscs', '$2a$12$WXFLcyTnvvFnzXPsVDLpJO9fJoHXqT8.2oqcQoJ4YSfGGSgvzpKsW', 'secretary@csms.go.tz', 'CSCS Secretary', '+255777901234', 'CSCS', 'inst-001', NOW(), true);

-- Insert sample employees
INSERT INTO employees (id, full_name, date_of_birth, place_of_birth, region, country_of_birth, payroll_number, zanzibar_id, zssf_number, rank, ministry, institution_id, department, appointment_type, contract_type, employment_date, employment_status, phone_number, gender, contact_address, created_at, is_active) VALUES
('emp-001', 'Ali Juma Ali', '1990-05-15', 'Stone Town', 'Zanzibar Urban', 'Tanzania', 'PAY001', '221458232', 'ZSSF001', 'Afisa Msaidizi', 'Ministry of Education', 'inst-002', 'Human Resources', 'Permanent', 'Full-time', '2023-09-10', 'UNCONFIRMED', '+255777111222', 'Male', 'Mwanakwerekwe, Zanzibar', NOW(), true),
('emp-002', 'Safia Juma Ali', '1988-08-20', 'Pemba', 'Pemba North', 'Tanzania', 'PAY002', '125468957', 'ZSSF002', 'Afisa Msaidizi', 'Ministry of Education', 'inst-002', 'Finance', 'Permanent', 'Full-time', '2020-03-15', 'CONFIRMED', '+255777222333', 'Female', 'Mkoani, Pemba', NOW(), true),
('emp-003', 'Fatma Said Omar', '1992-12-10', 'Micheweni', 'Pemba North', 'Tanzania', 'PAY003', '334589123', 'ZSSF003', 'Afisa Tehama', 'Ministry of Health', 'inst-003', 'Administration', 'Permanent', 'Full-time', '2021-07-01', 'CONFIRMED', '+255777333444', 'Female', 'Micheweni, Pemba', NOW(), true),
('emp-004', 'Hassan Mzee Juma', '1985-03-25', 'Wete', 'Pemba North', 'Tanzania', 'PAY004', '445678234', 'ZSSF004', 'Mhasibu', 'President Office Finance', 'inst-004', 'Finance', 'Permanent', 'Full-time', '2019-01-20', 'CONFIRMED', '+255777444555', 'Male', 'Wete, Pemba', NOW(), true),
('emp-005', 'Zainab Ali Khamis', '1965-11-30', 'Chake Chake', 'Pemba South', 'Tanzania', 'PAY005', '556789345', 'ZSSF005', 'Afisa Kilimo', 'Ministry of Agriculture', 'inst-005', 'Extension Services', 'Permanent', 'Full-time', '1990-05-10', 'CONFIRMED', '+255777555666', 'Female', 'Chake Chake, Pemba', NOW(), true),
('emp-006', 'Juma Omar Ali', '1987-07-18', 'Makasaki', 'Pemba South', 'Tanzania', 'PAY006', '667890456', 'ZSSF006', 'Afisa Msaidizi', 'Ministry of Agriculture', 'inst-005', 'Research', 'Permanent', 'Full-time', '2022-02-14', 'CONFIRMED', '+255777666777', 'Male', 'Makasaki, Pemba', NOW(), true);

-- Insert sample employee documents
INSERT INTO employee_documents (id, employee_id, document_type, file_name, file_path, file_size, content_type, description, created_at, is_active) VALUES
('doc-001', 'emp-001', 'Ardhil-Hali', 'ali_juma_ardhil_hali.pdf', '/uploads/documents/ali_juma_ardhil_hali.pdf', 1024000, 'application/pdf', 'Employee birth certificate', NOW(), true),
('doc-002', 'emp-001', 'Job Contract', 'ali_juma_contract.pdf', '/uploads/documents/ali_juma_contract.pdf', 512000, 'application/pdf', 'Employment contract', NOW(), true),
('doc-003', 'emp-002', 'Confirmation Letter', 'safia_juma_confirmation.pdf', '/uploads/documents/safia_juma_confirmation.pdf', 256000, 'application/pdf', 'Employment confirmation letter', NOW(), true);

-- Insert sample employee certificates
INSERT INTO employee_certificates (id, employee_id, certificate_type, institution_name, field_of_study, date_obtained, certificate_number, file_name, file_path, created_at, is_active) VALUES
('cert-001', 'emp-001', 'Diploma', 'Zanzibar University', 'Business Administration', '2018-06-30', 'ZUNIA/BBA/2018/001', 'ali_juma_diploma.pdf', '/uploads/certificates/ali_juma_diploma.pdf', NOW(), true),
('cert-002', 'emp-002', 'Bachelor Degree', 'University of Dar es Salaam', 'Accounting', '2015-11-15', 'UDSM/BAcc/2015/456', 'safia_juma_degree.pdf', '/uploads/certificates/safia_juma_degree.pdf', NOW(), true),
('cert-003', 'emp-003', 'Master Degree', 'Open University of Tanzania', 'Public Administration', '2020-10-20', 'OUT/MPA/2020/789', 'fatma_said_masters.pdf', '/uploads/certificates/fatma_said_masters.pdf', NOW(), true);

-- Insert sample requests
INSERT INTO requests (id, request_number, employee_id, submitted_by, submission_date, status, request_type, created_at, is_active) VALUES
('req-001', 'CONF/2024/001', 'emp-001', 'user-003', '2024-01-15 10:30:00', 'SUBMITTED', 'CONFIRMATION', NOW(), true),
('req-002', 'PROM/2024/001', 'emp-002', 'user-003', '2024-01-20 14:45:00', 'SUBMITTED', 'PROMOTION', NOW(), true),
('req-003', 'LWOP/2024/001', 'emp-003', 'user-003', '2024-02-01 09:15:00', 'APPROVED', 'LWOP', NOW(), true),
('req-004', 'RET/2024/001', 'emp-005', 'user-003', '2024-02-10 11:20:00', 'SUBMITTED', 'RETIREMENT', NOW(), true);

-- Insert sample confirmation requests
-- INSERT INTO confirmation_requests (id, probation_period_status, probation_end_date) VALUES
-- ('req-001', '12-18 months', '2024-09-10');

-- Insert sample promotion requests
-- INSERT INTO promotion_requests (id, promotion_type, current_rank, proposed_rank, education_completion_date, years_in_current_position) VALUES
-- ('req-002', 'Educational', 'Afisa Msaidizi', 'Afisa Tehama', '2015-11-15', 4);

-- Insert sample LWOP requests
-- INSERT INTO leave_without_pay_requests (id, reason, leave_start_date, leave_end_date, has_loan_guarantee) VALUES
-- ('req-003', 'Personal development - Further studies', '2024-06-01', '2024-12-01', false);

-- Insert sample complaints
INSERT INTO complaints (id, complaint_number, employee_id, complaint_type, complainant_name, respondent_name, complaint_source, incident_date, description, status, created_at, is_active) VALUES
('comp-001', 'COMP/2024/001', 'emp-001', 'Unconfirmed Employee', 'Ali Juma Ali', 'HR Department', 'Employee', '2024-01-10', 'Delayed confirmation process despite completing probation period', 'SUBMITTED', NOW(), true),
('comp-002', 'COMP/2024/002', 'emp-003', 'Job-Related', 'Fatma Said Omar', 'Department Head', 'Employee', '2024-01-25', 'Workplace harassment complaint', 'SUBMITTED', NOW(), true);

-- Insert sample audit logs
INSERT INTO audit_logs (id, user_id, username, action, entity_type, entity_id, timestamp, success) VALUES
('log-001', 'user-001', 'admin', 'LOGIN', 'User', 'user-001', NOW(), true),
('log-002', 'user-003', 'khamis.mnyonge', 'LOGIN', 'User', 'user-003', NOW(), true),
('log-003', 'user-003', 'khamis.mnyonge', 'CREATE_REQUEST', 'Request', 'req-001', NOW(), true),
('log-004', 'user-002', 'safia.khamis', 'LOGIN', 'User', 'user-002', NOW(), true),
('log-005', 'user-007', 'hmohamed', 'LOGIN', 'User', 'user-007', NOW(), true),
('log-006', 'user-007', 'hmohamed', 'CREATE_COMPLAINT', 'Complaint', 'comp-001', NOW(), true);