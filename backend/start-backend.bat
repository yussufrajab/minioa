@echo off
echo Starting CSMS Spring Boot Backend...
echo.
echo Database: PostgreSQL (prizma)
echo Profile: Development
echo Port: 8080
echo.

REM Set the active profile to development
set SPRING_PROFILES_ACTIVE=dev

REM Start the Spring Boot application
echo Starting Spring Boot with development profile...
mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause