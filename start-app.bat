@echo off
echo Starting Computer Shop Application...
echo.
echo Prerequisites:
echo 1. SQL Server must be running on localhost:1433
echo 2. Database user 'sa' with password '123456' must exist
echo 3. Run database.sql to create database schema first
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    pause
    exit /b 1
)

echo Compiling application...
mvn clean compile
if %errorlevel% neq 0 (
    echo Error: Compilation failed
    pause
    exit /b 1
)

echo Starting Spring Boot application...
echo Access application at: http://localhost:8080
echo Press Ctrl+C to stop the application
mvn spring-boot:run

pause