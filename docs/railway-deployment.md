# Railway 배포 가이드

## 📋 개요

Railway에서는 `.env` 파일 없이 환경 변수를 Dashboard에서 직접 설정합니다.

---

## 🚀 배포 과정

### 1️⃣ Railway 프로젝트 생성

```bash
# Railway CLI 설치
npm install -g @railway/cli

# 로그인
railway login

# 프로젝트 연결 (기존 프로젝트)
railway link

# 또는 새 프로젝트 생성
railway init
```

---

### 2️⃣ 환경 변수 설정

#### 방법 A: Railway Dashboard (권장)

1. **Railway Dashboard 열기**
   - 🌐 https://railway.app/dashboard

2. **프로젝트 선택**
   - 배포할 프로젝트 클릭

3. **Variables 탭 클릭**
   - 좌측 메뉴에서 "Variables" 선택

4. **환경 변수 추가**

   **데이터베이스 설정:**
   ```
   Variable Name: DB_URL
   Value: jdbc:postgresql://containers-us-west-123.railway.app:5432/railway

   Variable Name: DB_USERNAME
   Value: postgres

   Variable Name: DB_PASSWORD
   Value: [Railway DB 비밀번호]
   ```

   **OAuth2 설정:**
   ```
   Variable Name: GOOGLE_CLIENT_ID
   Value: [Google Console에서 발급받은 ID]

   Variable Name: GOOGLE_CLIENT_SECRET
   Value: [Google Console에서 발급받은 Secret]
   ```

   **JWT 설정:**
   ```
   Variable Name: JWT_SECRET
   Value: [최소 32자 이상의 랜덤 문자열]
   ```

   **Kakao API 설정:**
   ```
   Variable Name: KAKAO_ADMIN_KEY
   Value: [Kakao Developers에서 발급]

   Variable Name: KAKAO_SENDER_KEY
   Value: [Kakao Biz Message Sender Key]
   ```

   **Mail 설정:**
   ```
   Variable Name: MAIL_USERNAME
   Value: your-email@gmail.com

   Variable Name: MAIL_PASSWORD
   Value: [Gmail App Password]
   ```

#### 방법 B: Railway CLI

```bash
# 한 번에 하나씩 설정
railway variables set DB_URL="jdbc:postgresql://your-host:5432/railway"
railway variables set DB_USERNAME="postgres"
railway variables set DB_PASSWORD="your-password"
railway variables set GOOGLE_CLIENT_ID="your-client-id"
railway variables set GOOGLE_CLIENT_SECRET="your-secret"
railway variables set JWT_SECRET="your-jwt-secret-key-min-32-chars"
railway variables set KAKAO_ADMIN_KEY="your-kakao-key"
railway variables set MAIL_USERNAME="your-email@gmail.com"
railway variables set MAIL_PASSWORD="your-app-password"

# 설정 확인
railway variables
```

#### 방법 C: 자동 스크립트 (Windows)

```bash
# 프로젝트 루트에서 실행
.\railway-setup.bat
```

---

### 3️⃣ 데이터베이스 연결 정보 확인

Railway PostgreSQL을 사용하는 경우:

1. **Database 탭 클릭**
2. **Connect** 클릭
3. **JDBC URL 복사**
   ```
   jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
   ```
4. Variables에 `DB_URL`로 추가

---

### 4️⃣ GitHub 연동 (자동 배포)

1. **Railway Dashboard에서:**
   - Settings → Connect to GitHub
   - Repository 선택
   - Branch 선택 (main 또는 master)

2. **이제 GitHub에 Push하면 자동 배포됨:**
   ```bash
   git add .
   git commit -m "feat: Add Railway deployment config"
   git push origin main
   ```

3. **배포 로그 확인:**
   - Railway Dashboard에서 실시간으로 배포 로그 확인 가능

---

### 5️⃣ 수동 배포

```bash
# Railway CLI로 배포
railway up

# 특정 서비스 선택
railway up --service my-api-server

# 로그 확인
railway logs
```

---

## 🔍 배포 후 확인

### Health Check

```bash
# Railway에서 제공하는 도메인으로 접속
curl https://your-app.railway.app/api/health

# 예상 응답:
# {"service":"my-api-server","status":"UP","timestamp":"2026-02-09T..."}
```

### Swagger UI

```
https://your-app.railway.app/swagger-ui/index.html
```

### 로그 확인

```bash
# Railway CLI
railway logs

# 또는 Dashboard에서 "Deployments" 탭 확인
```

---

## 🛠️ 트러블슈팅

### 문제 1: 환경 변수를 찾을 수 없음

**증상:**
```
Error: Could not resolve placeholder 'DB_URL'
```

**해결:**
1. Railway Variables 탭에서 `DB_URL` 확인
2. 변수 이름 철자 확인 (대소문자 구분)
3. 서비스 재배포

### 문제 2: 데이터베이스 연결 실패

**증상:**
```
Connection refused: localhost:5432
```

**해결:**
1. `DB_URL`이 Railway PostgreSQL 주소인지 확인
2. Railway Database의 Connection String 확인
3. `jdbc:postgresql://` 프로토콜 확인

### 문제 3: 빌드 실패

**증상:**
```
Task :bootJar FAILED
```

**해결:**
1. `build.gradle.kts`에서 `.env` 로드 코드 확인
2. Railway에서는 `.env` 파일 없이 환경 변수 사용
3. `application.properties`에서 기본값 제거 확인

---

## 📊 환경 구성 비교

| 환경 | 설정 방법 | 파일 |
|------|----------|------|
| **로컬 개발** | `.env` 파일 | ✅ 사용 |
| **Railway (개발)** | Railway Variables | ❌ 없음 |
| **Railway (프로덕션)** | Railway Variables | ❌ 없음 |

---

## 🔐 보안 팁

### DO ✅

1. **환경별 다른 비밀번호 사용**
   - 로컬: `.env`의 비밀번호
   - Railway 개발: 다른 비밀번호
   - Railway 프로덕션: 또 다른 비밀번호

2. **강력한 JWT Secret 사용**
   ```bash
   # 랜덤 생성 (Linux/Mac)
   openssl rand -base64 32

   # 랜덤 생성 (Windows PowerShell)
   -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 32 | % {[char]$_})
   ```

3. **정기적인 시크릿 로테이션**
   - 3개월마다 DB 비밀번호 변경
   - 6개월마다 JWT Secret 변경

### DON'T ❌

1. `.env` 파일을 GitHub에 커밋하지 말 것
2. 로그에 환경 변수 출력하지 말 것
3. 모든 환경에 같은 비밀번호 사용하지 말 것

---

## 📚 참고 문서

- [Railway 공식 문서](https://docs.railway.app/)
- [Railway Environment Variables](https://docs.railway.app/deploy/variables)
- [Railway CLI](https://docs.railway.app/develop/cli)
