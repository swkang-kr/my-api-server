# 🚀 배포 빠른 시작 가이드

## 📌 핵심 개념

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│  개발 환경   │         │  GitHub      │         │  Railway    │
│             │         │              │         │             │
│  .env 파일  │ ──────▶ │  코드만      │ ──────▶ │ Variables   │
│  (로컬)     │         │  (비밀 제외) │         │  (환경변수) │
└─────────────┘         └──────────────┘         └─────────────┘
```

## ⚡ 3단계로 배포하기

### 1️⃣ 로컬에서 .env 파일 생성

```bash
# .env.example 복사
cp .env.example .env

# .env 파일 편집
DB_URL=jdbc:postgresql://interchange.proxy.rlwy.net:57338/mydb_dev
DB_USERNAME=postgres
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

### 2️⃣ GitHub에 코드 Push

```bash
git add .
git commit -m "feat: Add environment variable configuration"
git push origin main
```

**⚠️ 주의:** `.env` 파일은 `.gitignore`에 의해 자동으로 제외됩니다!

### 3️⃣ Railway에 환경 변수 설정

#### 방법 A: Railway Dashboard (가장 쉬움)

1. **Railway 접속**
   - 🌐 https://railway.app/dashboard

2. **프로젝트 선택**

3. **Variables 탭 클릭**

4. **환경 변수 추가** (`.env` 파일 내용 복사)
   ```
   DB_URL = jdbc:postgresql://...
   DB_USERNAME = postgres
   DB_PASSWORD = your-password
   JWT_SECRET = your-secret
   GOOGLE_CLIENT_ID = your-client-id
   GOOGLE_CLIENT_SECRET = your-secret
   ```

5. **Deploy** 클릭 또는 GitHub 연동 시 자동 배포

#### 방법 B: Railway CLI (빠름)

```bash
# 1. Railway CLI 설치
npm install -g @railway/cli

# 2. 로그인
railway login

# 3. 환경 변수 설정
railway variables set DB_URL="jdbc:postgresql://..."
railway variables set DB_USERNAME="postgres"
railway variables set DB_PASSWORD="your-password"
railway variables set JWT_SECRET="your-secret"

# 4. 배포
railway up
```

#### 방법 C: 자동 스크립트 (매우 빠름)

```bash
# Windows
railway-setup.bat

# Linux/Mac
./railway-setup.sh
```

---

## 📊 환경별 설정 비교

| 항목 | 로컬 개발 | Railway 배포 |
|------|----------|-------------|
| **설정 파일** | `.env` | ❌ 없음 |
| **환경 변수** | 파일에서 로드 | Railway Variables |
| **비밀번호** | 개발용 | 프로덕션용 (다름) |
| **자동 배포** | ❌ | ✅ GitHub 연동 |
| **HTTPS** | ❌ | ✅ 자동 제공 |

---

## 🔍 배포 확인

### Health Check

```bash
# 로컬
curl http://localhost:8080/api/health

# Railway
curl https://your-app.railway.app/api/health
```

**예상 응답:**
```json
{
  "service": "my-api-server",
  "status": "UP",
  "timestamp": "2026-02-09T..."
}
```

### Swagger UI

```
로컬: http://localhost:8080/swagger-ui/index.html
Railway: https://your-app.railway.app/swagger-ui/index.html
```

---

## 🎯 자주 묻는 질문

### Q1: .env 파일을 GitHub에 올려도 되나요?

**❌ 절대 안 됩니다!**

- `.env` 파일은 `.gitignore`에 포함되어 있어 자동으로 제외됩니다
- 비밀번호와 API 키가 포함되어 있어 공개되면 안 됩니다
- 대신 `.env.example`을 커밋하여 필요한 변수를 안내하세요

### Q2: Railway에 .env 파일을 어떻게 업로드하나요?

**업로드하지 않습니다!**

- Railway에서는 **Variables 탭**에서 환경 변수를 직접 입력합니다
- `.env` 파일의 내용을 복사하여 Railway Variables에 하나씩 추가하세요
- 또는 `railway-setup.sh` 스크립트를 사용하세요

### Q3: 로컬과 프로덕션 DB가 다른데 어떻게 하나요?

**환경별로 다른 값을 사용하세요:**

```bash
# 로컬 .env
DB_URL=jdbc:postgresql://interchange.proxy.rlwy.net:57338/mydb_dev

# Railway Variables
DB_URL=jdbc:postgresql://prod-server:5432/prod_db
```

### Q4: 환경 변수를 변경하면 재배포해야 하나요?

**Railway에서는:**
- Variables 변경 시 자동으로 재배포됩니다
- 또는 "Redeploy" 버튼을 클릭하세요

**로컬에서는:**
- `.env` 파일 수정 후 애플리케이션 재시작

### Q5: 팀원이 합류하면 어떻게 설정하나요?

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo/my-api-server.git
cd my-api-server

# 2. .env 파일 생성
cp .env.example .env

# 3. .env 파일 수정 (팀 리더에게 비밀번호 요청)
# DB_PASSWORD=...
# JWT_SECRET=...

# 4. 실행
./gradlew bootRun
```

---

## 🔒 보안 체크리스트

배포 전 확인:

- [ ] `.env` 파일이 `.gitignore`에 포함됨
- [ ] GitHub에 `.env` 파일이 커밋되지 않음
- [ ] Railway Variables에 모든 환경 변수 설정됨
- [ ] 프로덕션 비밀번호 ≠ 개발 비밀번호
- [ ] JWT_SECRET이 강력함 (최소 32자)
- [ ] Railway Database 비밀번호가 설정됨
- [ ] HTTPS가 활성화됨 (Railway 자동 제공)

---

## 📚 더 자세한 가이드

- 📖 **전체 배포 가이드:** [DEPLOYMENT.md](DEPLOYMENT.md)
- 🚂 **Railway 상세 가이드:** [docs/railway-deployment.md](docs/railway-deployment.md)
- 🔧 **환경 설정 가이드:** [SETUP.md](SETUP.md)
- 🔒 **보안 정리 가이드:** [SECURITY_CLEANUP.md](SECURITY_CLEANUP.md)

---

## 💡 팁

### 빠른 재배포

```bash
# Railway CLI
railway up

# 또는 GitHub Push
git push origin main
```

### 로그 확인

```bash
# Railway CLI
railway logs

# 또는 Railway Dashboard → Deployments 탭
```

### 환경 변수 확인

```bash
# Railway CLI
railway variables

# 또는 Railway Dashboard → Variables 탭
```

---

## 🎉 완료!

이제 `.env` 파일 없이도 Railway에서 안전하게 배포할 수 있습니다!

배포 URL: `https://your-app.railway.app`
