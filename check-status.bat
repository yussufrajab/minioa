@echo off
echo ================================================================
echo            CSMS - Service Status Check
echo ================================================================
echo.

echo Checking all required services...
echo.

REM PostgreSQL
echo 📊 PostgreSQL (Database):
netstat -an | findstr ":5432" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 5432
) else (
    echo    ❌ Not running on port 5432
    echo    💡 Start with: net start postgresql-x64-15
)

echo.

REM MinIO S3 API
echo 📁 MinIO S3 API (File Storage):
netstat -an | findstr ":9000" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 9000
) else (
    echo    ❌ Not running on port 9000
    echo    💡 Start with: minio.exe server C:\minio\data --console-address :9001
)

echo.

REM MinIO Console
echo 🖥️ MinIO Console:
netstat -an | findstr ":9001" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 9001
    echo    🌐 Access: http://localhost:9001 (minioadmin/minioadmin)
) else (
    echo    ❌ Not running on port 9001
)

echo.

REM Backend
echo 🚀 CSMS Backend (Spring Boot):
netstat -an | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 8080
    echo    🌐 API: http://localhost:8080
    echo    📖 Docs: http://localhost:8080/swagger-ui.html
    
    REM Check if backend is healthy
    curl -s http://localhost:8080/actuator/health >nul 2>&1
    if %errorlevel% equ 0 (
        echo    💚 Health check: PASSED
    ) else (
        echo    ⚠️ Health check: May still be starting
    )
) else (
    echo    ❌ Not running on port 8080
    echo    💡 Start from backend folder: mvn spring-boot:run
)

echo.

REM Frontend
echo 🌐 CSMS Frontend (Next.js):
set FRONTEND_FOUND=0

netstat -an | findstr ":3000" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 3000
    echo    🌐 App: http://localhost:3000
    set FRONTEND_FOUND=1
)

netstat -an | findstr ":9002" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 9002
    echo    🌐 App: http://localhost:9002
    set FRONTEND_FOUND=1
)

netstat -an | findstr ":3001" >nul
if %errorlevel% equ 0 (
    echo    ✓ Running on port 3001
    echo    🌐 App: http://localhost:3001
    set FRONTEND_FOUND=1
)

if %FRONTEND_FOUND% equ 0 (
    echo    ❌ Not running on any common ports
    echo    💡 Start from frontend folder: npm run dev
)

echo.
echo ================================================================
echo Summary:
echo ================================================================

REM Count running services
set /a RUNNING=0

netstat -an | findstr ":5432" >nul
if %errorlevel% equ 0 set /a RUNNING+=1

netstat -an | findstr ":9000" >nul
if %errorlevel% equ 0 set /a RUNNING+=1

netstat -an | findstr ":8080" >nul
if %errorlevel% equ 0 set /a RUNNING+=1

if %FRONTEND_FOUND% equ 1 set /a RUNNING+=1

echo %RUNNING%/4 core services are running

if %RUNNING% equ 4 (
    echo ✅ All services are running! CSMS is ready to use.
) else if %RUNNING% geq 2 (
    echo ⚠️ Some services are running. Check individual status above.
) else (
    echo ❌ Most services are not running. Use start-all-services.bat to start everything.
)

echo.
echo 🔧 Quick Actions:
echo    start-all-services.bat  - Start everything
echo    start-csms.bat         - Quick start (daily use)
echo    stop-all-services.bat  - Stop CSMS services
echo.
pause