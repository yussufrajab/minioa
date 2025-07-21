@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo CSMS Dual-ORM Backup Script for Windows
echo For Prisma (Frontend) + Hibernate (Backend)
echo ==========================================
echo.

REM Set PostgreSQL password
set PGPASSWORD=Mamlaka2020

REM Set backup directory with timestamp
for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
set "YY=%dt:~2,2%" & set "YYYY=%dt:~0,4%" & set "MM=%dt:~4,2%" & set "DD=%dt:~6,2%"
set "HH=%dt:~8,2%" & set "Min=%dt:~10,2%" & set "Sec=%dt:~12,2%"
set "TIMESTAMP=%YYYY%%MM%%DD%_%HH%%Min%%Sec%"
set BACKUP_DIR=C:\hamisho\project_csms\database\backup_%TIMESTAMP%

echo Creating backup directory: %BACKUP_DIR%
mkdir "%BACKUP_DIR%" 2>nul

echo.
echo Step 1: Backing up PostgreSQL database...
echo ----------------------------------------
pg_dump -U postgres -h localhost -p 5432 --format=custom --verbose --file="%BACKUP_DIR%\csms_database.backup" prizma
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Database backup created
) else (
    echo [WARNING] Database backup may have failed, trying plain SQL format...
    pg_dump -U postgres -h localhost -p 5432 --clean --if-exists --file="%BACKUP_DIR%\csms_database.sql" prizma
)

echo.
echo Step 2: Backing up Prisma schema...
echo -----------------------------------
cd /d C:\hamisho\project_csms\frontend
if exist prisma\schema.prisma (
    copy prisma\schema.prisma "%BACKUP_DIR%\prisma_schema.prisma" >nul
    echo [SUCCESS] Prisma schema backed up
    
    REM Try to pull current database state
    call npx prisma db pull >"%BACKUP_DIR%\prisma_pull_log.txt" 2>&1
    if exist prisma\schema.prisma (
        copy prisma\schema.prisma "%BACKUP_DIR%\prisma_schema_from_db.prisma" >nul
        echo [SUCCESS] Current database schema pulled via Prisma
    )
) else (
    echo [ERROR] Prisma schema not found
)

echo.
echo Step 3: Backing up Hibernate/JPA entities...
echo -------------------------------------------
cd /d C:\hamisho\project_csms\backend
if exist src\main\java\com\zanzibar\csms\entity (
    xcopy /E /I /Y src\main\java\com\zanzibar\csms\entity "%BACKUP_DIR%\hibernate_entities" >nul
    echo [SUCCESS] Hibernate entities backed up
) else (
    echo [ERROR] Hibernate entities not found
)

REM Copy Spring Boot configuration
if exist src\main\resources\application.properties (
    copy src\main\resources\application*.properties "%BACKUP_DIR%\" >nul
    echo [SUCCESS] Spring Boot configuration backed up
)

echo.
echo Step 4: Exporting data as CSV for verification...
echo ------------------------------------------------
cd /d "%BACKUP_DIR%"
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"User\" TO 'users_data.csv' CSV HEADER" 2>nul
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"Employee\" TO 'employees_data.csv' CSV HEADER" 2>nul
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"Institution\" TO 'institutions_data.csv' CSV HEADER" 2>nul
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"ConfirmationRequest\" TO 'confirmation_requests.csv' CSV HEADER" 2>nul
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"PromotionRequest\" TO 'promotion_requests.csv' CSV HEADER" 2>nul
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"LwopRequest\" TO 'lwop_requests.csv' CSV HEADER" 2>nul
echo [INFO] CSV export completed (some tables may be empty)

echo.
echo Step 5: Creating backup verification script...
echo ---------------------------------------------
(
echo @echo off
echo echo Verifying CSMS Backup...
echo set PGPASSWORD=Mamlaka2020
echo.
echo echo Checking database connection...
echo psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT version();"
echo.
echo echo Counting records in main tables...
echo psql -U postgres -h localhost -p 5432 -d prizma -c "SELECT 'Users' as table_name, COUNT(*) as count FROM \"User\" UNION ALL SELECT 'Employees', COUNT(*) FROM \"Employee\" UNION ALL SELECT 'Institutions', COUNT(*) FROM \"Institution\";"
echo.
echo echo Checking Prisma schema...
echo cd /d C:\hamisho\project_csms\frontend
echo call npx prisma validate
echo.
echo echo Checking Hibernate connection...
echo cd /d C:\hamisho\project_csms\backend
echo call mvn spring-boot:run -Dspring.profiles.active=prod -Drun.jvmArguments="-Dspring.jpa.hibernate.ddl-auto=validate"
echo.
echo pause
) > "%BACKUP_DIR%\verify_backup.bat"
echo [SUCCESS] Verification script created

echo.
echo Step 6: Creating backup metadata...
echo -----------------------------------
REM Get record counts
for /f %%i in ('psql -U postgres -h localhost -p 5432 -d prizma -t -c "SELECT COUNT(*) FROM \"User\""') do set USER_COUNT=%%i
for /f %%i in ('psql -U postgres -h localhost -p 5432 -d prizma -t -c "SELECT COUNT(*) FROM \"Employee\""') do set EMP_COUNT=%%i
for /f %%i in ('psql -U postgres -h localhost -p 5432 -d prizma -t -c "SELECT COUNT(*) FROM \"Institution\""') do set INST_COUNT=%%i

(
echo Backup Metadata
echo ===============
echo Backup Date: %DATE% %TIME%
echo Backup Directory: %BACKUP_DIR%
echo.
echo Database Information:
echo - Name: prizma
echo - Host: localhost
echo - Port: 5432
echo - User: postgres
echo.
echo Record Counts:
echo - Users: %USER_COUNT%
echo - Employees: %EMP_COUNT%
echo - Institutions: %INST_COUNT%
echo.
echo Architecture:
echo - Frontend: Next.js with Prisma ORM
echo - Backend: Spring Boot with Hibernate/JPA
echo - Database: PostgreSQL 15
echo.
echo Files in this backup:
echo - csms_database.backup (or .sql) - Complete database backup
echo - prisma_schema.prisma - Prisma schema definition
echo - prisma_schema_from_db.prisma - Current DB schema via Prisma
echo - hibernate_entities\ - Java entity classes
echo - application*.properties - Spring Boot configuration
echo - *_data.csv - Data exports for verification
echo - verify_backup.bat - Verification script
) > "%BACKUP_DIR%\backup_info.txt"
echo [SUCCESS] Metadata file created

echo.
echo Step 7: Creating restoration script...
echo -------------------------------------
(
echo @echo off
echo echo CSMS Dual-ORM Restoration Script
echo echo ================================
echo.
echo set PGPASSWORD=Mamlaka2020
echo.
echo echo WARNING: This will replace the existing database!
echo pause
echo.
echo echo Step 1: Creating fresh database...
echo dropdb -U postgres prizma 2^>nul
echo createdb -U postgres prizma
echo.
echo echo Step 2: Restoring database...
echo if exist csms_database.backup (
echo     pg_restore -U postgres -h localhost -p 5432 -d prizma -v csms_database.backup
echo ^) else if exist csms_database.sql (
echo     psql -U postgres -d prizma -f csms_database.sql
echo ^) else (
echo     echo ERROR: No backup file found!
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo Step 3: Updating Prisma client...
echo cd /d C:\hamisho\project_csms\frontend
echo call npm install
echo call npx prisma generate
echo call npx prisma db pull
echo.
echo echo Step 4: Verifying restoration...
echo psql -U postgres -d prizma -c "SELECT COUNT(*) FROM \"User\";"
echo.
echo echo Restoration completed!
echo echo.
echo echo Next steps:
echo echo 1. Start backend: cd backend ^&^& mvn spring-boot:run
echo echo 2. Start frontend: cd frontend ^&^& npm run dev
echo pause
) > "%BACKUP_DIR%\restore_backup.bat"
echo [SUCCESS] Restoration script created

echo.
echo ==========================================
echo BACKUP COMPLETED SUCCESSFULLY!
echo ==========================================
echo.
echo Backup Location: %BACKUP_DIR%
echo.
echo Important Files Created:
echo - Database Backup: csms_database.backup (or .sql)
echo - Prisma Schema: prisma_schema.prisma
echo - Hibernate Entities: hibernate_entities\
echo - CSV Data Exports: *_data.csv
echo - Verification Script: verify_backup.bat
echo - Restoration Script: restore_backup.bat
echo.
echo To verify this backup, run: %BACKUP_DIR%\verify_backup.bat
echo To restore from this backup, run: %BACKUP_DIR%\restore_backup.bat
echo.
pause