-- Test database connection and user authentication
-- Run this to verify the database is ready for Spring Boot

\c prizma;

-- Test 1: Check table exists
SELECT 'TEST 1: Table exists' as test;
SELECT EXISTS (
    SELECT FROM information_schema.tables 
    WHERE table_schema = 'public' 
    AND table_name = 'User'
) as user_table_exists;

-- Test 2: Check sample user data
SELECT 'TEST 2: Sample user data' as test;
SELECT 
    username,
    role,
    active,
    LEFT(password, 7) as password_hash_prefix
FROM "User" 
WHERE username IN ('hro_commission', 'ahmedm', 'skhamis')
ORDER BY username;

-- Test 3: Verify password hashing format
SELECT 'TEST 3: Password format verification' as test;
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN password LIKE '$2a$10$%' THEN 1 END) as bcrypt_hashed_passwords,
    COUNT(CASE WHEN active = true THEN 1 END) as active_users
FROM "User";

-- Test 4: Check institution relationships
SELECT 'TEST 4: Institution relationships' as test;
SELECT 
    u.username,
    u.role,
    i.name as institution_name
FROM "User" u
JOIN "Institution" i ON u."institutionId" = i.id
WHERE u.username IN ('hro_commission', 'ahmedm')
LIMIT 2;

-- Test 5: Check for required fields
SELECT 'TEST 5: Required fields check' as test;
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN name IS NOT NULL AND name != '' THEN 1 END) as users_with_name,
    COUNT(CASE WHEN username IS NOT NULL AND username != '' THEN 1 END) as users_with_username,
    COUNT(CASE WHEN role IS NOT NULL AND role != '' THEN 1 END) as users_with_role
FROM "User";

-- Test 6: Simulate Spring Boot login query
SELECT 'TEST 6: Spring Boot login simulation' as test;
SELECT 
    id,
    name,
    username,
    role,
    active,
    "employeeId",
    "institutionId"
FROM "User"
WHERE username = 'hro_commission' AND active = true;

\echo 'Database connection tests completed!'
\echo 'If all tests show expected results, Spring Boot should connect successfully.'