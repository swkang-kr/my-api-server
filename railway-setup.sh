#!/bin/bash
# Railway 환경 변수 설정 스크립트

echo "🚂 Railway 환경 변수 설정"
echo "=========================="
echo ""

# .env 파일에서 변수 읽기
if [ ! -f .env ]; then
    echo "❌ .env 파일을 찾을 수 없습니다."
    echo "💡 .env.example을 복사하여 .env를 만드세요."
    exit 1
fi

echo "📋 .env 파일에서 변수를 읽는 중..."
echo ""

# Railway CLI 설치 확인
if ! command -v railway &> /dev/null; then
    echo "⚠️  Railway CLI가 설치되지 않았습니다."
    echo ""
    echo "설치 방법:"
    echo "  npm i -g @railway/cli"
    echo ""
    echo "또는 Railway Dashboard에서 수동으로 설정하세요:"
    echo "  https://railway.app/dashboard"
    exit 1
fi

# Railway 로그인 확인
echo "🔐 Railway 로그인 확인 중..."
if ! railway whoami &> /dev/null; then
    echo "⚠️  Railway에 로그인되지 않았습니다."
    echo "railway login 명령을 실행하세요."
    exit 1
fi

echo "✅ Railway 로그인됨"
echo ""

# 환경 변수 설정
echo "📤 환경 변수 업로드 중..."
echo ""

# .env 파일 읽어서 Railway에 설정
while IFS='=' read -r key value; do
    # 주석과 빈 줄 건너뛰기
    if [[ $key =~ ^#.* ]] || [[ -z $key ]]; then
        continue
    fi

    # 앞뒤 공백 제거
    key=$(echo "$key" | xargs)
    value=$(echo "$value" | xargs)

    echo "  → $key"
    railway variables set "$key=$value"
done < .env

echo ""
echo "✅ 모든 환경 변수가 설정되었습니다!"
echo ""
echo "📋 설정된 변수 확인:"
railway variables

echo ""
echo "🚀 배포 방법:"
echo "  railway up"
echo ""
echo "또는 GitHub 연동 시 자동 배포:"
echo "  git push origin main"
