@echo off
REM Execute schema.sql on Railway database

echo Connecting to Railway PostgreSQL database...
echo.

set PGPASSWORD=mGnCZTdilSEekfHOmavezeECINsUloMq
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -h interchange.proxy.rlwy.net -p 57338 -U postgres -d railway -f src\main\resources\schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Schema executed successfully!
) else (
    echo.
    echo Failed to execute schema.
)

pause
