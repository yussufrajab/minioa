-- Populate OFISI YA RAIS, FEDHA NA MIPANGO with Employees in Prisma database
-- 13 employees in probation (not confirmed)
-- 10 employees confirmed
-- All with unique ZanIDs, ZSSF numbers, and payroll numbers

BEGIN;

-- Insert 23 employees for OFISI YA RAIS, FEDHA NA MIPANGO into Prisma schema
INSERT INTO "Employee" (
    id, name, gender, "profileImageUrl", "dateOfBirth", "placeOfBirth", region, "countryOfBirth",
    "zanId", "phoneNumber", "contactAddress", "zssfNumber", "payrollNumber", cadre, "salaryScale",
    ministry, department, "appointmentType", "contractType", "recentTitleDate", 
    "currentReportingOffice", "currentWorkplace", "employmentDate", "confirmationDate", 
    "retirementDate", status, "institutionId"
) VALUES
-- 10 Confirmed Employees
('ofisi_emp_001', 'Hamza Khamis Maalim', 'Male', 'https://placehold.co/150x150.png?text=HKM', 
 '1978-03-15T00:00:00.000Z', 'Stone Town', 'Mjini Magharibi', 'Tanzania',
 '1905780315', '0777-234567', 'S.L.P 1234, Mjini, Zanzibar', 'ZSSF2001', 'PAY2001', 
 'Mkurugenzi', 'ZPS 7.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Mipango', 
 'Permanent', 'Full-time', '2015-04-01T00:00:00.000Z', 'Mkuu wa Kitengo', 'Makao Makuu',
 '2010-01-15T00:00:00.000Z', '2011-01-15T00:00:00.000Z', '2038-03-15T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_002', 'Mariam Hassan Juma', 'Female', 'https://placehold.co/150x150.png?text=MHJ',
 '1980-06-20T00:00:00.000Z', 'Micheweni', 'Kaskazini Pemba', 'Tanzania',
 '1905800620', '0777-345678', 'S.L.P 2345, Micheweni, Pemba', 'ZSSF2002', 'PAY2002', 
 'Afisa Mkuu Fedha', 'ZPS 6.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Fedha', 
 'Permanent', 'Full-time', '2016-02-10T00:00:00.000Z', 'Mkuu wa Idara ya Fedha', 'Makao Makuu',
 '2012-03-01T00:00:00.000Z', '2013-03-01T00:00:00.000Z', '2040-06-20T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_003', 'Salim Abdallah Mgeni', 'Male', 'https://placehold.co/150x150.png?text=SAM',
 '1975-11-08T00:00:00.000Z', 'Wete', 'Kaskazini Pemba', 'Tanzania',
 '1905751108', '0777-456789', 'S.L.P 3456, Wete, Pemba', 'ZSSF2003', 'PAY2003', 
 'Katibu', 'ZPS 8.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Utawala', 
 'Permanent', 'Full-time', '2014-07-15T00:00:00.000Z', 'Katibu Mkuu', 'Makao Makuu',
 '2008-05-20T00:00:00.000Z', '2009-05-20T00:00:00.000Z', '2035-11-08T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_004', 'Fatma Suleiman Kombo', 'Female', 'https://placehold.co/150x150.png?text=FSK',
 '1982-02-14T00:00:00.000Z', 'Kizimkazi', 'Kusini Unguja', 'Tanzania',
 '1905820214', '0777-567890', 'S.L.P 4567, Kizimkazi, Zanzibar', 'ZSSF2004', 'PAY2004', 
 'Afisa Daraja la I', 'ZPS 5.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Bajeti', 
 'Permanent', 'Full-time', '2017-09-01T00:00:00.000Z', 'Mkuu wa Kitengo cha Bajeti', 'Makao Makuu',
 '2014-06-10T00:00:00.000Z', '2015-06-10T00:00:00.000Z', '2042-02-14T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_005', 'Mwinyi Hamad Vuai', 'Male', 'https://placehold.co/150x150.png?text=MHV',
 '1977-09-03T00:00:00.000Z', 'Bububu', 'Mjini Magharibi', 'Tanzania',
 '1905770903', '0777-678901', 'S.L.P 5678, Bububu, Zanzibar', 'ZSSF2005', 'PAY2005', 
 'Mkurugenzi Msaidizi', 'ZPS 6.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Uchumi', 
 'Permanent', 'Full-time', '2013-12-20T00:00:00.000Z', 'Mkurugenzi Msaidizi', 'Makao Makuu',
 '2007-10-01T00:00:00.000Z', '2008-10-01T00:00:00.000Z', '2037-09-03T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_006', 'Zubeda Mohammed Khamis', 'Female', 'https://placehold.co/150x150.png?text=ZMK',
 '1985-12-25T00:00:00.000Z', 'Chake Chake', 'Kaskazini Pemba', 'Tanzania',
 '1905851225', '0777-789012', 'S.L.P 6789, Chake Chake, Pemba', 'ZSSF2006', 'PAY2006', 
 'Afisa Mipango', 'ZPS 4.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Mipango ya Maendeleo', 
 'Permanent', 'Full-time', '2018-03-15T00:00:00.000Z', 'Mratibu wa Miradi', 'Makao Makuu',
 '2015-01-20T00:00:00.000Z', '2016-01-20T00:00:00.000Z', '2045-12-25T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_007', 'Khalid Juma Ali', 'Male', 'https://placehold.co/150x150.png?text=KJA',
 '1979-04-18T00:00:00.000Z', 'Mkokotoni', 'Kaskazini Unguja', 'Tanzania',
 '1905790418', '0777-890123', 'S.L.P 7890, Mkokotoni, Zanzibar', 'ZSSF2007', 'PAY2007', 
 'Mkaguzi Mkuu', 'ZPS 5.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Ukaguzi', 
 'Permanent', 'Full-time', '2016-11-05T00:00:00.000Z', 'Mkaguzi Mkuu wa Ndani', 'Makao Makuu',
 '2011-08-15T00:00:00.000Z', '2012-08-15T00:00:00.000Z', '2039-04-18T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_008', 'Asha Othman Shaaban', 'Female', 'https://placehold.co/150x150.png?text=AOS',
 '1983-07-22T00:00:00.000Z', 'Mkoani', 'Kaskazini Pemba', 'Tanzania',
 '1905830722', '0777-901234', 'S.L.P 8901, Mkoani, Pemba', 'ZSSF2008', 'PAY2008', 
 'Afisa Uhalifu', 'ZPS 4.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Huduma za Wafanyakazi', 
 'Permanent', 'Full-time', '2017-05-10T00:00:00.000Z', 'Mkuu wa Huduma za Wafanyakazi', 'Makao Makuu',
 '2013-04-01T00:00:00.000Z', '2014-04-01T00:00:00.000Z', '2043-07-22T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_009', 'Rashid Mfaume Haji', 'Male', 'https://placehold.co/150x150.png?text=RMH',
 '1976-01-10T00:00:00.000Z', 'Nungwi', 'Kaskazini Unguja', 'Tanzania',
 '1905760110', '0777-012345', 'S.L.P 9012, Nungwi, Zanzibar', 'ZSSF2009', 'PAY2009', 
 'Mkuu wa Kitengo', 'ZPS 5.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Teknolojia ya Habari', 
 'Permanent', 'Full-time', '2015-08-25T00:00:00.000Z', 'Mkuu wa TEHAMA', 'Makao Makuu',
 '2009-07-10T00:00:00.000Z', '2010-07-10T00:00:00.000Z', '2036-01-10T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_010', 'Saada Khamis Hamad', 'Female', 'https://placehold.co/150x150.png?text=SKH',
 '1981-05-05T00:00:00.000Z', 'Makunduchi', 'Kusini Unguja', 'Tanzania',
 '1905810505', '0777-123456', 'S.L.P 1012, Makunduchi, Zanzibar', 'ZSSF2010', 'PAY2010', 
 'Afisa Takwimu', 'ZPS 4.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Takwimu na Utafiti', 
 'Permanent', 'Full-time', '2016-06-30T00:00:00.000Z', 'Mkuu wa Kitengo cha Takwimu', 'Makao Makuu',
 '2012-05-15T00:00:00.000Z', '2013-05-15T00:00:00.000Z', '2041-05-05T00:00:00.000Z', 
 'Confirmed', 'cmd06nn7n0001e67w2h5rf86x'),

-- 13 Employees in Probation (not confirmed)
('ofisi_emp_011', 'Ibrahim Hassan Mohammed', 'Male', 'https://placehold.co/150x150.png?text=IHM',
 '1990-08-12T00:00:00.000Z', 'Paje', 'Kusini Unguja', 'Tanzania',
 '1905900812', '0777-234567', 'S.L.P 2011, Paje, Zanzibar', 'ZSSF2011', 'PAY2011', 
 'Afisa Daraja la II', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Fedha', 
 'Permanent', 'Full-time', '2024-01-10T00:00:00.000Z', 'Afisa Fedha', 'Makao Makuu',
 '2024-01-10T00:00:00.000Z', NULL, '2050-08-12T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_012', 'Rahma Ali Juma', 'Female', 'https://placehold.co/150x150.png?text=RAJ',
 '1992-03-25T00:00:00.000Z', 'Mwera', 'Kaskazini Pemba', 'Tanzania',
 '1905920325', '0777-345678', 'S.L.P 2012, Mwera, Pemba', 'ZSSF2012', 'PAY2012', 
 'Afisa Bajeti', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Bajeti', 
 'Permanent', 'Full-time', '2023-09-15T00:00:00.000Z', 'Afisa Bajeti', 'Makao Makuu',
 '2023-09-15T00:00:00.000Z', NULL, '2052-03-25T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_013', 'Yusuf Suleiman Makame', 'Male', 'https://placehold.co/150x150.png?text=YSM',
 '1988-11-30T00:00:00.000Z', 'Tunguu', 'Mjini Magharibi', 'Tanzania',
 '1905881130', '0777-456789', 'S.L.P 2013, Tunguu, Zanzibar', 'ZSSF2013', 'PAY2013', 
 'Afisa Mipango', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Mipango', 
 'Permanent', 'Full-time', '2023-07-01T00:00:00.000Z', 'Afisa Mipango', 'Makao Makuu',
 '2023-07-01T00:00:00.000Z', NULL, '2048-11-30T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_014', 'Khadija Mwinyi Vuai', 'Female', 'https://placehold.co/150x150.png?text=KMV',
 '1993-06-16T00:00:00.000Z', 'Vitongoji', 'Kaskazini Pemba', 'Tanzania',
 '1905930616', '0777-567890', 'S.L.P 2014, Vitongoji, Pemba', 'ZSSF2014', 'PAY2014', 
 'Afisa Utawala', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Utawala', 
 'Permanent', 'Full-time', '2024-02-20T00:00:00.000Z', 'Afisa Utawala', 'Makao Makuu',
 '2024-02-20T00:00:00.000Z', NULL, '2053-06-16T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_015', 'Juma Khamis Makame', 'Male', 'https://placehold.co/150x150.png?text=JKM',
 '1991-01-28T00:00:00.000Z', 'Donge', 'Mjini Magharibi', 'Tanzania',
 '1905910128', '0777-678901', 'S.L.P 2015, Donge, Zanzibar', 'ZSSF2015', 'PAY2015', 
 'Afisa TEHAMA', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Teknolojia ya Habari', 
 'Permanent', 'Full-time', '2023-11-05T00:00:00.000Z', 'Afisa TEHAMA', 'Makao Makuu',
 '2023-11-05T00:00:00.000Z', NULL, '2051-01-28T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_016', 'Zainab Hamad Ali', 'Female', 'https://placehold.co/150x150.png?text=ZHA',
 '1989-09-14T00:00:00.000Z', 'Kojani', 'Kaskazini Pemba', 'Tanzania',
 '1905890914', '0777-789012', 'S.L.P 2016, Kojani, Pemba', 'ZSSF2016', 'PAY2016', 
 'Karani', 'ZPS 2.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Huduma za Wafanyakazi', 
 'Permanent', 'Full-time', '2023-06-12T00:00:00.000Z', 'Karani', 'Makao Makuu',
 '2023-06-12T00:00:00.000Z', NULL, '2049-09-14T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_017', 'Hamis Mohammed Juma', 'Male', 'https://placehold.co/150x150.png?text=HMJ',
 '1994-04-07T00:00:00.000Z', 'Jambiani', 'Kusini Unguja', 'Tanzania',
 '1905940407', '0777-890123', 'S.L.P 2017, Jambiani, Zanzibar', 'ZSSF2017', 'PAY2017', 
 'Afisa Ukaguzi', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Ukaguzi', 
 'Permanent', 'Full-time', '2024-03-15T00:00:00.000Z', 'Afisa Ukaguzi', 'Makao Makuu',
 '2024-03-15T00:00:00.000Z', NULL, '2054-04-07T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_018', 'Maryam Othman Khamis', 'Female', 'https://placehold.co/150x150.png?text=MOK',
 '1990-12-02T00:00:00.000Z', 'Kiwengwa', 'Kaskazini Unguja', 'Tanzania',
 '1905901202', '0777-901234', 'S.L.P 2018, Kiwengwa, Zanzibar', 'ZSSF2018', 'PAY2018', 
 'Afisa Takwimu', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Takwimu na Utafiti', 
 'Permanent', 'Full-time', '2023-08-20T00:00:00.000Z', 'Afisa Takwimu', 'Makao Makuu',
 '2023-08-20T00:00:00.000Z', NULL, '2050-12-02T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_019', 'Ali Hassan Haji', 'Male', 'https://placehold.co/150x150.png?text=AHH',
 '1987-07-19T00:00:00.000Z', 'Gando', 'Kaskazini Pemba', 'Tanzania',
 '1905870719', '0777-012345', 'S.L.P 2019, Gando, Pemba', 'ZSSF2019', 'PAY2019', 
 'Afisa Uchumi', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Uchumi', 
 'Permanent', 'Full-time', '2023-05-25T00:00:00.000Z', 'Afisa Uchumi', 'Makao Makuu',
 '2023-05-25T00:00:00.000Z', NULL, '2047-07-19T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_020', 'Salma Juma Mwinyi', 'Female', 'https://placehold.co/150x150.png?text=SJM',
 '1995-02-11T00:00:00.000Z', 'Matemwe', 'Kaskazini Unguja', 'Tanzania',
 '1905950211', '0777-112345', 'S.L.P 2020, Matemwe, Zanzibar', 'ZSSF2020', 'PAY2020', 
 'Afisa Uhalifu', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Huduma za Wafanyakazi', 
 'Permanent', 'Full-time', '2024-04-10T00:00:00.000Z', 'Afisa Uhalifu', 'Makao Makuu',
 '2024-04-10T00:00:00.000Z', NULL, '2055-02-11T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_021', 'Abdallah Khamis Said', 'Male', 'https://placehold.co/150x150.png?text=AKS',
 '1986-10-23T00:00:00.000Z', 'Kangani', 'Kaskazini Pemba', 'Tanzania',
 '1905861023', '0777-223456', 'S.L.P 2021, Kangani, Pemba', 'ZSSF2021', 'PAY2021', 
 'Afisa Miradi', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Mipango ya Maendeleo', 
 'Permanent', 'Full-time', '2023-10-30T00:00:00.000Z', 'Afisa Miradi', 'Makao Makuu',
 '2023-10-30T00:00:00.000Z', NULL, '2046-10-23T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_022', 'Halima Suleiman Vuai', 'Female', 'https://placehold.co/150x150.png?text=HSV',
 '1993-08-05T00:00:00.000Z', 'Konde', 'Kusini Unguja', 'Tanzania',
 '1905930805', '0777-334567', 'S.L.P 2022, Konde, Zanzibar', 'ZSSF2022', 'PAY2022', 
 'Karani Mkuu', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Utawala', 
 'Permanent', 'Full-time', '2024-01-25T00:00:00.000Z', 'Karani Mkuu', 'Makao Makuu',
 '2024-01-25T00:00:00.000Z', NULL, '2053-08-05T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x'),

('ofisi_emp_023', 'Masoud Ali Mohammed', 'Male', 'https://placehold.co/150x150.png?text=MAM',
 '1991-05-17T00:00:00.000Z', 'Mchangani', 'Kaskazini Unguja', 'Tanzania',
 '1905910517', '0777-445678', 'S.L.P 2023, Mchangani, Zanzibar', 'ZSSF2023', 'PAY2023', 
 'Afisa Rasilimali Watu', 'ZPS 3.1', 'OFISI YA RAIS, FEDHA NA MIPANGO', 'Huduma za Wafanyakazi', 
 'Permanent', 'Full-time', '2023-12-15T00:00:00.000Z', 'Afisa Rasilimali Watu', 'Makao Makuu',
 '2023-12-15T00:00:00.000Z', NULL, '2051-05-17T00:00:00.000Z', 
 'Active', 'cmd06nn7n0001e67w2h5rf86x');

COMMIT;

-- Verification queries
SELECT 
    'Total Employees' as metric, 
    COUNT(*) as count 
FROM "Employee" 
WHERE "institutionId" = 'cmd06nn7n0001e67w2h5rf86x'

UNION ALL

SELECT 
    'Confirmed Employees' as metric, 
    COUNT(*) as count 
FROM "Employee" 
WHERE "institutionId" = 'cmd06nn7n0001e67w2h5rf86x' 
    AND "confirmationDate" IS NOT NULL

UNION ALL

SELECT 
    'Probation Employees' as metric, 
    COUNT(*) as count 
FROM "Employee" 
WHERE "institutionId" = 'cmd06nn7n0001e67w2h5rf86x' 
    AND "confirmationDate" IS NULL;