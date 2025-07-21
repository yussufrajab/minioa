-- Populate OFISI YA RAIS, FEDHA NA MIPANGO with Employees
-- 13 employees in probation (not confirmed)
-- 10 employees confirmed
-- All with unique ZanIDs, ZSSF numbers, and payroll numbers

BEGIN;

-- Use the existing institution ID
-- No need to insert as it already exists

-- Insert 23 employees for OFISI YA RAIS, FEDHA NA MIPANGO
INSERT INTO employees (
    id, full_name, profile_image, date_of_birth, place_of_birth, region, country_of_birth,
    payroll_number, zanzibar_id, zssf_number, rank, ministry, institution_id, department,
    appointment_type, contract_type, recent_title_date, current_reporting_office, current_workplace,
    employment_date, confirmation_date, employment_status, phone_number, gender,
    contact_address, created_at, updated_at
) VALUES
-- 10 Confirmed Employees
('ofisi_emp_001', 'Hamza Khamis Maalim', 'https://placehold.co/150x150.png?text=HKM', 
 '1978-03-15', 'Stone Town', 'Mjini Magharibi', 'Tanzania',
 'PAY2001', '1905780315', 'ZSSF2001', 'Mkurugenzi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Mipango', 'Permanent', 'Full-time',
 '2015-04-01', 'Mkuu wa Kitengo', 'Makao Makuu',
 '2010-01-15', '2011-01-15', 'ACTIVE', '0777-234567', 'Male',
 'S.L.P 1234, Mjini, Zanzibar', NOW(), NOW()),

('ofisi_emp_002', 'Mariam Hassan Juma', 'https://placehold.co/150x150.png?text=MHJ',
 '1980-06-20', 'Micheweni', 'Kaskazini Pemba', 'Tanzania',
 'PAY2002', '1905800620', 'ZSSF2002', 'Afisa Mkuu Fedha', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Fedha', 'Permanent', 'Full-time',
 '2016-02-10', 'Mkuu wa Idara ya Fedha', 'Makao Makuu',
 '2012-03-01', '2013-03-01', '2040-06-20', 'ACTIVE', '0777-345678', 'Female',
 'S.L.P 2345, Micheweni, Pemba', NOW(), NOW()),

('ofisi_emp_003', 'Salim Abdallah Mgeni', 'https://placehold.co/150x150.png?text=SAM',
 '1975-11-08', 'Wete', 'Kaskazini Pemba', 'Tanzania',
 'PAY2003', '1905751108', 'ZSSF2003', 'Katibu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Utawala', 'Permanent', 'Full-time',
 '2014-07-15', 'Katibu Mkuu', 'Makao Makuu',
 '2008-05-20', '2009-05-20', '2035-11-08', 'ACTIVE', '0777-456789', 'Male',
 'S.L.P 3456, Wete, Pemba', NOW(), NOW()),

('ofisi_emp_004', 'Fatma Suleiman Kombo', 'https://placehold.co/150x150.png?text=FSK',
 '1982-02-14', 'Kizimkazi', 'Kusini Unguja', 'Tanzania',
 'PAY2004', '1905820214', 'ZSSF2004', 'Afisa Daraja la I', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Bajeti', 'Permanent', 'Full-time',
 '2017-09-01', 'Mkuu wa Kitengo cha Bajeti', 'Makao Makuu',
 '2014-06-10', '2015-06-10', '2042-02-14', 'ACTIVE', '0777-567890', 'Female',
 'S.L.P 4567, Kizimkazi, Zanzibar', NOW(), NOW()),

('ofisi_emp_005', 'Mwinyi Hamad Vuai', 'https://placehold.co/150x150.png?text=MHV',
 '1977-09-03', 'Bububu', 'Mjini Magharibi', 'Tanzania',
 'PAY2005', '1905770903', 'ZSSF2005', 'Mkurugenzi Msaidizi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Uchumi', 'Permanent', 'Full-time',
 '2013-12-20', 'Mkurugenzi Msaidizi', 'Makao Makuu',
 '2007-10-01', '2008-10-01', '2037-09-03', 'ACTIVE', '0777-678901', 'Male',
 'S.L.P 5678, Bububu, Zanzibar', NOW(), NOW()),

('ofisi_emp_006', 'Zubeda Mohammed Khamis', 'https://placehold.co/150x150.png?text=ZMK',
 '1985-12-25', 'Chake Chake', 'Kaskazini Pemba', 'Tanzania',
 'PAY2006', '1905851225', 'ZSSF2006', 'Afisa Mipango', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Mipango ya Maendeleo', 'Permanent', 'Full-time',
 '2018-03-15', 'Mratibu wa Miradi', 'Makao Makuu',
 '2015-01-20', '2016-01-20', '2045-12-25', 'ACTIVE', '0777-789012', 'Female',
 'S.L.P 6789, Chake Chake, Pemba', NOW(), NOW()),

('ofisi_emp_007', 'Khalid Juma Ali', 'https://placehold.co/150x150.png?text=KJA',
 '1979-04-18', 'Mkokotoni', 'Kaskazini Unguja', 'Tanzania',
 'PAY2007', '1905790418', 'ZSSF2007', 'Mkaguzi Mkuu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Ukaguzi', 'Permanent', 'Full-time',
 '2016-11-05', 'Mkaguzi Mkuu wa Ndani', 'Makao Makuu',
 '2011-08-15', '2012-08-15', '2039-04-18', 'ACTIVE', '0777-890123', 'Male',
 'S.L.P 7890, Mkokotoni, Zanzibar', NOW(), NOW()),

('ofisi_emp_008', 'Asha Othman Shaaban', 'https://placehold.co/150x150.png?text=AOS',
 '1983-07-22', 'Mkoani', 'Kaskazini Pemba', 'Tanzania',
 'PAY2008', '1905830722', 'ZSSF2008', 'Afisa Uhalifu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Huduma za Wafanyakazi', 'Permanent', 'Full-time',
 '2017-05-10', 'Mkuu wa Huduma za Wafanyakazi', 'Makao Makuu',
 '2013-04-01', '2014-04-01', '2043-07-22', 'ACTIVE', '0777-901234', 'Female',
 'S.L.P 8901, Mkoani, Pemba', NOW(), NOW()),

('ofisi_emp_009', 'Rashid Mfaume Haji', 'https://placehold.co/150x150.png?text=RMH',
 '1976-01-10', 'Nungwi', 'Kaskazini Unguja', 'Tanzania',
 'PAY2009', '1905760110', 'ZSSF2009', 'Mkuu wa Kitengo', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Teknolojia ya Habari', 'Permanent', 'Full-time',
 '2015-08-25', 'Mkuu wa TEHAMA', 'Makao Makuu',
 '2009-07-10', '2010-07-10', '2036-01-10', 'ACTIVE', '0777-012345', 'Male',
 'S.L.P 9012, Nungwi, Zanzibar', NOW(), NOW()),

('ofisi_emp_010', 'Saada Khamis Hamad', 'https://placehold.co/150x150.png?text=SKH',
 '1981-05-05', 'Makunduchi', 'Kusini Unguja', 'Tanzania',
 'PAY2010', '1905810505', 'ZSSF2010', 'Afisa Takwimu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Takwimu na Utafiti', 'Permanent', 'Full-time',
 '2016-06-30', 'Mkuu wa Kitengo cha Takwimu', 'Makao Makuu',
 '2012-05-15', '2013-05-15', '2041-05-05', 'ACTIVE', '0777-123456', 'Female',
 'S.L.P 1012, Makunduchi, Zanzibar', NOW(), NOW()),

-- 13 Employees in Probation (not confirmed)
('ofisi_emp_011', 'Ibrahim Hassan Mohammed', 'https://placehold.co/150x150.png?text=IHM',
 '1990-08-12', 'Paje', 'Kusini Unguja', 'Tanzania',
 'PAY2011', '1905900812', 'ZSSF2011', 'Afisa Daraja la II', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Fedha', 'Permanent', 'Full-time',
 '2024-01-10', 'Afisa Fedha', 'Makao Makuu',
 '2024-01-10', NULL, '2050-08-12', 'ACTIVE', '0777-234567', 'Male',
 'S.L.P 2011, Paje, Zanzibar', NOW(), NOW()),

('ofisi_emp_012', 'Rahma Ali Juma', 'https://placehold.co/150x150.png?text=RAJ',
 '1992-03-25', 'Mwera', 'Kaskazini Pemba', 'Tanzania',
 'PAY2012', '1905920325', 'ZSSF2012', 'Afisa Bajeti', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Bajeti', 'Permanent', 'Full-time',
 '2023-09-15', 'Afisa Bajeti', 'Makao Makuu',
 '2023-09-15', NULL, '2052-03-25', 'ACTIVE', '0777-345678', 'Female',
 'S.L.P 2012, Mwera, Pemba', NOW(), NOW()),

('ofisi_emp_013', 'Yusuf Suleiman Makame', 'https://placehold.co/150x150.png?text=YSM',
 '1988-11-30', 'Tunguu', 'Mjini Magharibi', 'Tanzania',
 'PAY2013', '1905881130', 'ZSSF2013', 'Afisa Mipango', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Mipango', 'Permanent', 'Full-time',
 '2023-07-01', 'Afisa Mipango', 'Makao Makuu',
 '2023-07-01', NULL, '2048-11-30', 'ACTIVE', '0777-456789', 'Male',
 'S.L.P 2013, Tunguu, Zanzibar', NOW(), NOW()),

('ofisi_emp_014', 'Khadija Mwinyi Vuai', 'https://placehold.co/150x150.png?text=KMV',
 '1993-06-16', 'Vitongoji', 'Kaskazini Pemba', 'Tanzania',
 'PAY2014', '1905930616', 'ZSSF2014', 'Afisa Utawala', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Utawala', 'Permanent', 'Full-time',
 '2024-02-20', 'Afisa Utawala', 'Makao Makuu',
 '2024-02-20', NULL, '2053-06-16', 'ACTIVE', '0777-567890', 'Female',
 'S.L.P 2014, Vitongoji, Pemba', NOW(), NOW()),

('ofisi_emp_015', 'Juma Khamis Makame', 'https://placehold.co/150x150.png?text=JKM',
 '1991-01-28', 'Donge', 'Mjini Magharibi', 'Tanzania',
 'PAY2015', '1905910128', 'ZSSF2015', 'Afisa TEHAMA', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Teknolojia ya Habari', 'Permanent', 'Full-time',
 '2023-11-05', 'Afisa TEHAMA', 'Makao Makuu',
 '2023-11-05', NULL, '2051-01-28', 'ACTIVE', '0777-678901', 'Male',
 'S.L.P 2015, Donge, Zanzibar', NOW(), NOW()),

('ofisi_emp_016', 'Zainab Hamad Ali', 'https://placehold.co/150x150.png?text=ZHA',
 '1989-09-14', 'Kojani', 'Kaskazini Pemba', 'Tanzania',
 'PAY2016', '1905890914', 'ZSSF2016', 'Karani', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Huduma za Wafanyakazi', 'Permanent', 'Full-time',
 '2023-06-12', 'Karani', 'Makao Makuu',
 '2023-06-12', NULL, '2049-09-14', 'ACTIVE', '0777-789012', 'Female',
 'S.L.P 2016, Kojani, Pemba', NOW(), NOW()),

('ofisi_emp_017', 'Hamis Mohammed Juma', 'https://placehold.co/150x150.png?text=HMJ',
 '1994-04-07', 'Jambiani', 'Kusini Unguja', 'Tanzania',
 'PAY2017', '1905940407', 'ZSSF2017', 'Afisa Ukaguzi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Ukaguzi', 'Permanent', 'Full-time',
 '2024-03-15', 'Afisa Ukaguzi', 'Makao Makuu',
 '2024-03-15', NULL, '2054-04-07', 'ACTIVE', '0777-890123', 'Male',
 'S.L.P 2017, Jambiani, Zanzibar', NOW(), NOW()),

('ofisi_emp_018', 'Maryam Othman Khamis', 'https://placehold.co/150x150.png?text=MOK',
 '1990-12-02', 'Kiwengwa', 'Kaskazini Unguja', 'Tanzania',
 'PAY2018', '1905901202', 'ZSSF2018', 'Afisa Takwimu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Takwimu na Utafiti', 'Permanent', 'Full-time',
 '2023-08-20', 'Afisa Takwimu', 'Makao Makuu',
 '2023-08-20', NULL, '2050-12-02', 'ACTIVE', '0777-901234', 'Female',
 'S.L.P 2018, Kiwengwa, Zanzibar', NOW(), NOW()),

('ofisi_emp_019', 'Ali Hassan Haji', 'https://placehold.co/150x150.png?text=AHH',
 '1987-07-19', 'Gando', 'Kaskazini Pemba', 'Tanzania',
 'PAY2019', '1905870719', 'ZSSF2019', 'Afisa Uchumi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Uchumi', 'Permanent', 'Full-time',
 '2023-05-25', 'Afisa Uchumi', 'Makao Makuu',
 '2023-05-25', NULL, '2047-07-19', 'ACTIVE', '0777-012345', 'Male',
 'S.L.P 2019, Gando, Pemba', NOW(), NOW()),

('ofisi_emp_020', 'Salma Juma Mwinyi', 'https://placehold.co/150x150.png?text=SJM',
 '1995-02-11', 'Matemwe', 'Kaskazini Unguja', 'Tanzania',
 'PAY2020', '1905950211', 'ZSSF2020', 'Afisa Uhalifu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Huduma za Wafanyakazi', 'Permanent', 'Full-time',
 '2024-04-10', 'Afisa Uhalifu', 'Makao Makuu',
 '2024-04-10', NULL, '2055-02-11', 'ACTIVE', '0777-112345', 'Female',
 'S.L.P 2020, Matemwe, Zanzibar', NOW(), NOW()),

('ofisi_emp_021', 'Abdallah Khamis Said', 'https://placehold.co/150x150.png?text=AKS',
 '1986-10-23', 'Kangani', 'Kaskazini Pemba', 'Tanzania',
 'PAY2021', '1905861023', 'ZSSF2021', 'Afisa Miradi', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Mipango ya Maendeleo', 'Permanent', 'Full-time',
 '2023-10-30', 'Afisa Miradi', 'Makao Makuu',
 '2023-10-30', NULL, '2046-10-23', 'ACTIVE', '0777-223456', 'Male',
 'S.L.P 2021, Kangani, Pemba', NOW(), NOW()),

('ofisi_emp_022', 'Halima Suleiman Vuai', 'https://placehold.co/150x150.png?text=HSV',
 '1993-08-05', 'Konde', 'Kusini Unguja', 'Tanzania',
 'PAY2022', '1905930805', 'ZSSF2022', 'Karani Mkuu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Utawala', 'Permanent', 'Full-time',
 '2024-01-25', 'Karani Mkuu', 'Makao Makuu',
 '2024-01-25', NULL, '2053-08-05', 'ACTIVE', '0777-334567', 'Female',
 'S.L.P 2022, Konde, Zanzibar', NOW(), NOW()),

('ofisi_emp_023', 'Masoud Ali Mohammed', 'https://placehold.co/150x150.png?text=MAM',
 '1991-05-17', 'Mchangani', 'Kaskazini Unguja', 'Tanzania',
 'PAY2023', '1905910517', 'ZSSF2023', 'Afisa Rasilimali Watu', 'OFISI YA RAIS, FEDHA NA MIPANGO',
 'inst-004', 'Huduma za Wafanyakazi', 'Permanent', 'Full-time',
 '2023-12-15', 'Afisa Rasilimali Watu', 'Makao Makuu',
 '2023-12-15', NULL, '2051-05-17', 'ACTIVE', '0777-445678', 'Male',
 'S.L.P 2023, Mchangani, Zanzibar', NOW(), NOW());

-- Update employee count for the institution
UPDATE institutions 
SET employee_count = (
    SELECT COUNT(*) 
    FROM employees 
    WHERE institution_id = 'inst-004'
)
WHERE id = 'inst-004';

COMMIT;

-- Verification queries
SELECT 
    'Total Employees' as metric, 
    COUNT(*) as count 
FROM employees 
WHERE institution_id = 'inst-004'

UNION ALL

SELECT 
    'Confirmed Employees' as metric, 
    COUNT(*) as count 
FROM employees 
WHERE institution_id = 'inst-004' 
    AND confirmation_date IS NOT NULL

UNION ALL

SELECT 
    'Probation Employees' as metric, 
    COUNT(*) as count 
FROM employees 
WHERE institution_id = 'inst-004' 
    AND confirmation_date IS NULL;