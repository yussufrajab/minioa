-- CSMS Database Verification Script
-- Run this after restoring database to verify data integrity

-- Check if all tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Verify main data counts
SELECT 
  'Institutions' as entity, COUNT(*) as count 
FROM "Institution"
UNION ALL
SELECT 
  'Users' as entity, COUNT(*) as count 
FROM "User"
UNION ALL
SELECT 
  'Employees' as entity, COUNT(*) as count 
FROM "Employee"
UNION ALL
SELECT 
  'Employee Certificates' as entity, COUNT(*) as count 
FROM "EmployeeCertificate"
UNION ALL
SELECT 
  'Confirmation Requests' as entity, COUNT(*) as count 
FROM "ConfirmationRequest"
UNION ALL
SELECT 
  'Promotion Requests' as entity, COUNT(*) as count 
FROM "PromotionRequest"
UNION ALL
SELECT 
  'LWOP Requests' as entity, COUNT(*) as count 
FROM "LwopRequest"
UNION ALL
SELECT 
  'Cadre Change Requests' as entity, COUNT(*) as count 
FROM "CadreChangeRequest"
UNION ALL
SELECT 
  'Retirement Requests' as entity, COUNT(*) as count 
FROM "RetirementRequest"
UNION ALL
SELECT 
  'Resignation Requests' as entity, COUNT(*) as count 
FROM "ResignationRequest"
UNION ALL
SELECT 
  'Service Extension Requests' as entity, COUNT(*) as count 
FROM "ServiceExtensionRequest"
UNION ALL
SELECT 
  'Separation Requests' as entity, COUNT(*) as count 
FROM "SeparationRequest"
UNION ALL
SELECT 
  'Complaints' as entity, COUNT(*) as count 
FROM "Complaint"
UNION ALL
SELECT 
  'Notifications' as entity, COUNT(*) as count 
FROM "Notification"
ORDER BY entity;

-- Verify foreign key relationships
SELECT 
  'Users with valid institutions' as check_type,
  COUNT(*) as count
FROM "User" u
JOIN "Institution" i ON u."institutionId" = i.id

UNION ALL

SELECT 
  'Employees with valid institutions' as check_type,
  COUNT(*) as count
FROM "Employee" e
JOIN "Institution" i ON e."institutionId" = i.id

UNION ALL

SELECT 
  'Users with linked employees' as check_type,
  COUNT(*) as count
FROM "User" u
JOIN "Employee" e ON u."employeeId" = e.id

UNION ALL

SELECT 
  'Confirmation requests with valid employees' as check_type,
  COUNT(*) as count
FROM "ConfirmationRequest" cr
JOIN "Employee" e ON cr."employeeId" = e.id;

-- Check for admin user
SELECT 
  'Admin user exists' as check_type,
  CASE WHEN COUNT(*) > 0 THEN 'YES' ELSE 'NO' END as result
FROM "User" 
WHERE role = 'ADMIN';

-- Check user role distribution
SELECT 
  'User Roles' as category,
  role,
  COUNT(*) as count
FROM "User"
GROUP BY role
ORDER BY count DESC;

-- Check employee status distribution  
SELECT 
  'Employee Status' as category,
  COALESCE(status, 'NULL') as status,
  COUNT(*) as count
FROM "Employee"
GROUP BY status
ORDER BY count DESC;

-- Check recent activity (last 30 days)
SELECT 
  'Recent Activities' as category,
  'Confirmation Requests' as type,
  COUNT(*) as count
FROM "ConfirmationRequest"
WHERE "createdAt" > CURRENT_DATE - INTERVAL '30 days'

UNION ALL

SELECT 
  'Recent Activities' as category,
  'Promotion Requests' as type,
  COUNT(*) as count
FROM "PromotionRequest"
WHERE "createdAt" > CURRENT_DATE - INTERVAL '30 days'

UNION ALL

SELECT 
  'Recent Activities' as category,
  'LWOP Requests' as type,
  COUNT(*) as count
FROM "LwopRequest"
WHERE "createdAt" > CURRENT_DATE - INTERVAL '30 days'

UNION ALL

SELECT 
  'Recent Activities' as category,
  'Complaints' as type,
  COUNT(*) as count
FROM "Complaint"
WHERE "createdAt" > CURRENT_DATE - INTERVAL '30 days';

-- Verify database indexes exist
SELECT 
  'Database Indexes' as info,
  indexname,
  tablename
FROM pg_indexes 
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- Check for any orphaned records (should return 0 for each)
SELECT 'Orphaned Users (invalid institution)' as issue, COUNT(*)
FROM "User" u 
LEFT JOIN "Institution" i ON u."institutionId" = i.id
WHERE i.id IS NULL

UNION ALL

SELECT 'Orphaned Employees (invalid institution)' as issue, COUNT(*)
FROM "Employee" e
LEFT JOIN "Institution" i ON e."institutionId" = i.id  
WHERE i.id IS NULL

UNION ALL

SELECT 'Orphaned Confirmation Requests (invalid employee)' as issue, COUNT(*)
FROM "ConfirmationRequest" cr
LEFT JOIN "Employee" e ON cr."employeeId" = e.id
WHERE e.id IS NULL;

-- Sample data check - verify some known test records
SELECT 
  'Sample Check' as type,
  'Test user admin exists' as description,
  CASE WHEN COUNT(*) > 0 THEN 'PASS' ELSE 'FAIL' END as status
FROM "User" 
WHERE username = 'admin'

UNION ALL

SELECT 
  'Sample Check' as type,
  'Test institutions exist' as description,  
  CASE WHEN COUNT(*) >= 40 THEN 'PASS' ELSE 'FAIL' END as status
FROM "Institution"

UNION ALL

SELECT 
  'Sample Check' as type,
  'Test employees exist' as description,
  CASE WHEN COUNT(*) >= 150 THEN 'PASS' ELSE 'FAIL' END as status  
FROM "Employee";

-- Database version and settings
SELECT 
  'Database Info' as type,
  'PostgreSQL Version' as setting,
  version() as value

UNION ALL

SELECT 
  'Database Info' as type,
  'Current Database' as setting,
  current_database() as value

UNION ALL

SELECT 
  'Database Info' as type,
  'Current User' as setting,
  current_user as value

UNION ALL

SELECT 
  'Database Info' as type,
  'Timezone' as setting,
  current_setting('timezone') as value;

-- Completion message
SELECT 
  '=== VERIFICATION COMPLETE ===' as status,
  'Check all counts match expected values' as instruction,
  'Ensure no orphaned records found' as reminder;