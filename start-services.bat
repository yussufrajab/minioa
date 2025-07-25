@echo off
echo Starting CSMS Services...

echo.
echo ===================================================
echo Starting PostgreSQL Service...
echo ===================================================
net start postgresql-x64-15 2>nul
if %errorlevel% equ 0 (
    echo PostgreSQL started successfully
) else (
    echo PostgreSQL may already be running or failed to start
)

echo.
echo ===================================================
echo Starting MinIO Service...
echo ===================================================
echo Checking if MinIO is already running...
netstat -an | findstr ":9000" >nul
if %errorlevel% equ 0 (
    echo MinIO is already running
) else (
    echo Starting MinIO...
    start "MinIO" cmd /k "cd /d C:\ && docker run --rm --name minio -p 9000:9000 -p 9001:9001 -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin -v C:/minio/data:/data quay.io/minio/minio server /data --console-address :9001"
    
    echo Waiting for MinIO to start...
    timeout /t 10 /nobreak >nul
)

echo.
echo ===================================================
echo Starting Backend (Spring Boot)...
echo ===================================================
start "Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run -Dspring.profiles.active=prod"

echo.
echo Waiting for backend to start...
timeout /t 15 /nobreak >nul

echo.
echo ===================================================
echo Starting Frontend (Next.js)...
echo ===================================================
start "Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo ===================================================
echo All services are starting...
echo ===================================================
echo.
echo Frontend: http://localhost:9002
echo Backend:   http://localhost:8080
echo MinIO:     http://localhost:9001 (admin: minioadmin/minioadmin)
echo.
echo Check the individual terminal windows for startup progress.
echo.
pause