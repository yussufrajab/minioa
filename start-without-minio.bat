@echo off
echo Starting CSMS Services (without MinIO)...
echo =========================================

:: Start PostgreSQL
echo Starting PostgreSQL...
start "PostgreSQL" cmd /k "net start postgresql-x64-15"
timeout /t 5 /nobreak >nul

:: Start Backend
echo Starting Backend...
cd /d "%~dp0backend"
start "CSMS Backend" cmd /k "mvn spring-boot:run -Dspring.profiles.active=dev"
timeout /t 10 /nobreak >nul

:: Start Frontend
echo Starting Frontend...
cd /d "%~dp0frontend"
start "CSMS Frontend" cmd /k "npm run dev"

echo.
echo All services starting...
echo ========================
echo PostgreSQL: Starting/Running
echo Backend: http://localhost:8080/api
echo Frontend: http://localhost:9002
echo.
echo NOTE: Running without MinIO - file uploads will not work
echo.
echo Press any key to exit this window...
pause >nul