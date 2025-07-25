@echo off
title CSMS Startup
echo ================================================================
echo            CSMS - Quick Start
echo ================================================================

REM Quick service checks
echo Checking services...

REM PostgreSQL
netstat -an | findstr ":5432" >nul
if %errorlevel% equ 0 (
    echo ✓ PostgreSQL ready
) else (
    echo Starting PostgreSQL...
    net start postgresql-x64-15 >nul 2>&1
)

REM MinIO check
netstat -an | findstr ":9000" >nul
if %errorlevel% equ 0 (
    echo ✓ MinIO ready
) else (
    echo ⚠ MinIO not running - file uploads will be disabled
    echo   Start MinIO manually if needed: minio.exe server C:\minio\data --console-address :9001
)

REM Find available frontend port
set FRONTEND_PORT=3000
netstat -an | findstr ":3000" >nul
if %errorlevel% equ 0 set FRONTEND_PORT=3001
netstat -an | findstr ":3001" >nul
if %errorlevel% equ 0 set FRONTEND_PORT=9002

echo.
echo Starting CSMS services...
echo - Backend: http://localhost:8080
echo - Frontend: http://localhost:%FRONTEND_PORT%
echo.

REM Start backend
start "CSMS Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run -Dspring.profiles.active=prod"

REM Wait a bit then start frontend
timeout /t 15 /nobreak >nul
start "CSMS Frontend" cmd /k "cd /d %~dp0frontend && npm run dev -- -p %FRONTEND_PORT%"

REM Wait for frontend then open browser
timeout /t 10 /nobreak >nul
start http://localhost:%FRONTEND_PORT%

echo.
echo CSMS is starting...
echo Browser will open automatically when ready.
echo.
echo Keep terminal windows open to maintain services.
pause