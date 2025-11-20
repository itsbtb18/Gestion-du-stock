@echo off
echo ========================================
echo  SuperMarket Management System
echo  Starting Application...
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/
    pause
    exit /b 1
)

echo [1/3] Cleaning previous builds...
call mvn clean

echo.
echo [2/3] Compiling project...
call mvn compile

echo.
echo [3/3] Launching application...
call mvn javafx:run

pause
