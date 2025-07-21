@echo off
cls
echo ========================================
echo    CSMS DATABASE BACKUP TO HAKIB FILE
echo ========================================
echo.
echo This will create a backup of the 'prizma' database
echo and save it to: C:\hamisho\project_csms\database\hakib
echo.
echo Database credentials:
echo - Host: localhost
echo - Port: 5432  
echo - Database: prizma
echo - Username: postgres
echo - Password: Mamlaka2020
echo.
pause

REM Set password
set PGPASSWORD=Mamlaka2020

REM Delete old hakib file if exists
if exist "C:\hamisho\project_csms\database\hakib" (
    echo Removing old hakib file...
    del /f "C:\hamisho\project_csms\database\hakib"
)

echo.
echo Creating backup...
echo.

REM Method 1: Direct pg_dump
echo Method 1: Using pg_dump...
pg_dump -U postgres -h localhost -p 5432 prizma > "C:\hamisho\project_csms\database\hakib"

REM Check if successful
if exist "C:\hamisho\project_csms\database\hakib" (
    for %%A in ("C:\hamisho\project_csms\database\hakib") do (
        if %%~zA GTR 0 (
            echo.
            echo SUCCESS! Backup created successfully.
            echo File: C:\hamisho\project_csms\database\hakib
            echo Size: %%~zA bytes
            goto :end
        )
    )
)

echo.
echo Method 1 failed or created empty file. Trying alternative method...
echo.

REM Method 2: Using custom format
echo Method 2: Using custom format...
pg_dump -U postgres -h localhost -p 5432 -Fc prizma > "C:\hamisho\project_csms\database\hakib.backup"
if exist "C:\hamisho\project_csms\database\hakib.backup" (
    echo Custom format backup created as hakib.backup
    echo To restore: pg_restore -U postgres -d prizma hakib.backup
    goto :end
)

echo.
echo Method 2 failed. Trying SQL export...
echo.

REM Method 3: Export essential data only
echo Method 3: Exporting essential tables...
(
echo -- CSMS Essential Data Backup
echo -- Database: prizma
echo -- Created: %DATE% %TIME%
echo.
echo SET client_encoding = 'UTF8';
echo.
) > "C:\hamisho\project_csms\database\hakib"

psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"Institution\" TO STDOUT WITH CSV HEADER" >> "C:\hamisho\project_csms\database\hakib"
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"User\" TO STDOUT WITH CSV HEADER" >> "C:\hamisho\project_csms\database\hakib"
psql -U postgres -h localhost -p 5432 -d prizma -c "\copy \"Employee\" TO STDOUT WITH CSV HEADER" >> "C:\hamisho\project_csms\database\hakib"

echo.
echo Partial backup created with essential tables.

:end
echo.
echo ========================================
echo If automated backup failed, please use:
echo.
echo 1. pgAdmin GUI:
echo    - Connect to localhost:5432
echo    - Username: postgres
echo    - Password: Mamlaka2020
echo    - Right-click 'prizma' → Backup
echo    - Save as 'hakib'
echo.
echo 2. Manual command:
echo    set PGPASSWORD=Mamlaka2020
echo    pg_dump -U postgres -h localhost prizma ^> hakib
echo.
echo ========================================
echo.
pause