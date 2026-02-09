# 배포 가이드

## Railway 배포

### 1. Railway 환경 변수 설정

**Railway Dashboard에서:**

1. 프로젝트 선택
2. **Variables** 탭 클릭
3. 환경 변수 추가:

```
DB_URL=jdbc:postgresql://your-railway-db-host:5432/railway
DB_USERNAME=postgres
DB_PASSWORD=your-railway-db-password
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
JWT_SECRET=your-production-jwt-secret
KAKAO_ADMIN_KEY=your-kakao-admin-key
KAKAO_SENDER_KEY=your-kakao-sender-key
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 2. Railway CLI로 설정

```bash
# Railway CLI 설치
npm i -g @railway/cli

# 로그인
railway login

# 환경 변수 설정
railway variables set DB_URL="jdbc:postgresql://..."
railway variables set DB_USERNAME="postgres"
railway variables set DB_PASSWORD="your-password"

# 모든 변수 확인
railway variables
```

### 3. 배포

```bash
# Railway에 배포
railway up

# 또는 GitHub 연동 시 자동 배포
git push origin main
```

---

## GitHub Actions CI/CD

### GitHub Secrets 설정

**GitHub Repository → Settings → Secrets and variables → Actions**

1. `DB_URL` 추가
2. `DB_USERNAME` 추가
3. `DB_PASSWORD` 추가
4. 기타 필요한 모든 환경 변수 추가

### Workflow 파일 생성

`.github/workflows/deploy.yml`:

```yaml
name: Deploy to Railway

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build with Gradle
      run: ./gradlew build
      env:
        DB_URL: ${{ secrets.DB_URL }}
        DB_USERNAME: ${{ secrets.DB_USERNAME }}
        DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
        JWT_SECRET: ${{ secrets.JWT_SECRET }}

    - name: Deploy to Railway
      uses: bervProject/railway-deploy@main
      with:
        railway_token: ${{ secrets.RAILWAY_TOKEN }}
        service: your-service-name
```

---

## Docker 배포

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/*.jar app.jar

# 환경 변수는 런타임에 주입
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker 실행

```bash
# 환경 변수 파일 사용
docker run -d \
  --env-file .env.production \
  -p 8080:8080 \
  your-image-name

# 또는 직접 지정
docker run -d \
  -e DB_URL="jdbc:postgresql://..." \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -p 8080:8080 \
  your-image-name
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: ${DB_URL}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    # 또는 env_file 사용
    env_file:
      - .env.production  # 서버에만 존재하는 파일
```

---

## AWS 배포

### AWS Elastic Beanstalk

**방법 1: 웹 콘솔**
1. Elastic Beanstalk 환경 선택
2. Configuration → Software
3. Environment properties에 환경 변수 추가

**방법 2: CLI**
```bash
# .ebextensions/env.config
option_settings:
  aws:elasticbeanstalk:application:environment:
    DB_URL: jdbc:postgresql://...
    DB_USERNAME: postgres
    DB_PASSWORD: your-password
```

### AWS ECS (Fargate)

Task Definition에서 환경 변수 설정:

```json
{
  "containerDefinitions": [
    {
      "environment": [
        {
          "name": "DB_URL",
          "value": "jdbc:postgresql://..."
        },
        {
          "name": "DB_PASSWORD",
          "value": "password"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:..."
        }
      ]
    }
  ]
}
```

---

## Kubernetes 배포

### Secret 생성

```bash
# Secret 생성
kubectl create secret generic app-secrets \
  --from-literal=DB_URL='jdbc:postgresql://...' \
  --from-literal=DB_USERNAME='postgres' \
  --from-literal=DB_PASSWORD='password'
```

### Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-api-server
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: app
        image: your-image:latest
        env:
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_URL
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_USERNAME
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: DB_PASSWORD
```

---

## Heroku 배포

### Config Vars 설정

```bash
# Heroku CLI
heroku config:set DB_URL="jdbc:postgresql://..."
heroku config:set DB_USERNAME="postgres"
heroku config:set DB_PASSWORD="password"

# 확인
heroku config
```

---

## 환경별 구성 파일

### 프로덕션용 프로퍼티

`src/main/resources/application-prod.properties`:

```properties
# 이 파일은 커밋하지 않음 (.gitignore에 추가됨)
spring.profiles.active=prod

# 모든 값은 환경 변수에서 가져옴
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# 프로덕션 설정
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.example.api=INFO
```

---

## 보안 모범 사례

### ✅ 해야 할 것

1. **각 환경마다 다른 비밀번호 사용**
   - 개발: `.env` (로컬)
   - 스테이징: Railway Variables
   - 프로덕션: Railway Variables (다른 값)

2. **프로덕션 시크릿 로테이션**
   ```bash
   # 정기적으로 비밀번호 변경
   railway variables set DB_PASSWORD="new-password"
   ```

3. **최소 권한 원칙**
   - DB 사용자는 필요한 권한만 부여
   - API 키는 필요한 scope만 허용

4. **시크릿 스캐닝 활성화**
   - GitHub Secret Scanning 활성화
   - git-secrets 도구 사용

### ❌ 하지 말아야 할 것

1. `.env` 파일을 GitHub에 커밋
2. 로그에 비밀번호 출력
3. 에러 메시지에 민감 정보 포함
4. 모든 환경에서 같은 비밀번호 사용

---

## 체크리스트

배포 전 확인사항:

- [ ] `.env` 파일이 `.gitignore`에 포함됨
- [ ] `.env.example`에 모든 필요한 변수 나열됨
- [ ] 프로덕션 환경 변수가 클라우드에 설정됨
- [ ] 프로덕션 DB 비밀번호가 개발 환경과 다름
- [ ] JWT_SECRET이 강력하고 유니크함
- [ ] 애플리케이션이 환경 변수 없이 실행되지 않음
- [ ] HTTPS 사용 (Railway는 자동 제공)
- [ ] 정기적인 시크릿 로테이션 계획 수립
