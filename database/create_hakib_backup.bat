@echo off
echo Creating CSMS database backup to 'hakib' file...
echo ==============================================
echo.

REM Set PostgreSQL password
set PGPASSWORD=Mamlaka2020

REM Change to database directory
cd /d C:\hamisho\project_csms\database

REM Create the backup
echo Running backup command...
pg_dump -U postgres -h localhost -p 5432 prizma > hakib

REM Check if backup was created
if exist hakib (
    echo.
    echo SUCCESS: Backup created successfully!
    echo File location: C:\hamisho\project_csms\database\hakib
    
    REM Get file size
    for %%A in (hakib) do echo File size: %%~zA bytes
    
    echo.
    echo To restore from this backup later, use:
    echo psql -U postgres -d prizma -f hakib
) else (
    echo.
    echo ERROR: Backup creation failed!
    echo Please check:
    echo 1. PostgreSQL is running
    echo 2. Password is correct (Mamlaka2020)
    echo 3. Database 'prizma' exists
)

echo.
pause