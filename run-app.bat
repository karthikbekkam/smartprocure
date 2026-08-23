@echo off
TITLE SmartProcure Enterprise ERP Application Launcher
echo =======================================================================
echo          SmartProcure - Enterprise Procurement System Launcher
echo =======================================================================
echo.
echo [1] Checking Java Environment...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java runtime not found in PATH.
    echo Please install JDK 21 from https://adoptium.net/temurin/releases/?version=21
    pause
    exit /b 1
)

echo.
echo [2] Starting SmartProcure Enterprise Web Server...
echo Application URL: http://localhost:8080/smartprocure
echo Swagger API Docs: http://localhost:8080/smartprocure/swagger-ui.html
echo.

mvn spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [INFO] Opening Instant Demo Preview in Browser...
    start open-app.html
)

pause
