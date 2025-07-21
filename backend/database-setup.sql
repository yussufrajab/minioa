-- PostgreSQL Database Setup for CSMS Three-Tier Architecture
-- This script ensures proper database configuration for Spring Boot backend

-- Connect to the prizma database (same as frontend)
\c prizma;

-- Verify database connection
SELECT current_database(), current_user, version();

-- Check if required tables exist
SELECT 
    schemaname,
    tablename 
FROM pg_tables 
WHERE schemaname = 'public' 
    AND tablename IN ('User', 'Employee', 'Institution', 'ConfirmationRequest', 'PromotionRequest')
ORDER BY tablename;

-- Verify user data exists
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN active = true THEN 1 END) as active_users
FROM "User";

-- Show sample users for testing
SELECT 
    username,
    role,
    active,
    "institutionId"
FROM "User"
WHERE role IN ('ADMIN', 'HRO', 'HHRMD', 'DO')
LIMIT 10;

-- Verify institutions exist
SELECT COUNT(*) as total_institutions FROM "Institution";

-- Verify employees exist  
SELECT COUNT(*) as total_employees FROM "Employee";

-- Check table constraints and indexes
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
    AND tc.table_name IN ('User', 'Employee', 'Institution')
ORDER BY tc.table_name, tc.constraint_type;

-- Show password format (should be BCrypt hashed)
SELECT 
    username,
    LEFT(password, 7) as hash_prefix,
    LENGTH(password) as password_length
FROM "User"
WHERE username IN ('hro_commission', 'ahmedm', 'skhamis')
LIMIT 3;

-- Create indexes for performance if they don't exist
DO $$
BEGIN
    -- Index for username lookup (should already exist as unique constraint)
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_username_active') THEN
        CREATE INDEX idx_user_username_active ON "User"(username, active);
    END IF;
    
    -- Index for employee searches
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_employee_zanid') THEN
        CREATE INDEX idx_employee_zanid ON "Employee"("zanId");
    END IF;
    
    -- Index for request queries
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_confirmation_status') THEN
        CREATE INDEX idx_confirmation_status ON "ConfirmationRequest"(status, "reviewStage");
    END IF;
    
    RAISE NOTICE 'Database indexes verified/created successfully';
END
$$;

-- Verify table relationships
SELECT 
    conname AS constraint_name,
    conrelid::regclass AS table_name,
    confrelid::regclass AS referenced_table
FROM pg_constraint
WHERE contype = 'f'
    AND conrelid::regclass::text IN ('"User"', '"Employee"', '"Institution"')
ORDER BY table_name;

-- Show database statistics
SELECT 
    'Users' as table_name, COUNT(*) as record_count FROM "User"
UNION ALL
SELECT 
    'Employees', COUNT(*) FROM "Employee"
UNION ALL
SELECT 
    'Institutions', COUNT(*) FROM "Institution"
UNION ALL
SELECT 
    'Confirmation Requests', COUNT(*) FROM "ConfirmationRequest"
UNION ALL
SELECT 
    'Promotion Requests', COUNT(*) FROM "PromotionRequest"
ORDER BY table_name;

-- Final verification message
\echo 'Database setup verification completed!'
\echo 'If all checks passed, the Spring Boot backend should be able to connect successfully.'
\echo 'Test credentials available:'
\echo '  Username: hro_commission, Role: HRO'
\echo '  Username: ahmedm, Role: HRO'  
\echo '  Username: skhamis, Role: HHRMD'