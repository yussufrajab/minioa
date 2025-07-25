@echo off
REM CSMS Database Restoration Script for Windows
REM This script restores the CSMS database on a new VPS
REM Run this as Administrator

echo =========================================
echo CSMS Database Restoration Script
echo =========================================
echo.

REM Check if PostgreSQL is installed and running
echo [1/6] Checking PostgreSQL installation...
pg_config --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL 15 or later first
    pause
    exit /b 1
)

echo PostgreSQL found.
echo.

REM Check if psql is available
echo [2/6] Checking psql command...
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: psql command not found
    echo Please ensure PostgreSQL client tools are installed
    pause
    exit /b 1
)

echo psql command available.
echo.

REM Set database credentials
set DB_NAME=prizma
set DB_USER=postgres
set /p DB_PASSWORD=Enter PostgreSQL password for user 'postgres': 

echo.
echo [3/6] Creating database '%DB_NAME%'...
psql -U %DB_USER% -c "CREATE DATABASE %DB_NAME%;" 2>nul
if %errorlevel% neq 0 (
    echo Database '%DB_NAME%' may already exist. Continuing...
)

echo.
echo [4/6] Creating database schema...
set PGPASSWORD=%DB_PASSWORD%
psql -U %DB_USER% -d %DB_NAME% -f schema-backup.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to create database schema
    pause
    exit /b 1
)

echo Schema created successfully.
echo.

echo [5/6] Importing data...
psql -U %DB_USER% -d %DB_NAME% -f csms-data-export.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to import data
    pause
    exit /b 1
)

echo Data imported successfully.
echo.

echo [6/6] Verifying installation...
psql -U %DB_USER% -d %DB_NAME% -c "SELECT 'Institutions: ' || COUNT(*) FROM \"Institution\";"
psql -U %DB_USER% -d %DB_NAME% -c "SELECT 'Users: ' || COUNT(*) FROM \"User\";"
psql -U %DB_USER% -d %DB_NAME% -c "SELECT 'Employees: ' || COUNT(*) FROM \"Employee\";"

echo.
echo =========================================
echo Database restoration completed successfully!
echo =========================================
echo.
echo Database Details:
echo - Name: %DB_NAME%
echo - Host: localhost
echo - Port: 5432
echo - User: %DB_USER%
echo.
echo Next steps:
echo 1. Update your .env files with the database connection
echo 2. Install Node.js dependencies: npm install
echo 3. Generate Prisma client: npx prisma generate
echo 4. Start your application
echo.
pause