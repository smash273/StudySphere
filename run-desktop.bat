@echo off
cd /d "%~dp0"
REM StudySphere Desktop Application Launcher
echo =========================================================
echo   StudySphere - AI-Powered Academic Learning Platform
echo   JavaFX Desktop Client (CSE1007 Java Programming)
echo =========================================================
set JAVA_HOME=C:\Users\Rajde\tools\jdk-21.0.6+7
set PATH=%JAVA_HOME%\bin;C:\Users\Rajde\tools\apache-maven-3.9.6\bin;%PATH%
echo.
echo Building & launching StudySphere Desktop...
call mvn javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo Execution failed. Please verify PostgreSQL is running and environment is configured.
    pause
    exit /b 1
)
pause
