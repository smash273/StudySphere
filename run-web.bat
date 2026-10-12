@echo off
cd /d "%~dp0"
REM StudySphere Web Application Builder (WAR)
echo =========================================================
echo   StudySphere - AI-Powered Academic Learning Platform
echo   Web Application Packager (Jakarta Servlet + JSP)
echo =========================================================
set JAVA_HOME=C:\Users\Rajde\tools\jdk-21.0.6+7
set PATH=%JAVA_HOME%\bin;C:\Users\Rajde\tools\apache-maven-3.9.6\bin;%PATH%
echo.
echo Packaging WAR for Tomcat 10+...
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo.
    echo WAR packaging failed. Check build logs above.
    pause
    exit /b 1
)
echo.
echo =========================================================
echo   Build Successful!
echo   Target artifact: target\studysphere.war
echo   Deploy this WAR to your Apache Tomcat 10+ /webapps/
echo =========================================================
pause
