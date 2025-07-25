@echo off
echo ================================================================
echo            CSMS - Stopping All Services
echo ================================================================
echo.

echo Stopping CSMS services...

echo.
echo Stopping Java processes (Backend)...
taskkill /F /IM java.exe >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Backend processes stopped
) else (
    echo ⚠ No Java processes found
)

echo.
echo Stopping Node.js processes (Frontend)...
taskkill /F /IM node.exe >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Frontend processes stopped
) else (
    echo ⚠ No Node.js processes found
)

echo.
echo Freeing ports...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" 2^>nul') do (
    echo Freeing port 8080 (PID: %%a)
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3000" 2^>nul') do (
    echo Freeing port 3000 (PID: %%a)
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":9002" 2^>nul') do (
    echo Freeing port 9002 (PID: %%a)
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo Note: MinIO and PostgreSQL are left running as they may be used by other applications.
echo To stop them manually:
echo   MinIO: Press Ctrl+C in the MinIO terminal window
echo   PostgreSQL: net stop postgresql-x64-15 (requires admin)
echo.

echo ✓ CSMS services stopped successfully!
echo.
pause