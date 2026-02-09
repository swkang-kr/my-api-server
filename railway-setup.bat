@echo off
REM Railway 환경 변수 설정 스크립트 (Windows)

echo 🚂 Railway 환경 변수 설정
echo ==========================
echo.

REM .env 파일 확인
if not exist .env (
    echo ❌ .env 파일을 찾을 수 없습니다.
    echo 💡 .env.example을 복사하여 .env를 만드세요.
    pause
    exit /b 1
)

echo 📋 .env 파일에서 변수를 읽는 중...
echo.

REM Railway CLI 확인
where railway >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Railway CLI가 설치되지 않았습니다.
    echo.
    echo 설치 방법:
    echo   npm i -g @railway/cli
    echo.
    echo 또는 Railway Dashboard에서 수동으로 설정하세요:
    echo   https://railway.app/dashboard
    pause
    exit /b 1
)

echo ✅ Railway CLI 설치됨
echo.

REM 안내 메시지
echo 💡 다음 명령으로 환경 변수를 설정하세요:
echo.
echo railway login
echo.

REM .env 파일 내용 표시
echo 📤 설정할 환경 변수:
echo.
type .env | findstr /v "^#" | findstr /v "^$"

echo.
echo ============================================
echo 수동 설정 방법:
echo ============================================
echo.
echo 1. Railway Dashboard 열기
echo    https://railway.app/dashboard
echo.
echo 2. 프로젝트 선택
echo.
echo 3. Variables 탭 클릭
echo.
echo 4. 위의 환경 변수들을 추가
echo.
echo ============================================
echo.

pause
