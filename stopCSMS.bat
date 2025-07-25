@echo off
setlocal EnableDelayedExpansion

echo ================================================================
echo            CSMS - Civil Service Management System
echo                   Shutting Down All Services
echo ================================================================
echo.

REM ---- Function to kill all processes on a port ----
:kill_port
set PORT=%1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT%"') do (
    echo Terminating process on port %PORT% (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)
goto :eof

REM ---- List of ports to terminate ----
set PORTS=5432 9000 9001 8080 3000 3001 9002

for %%P in (%PORTS%) do (
    call :kill_port %%P
)

echo.
echo ✅ All known service ports have been released.
echo.
echo 🔚 CSMS services have been shut down.
echo ================================================================
pause
