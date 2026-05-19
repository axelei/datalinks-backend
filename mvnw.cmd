@echo off
REM ---------------------------------------------------------------------------
REM Maven Wrapper (Windows)
REM ---------------------------------------------------------------------------
@SETLOCAL
set MAVEN_WRAPPER_DIR=%~dp0\.mvn\wrapper
java -jar "%MAVEN_WRAPPER_DIR%\maven-wrapper.jar" %*
ENDLOCAL
