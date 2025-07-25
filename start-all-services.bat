@echo off
echo Starting CSMS Services...
echo ========================

:: Start PostgreSQL
echo Starting PostgreSQL...
start "PostgreSQL" cmd /k "net start postgresql-x64-17"
timeout /t 5 /nobreak >nul

:: Start MinIO
echo Starting MinIO...
if exist "C:\minio\minio.exe" (
    cd /d "C:\minio"
    start "MinIO" cmd /k "minio.exe server C:\minio\data --console-address :9001"
    timeout /t 3 /nobreak >nul
) else (
    echo WARNING: MinIO not found at C:\minio\minio.exe
    echo.
    echo Please ensure MinIO is installed at C:\minio\
    echo.
    echo Continuing without MinIO...
    timeout /t 3 /nobreak >nul
)

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
if exist "C:\minio\minio.exe" (
    echo MinIO: http://localhost:9000 ^(console: http://localhost:9001^)
) else (
    echo MinIO: NOT RUNNING - File uploads will not work
)
echo Backend: http://localhost:8080/api
echo Frontend: http://localhost:9002
echo.
echo Press any key to exit this window...
pause >nul