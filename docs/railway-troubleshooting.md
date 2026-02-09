# Railway 배포 트러블슈팅

## 🔧 JAR 파일을 찾을 수 없음

### 오류 메시지
```
ls: cannot access '*/build/libs/*jar': No such file or directory
```

### 원인
1. Gradle 빌드가 실행되지 않음
2. `gradlew` 실행 권한이 없음
3. Railway가 빌드 명령을 모름

### ✅ 해결 방법

#### 방법 1: railway.json 사용 (권장)

`railway.json` 파일이 프로젝트 루트에 있는지 확인:

```json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "./gradlew clean bootJar"
  },
  "deploy": {
    "startCommand": "java -jar build/libs/*.jar"
  }
}
```

#### 방법 2: Railway Dashboard 설정

1. **Railway Dashboard** 열기
2. 프로젝트 선택 → **Settings** 탭
3. **Build & Deploy** 섹션:

   **Build Command:**
   ```bash
   ./gradlew clean bootJar --no-daemon
   ```

   **Start Command:**
   ```bash
   java -Dserver.port=$PORT -jar build/libs/*.jar
   ```

4. **Deploy** 클릭

#### 방법 3: nixpacks.toml 사용

`nixpacks.toml` 파일 확인:

```toml
[phases.setup]
nixPkgs = ['...', 'jdk17']

[phases.install]
cmds = ['chmod +x gradlew']

[phases.build]
cmds = ['./gradlew clean bootJar --no-daemon']

[start]
cmd = 'java -Dserver.port=$PORT -jar build/libs/*.jar'
```

---

## 🔧 gradlew: Permission denied

### 오류 메시지
```
/bin/sh: ./gradlew: Permission denied
```

### 원인
Git이 `gradlew` 파일의 실행 권한을 추적하지 않음

### ✅ 해결 방법

```bash
# 실행 권한 추가
git update-index --chmod=+x gradlew

# 확인
git ls-files --stage gradlew
# 출력: 100755 ... (755가 맞음)

# 커밋
git add gradlew
git commit -m "fix: Add execute permission to gradlew"
git push origin main
```

---

## 🔧 Java version 문제

### 오류 메시지
```
Unsupported class file major version 61
```

### 원인
Railway가 잘못된 Java 버전 사용 (프로젝트는 Java 17 필요)

### ✅ 해결 방법

#### 방법 1: nixpacks.toml 설정

```toml
[phases.setup]
nixPkgs = ['jdk17']
```

#### 방법 2: system.properties 생성

`system.properties` 파일 생성:

```properties
java.runtime.version=17
```

---

## 🔧 환경 변수 문제

### 오류 메시지
```
Could not resolve placeholder 'DB_URL' in value "${DB_URL}"
```

### 원인
Railway Variables에 환경 변수가 설정되지 않음

### ✅ 해결 방법

1. **Railway Dashboard** → 프로젝트 선택
2. **Variables** 탭 클릭
3. 필수 환경 변수 추가:

```
DB_URL=jdbc:postgresql://...
DB_USERNAME=postgres
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

---

## 🔧 빌드는 성공하지만 시작 실패

### 오류 메시지
```
Error: Could not find or load main class
```

### 원인
JAR 파일 이름이 예상과 다름

### ✅ 해결 방법

#### 1. JAR 파일 이름 확인

`build.gradle.kts` 확인:

```kotlin
springBoot {
    mainClass.set("com.example.api.ApiApplication")
}
```

#### 2. Start Command 수정

**Railway Dashboard → Settings → Deploy:**

```bash
# 방법 1: 와일드카드 사용
java -Dserver.port=$PORT -jar build/libs/*.jar

# 방법 2: 정확한 파일명
java -Dserver.port=$PORT -jar build/libs/my-api-server-0.0.1-SNAPSHOT.jar
```

---

## 🔧 포트 바인딩 문제

### 오류 메시지
```
Web process failed to bind to $PORT within 60 seconds
```

### 원인
애플리케이션이 Railway의 $PORT 환경 변수를 사용하지 않음

### ✅ 해결 방법

#### 방법 1: Start Command에 포트 지정

```bash
java -Dserver.port=$PORT -jar build/libs/*.jar
```

#### 방법 2: application.properties 설정

```properties
server.port=${PORT:8080}
```

---

## 🔧 데이터베이스 연결 실패

### 오류 메시지
```
Connection refused: localhost:5432
```

### 원인
Railway DB 연결 정보가 환경 변수에 없음

### ✅ 해결 방법

1. **Railway Dashboard** → Database 선택
2. **Connect** 탭에서 JDBC URL 확인
3. **Variables** 탭에 추가:

```
DB_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
DB_USERNAME=postgres
DB_PASSWORD=<Railway DB 비밀번호>
```

---

## 🔧 RabbitMQ 연결 문제

### 오류 메시지
```
Connection refused: localhost:5672
```

### 원인
RabbitMQ가 Railway에 없음 (로컬에만 있음)

### ✅ 해결 방법

RabbitMQ는 개발 환경에서 이미 비활성화됨:

`application.properties` 확인:
```properties
# RabbitMQ - Disabled by default
# spring.rabbitmq.host=localhost
```

만약 프로덕션에서 RabbitMQ 사용 시:
1. Railway에서 RabbitMQ 서비스 추가
2. 환경 변수 설정

---

## 🔧 메모리 부족

### 오류 메시지
```
OutOfMemoryError: Java heap space
```

### 원인
Railway 무료 플랜의 메모리 제한 (512MB)

### ✅ 해결 방법

**Start Command 수정:**

```bash
java -Xmx450m -Xms256m -Dserver.port=$PORT -jar build/libs/*.jar
```

**최적화:**
```bash
java -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=75.0 \
     -Dserver.port=$PORT \
     -jar build/libs/*.jar
```

---

## 📊 배포 체크리스트

배포 전 확인:

- [ ] `railway.json` 또는 `nixpacks.toml` 존재
- [ ] `gradlew` 실행 권한 (100755)
- [ ] Railway Variables에 모든 환경 변수 설정
- [ ] Java 17 설정됨
- [ ] Start Command에 `-Dserver.port=$PORT` 포함
- [ ] `build/libs/*.jar` 경로 정확함
- [ ] DB 연결 정보가 Railway DB 주소임
- [ ] GitHub 연동 완료

---

## 🚀 배포 성공 확인

### 1. 빌드 로그 확인

**Railway Dashboard → Deployments → 최신 배포 클릭**

성공적인 빌드:
```
✓ Building with Nixpacks
✓ ./gradlew clean bootJar
✓ BUILD SUCCESSFUL in 45s
✓ Starting deployment
```

### 2. Health Check

```bash
curl https://your-app.railway.app/api/health
```

예상 응답:
```json
{
  "service": "my-api-server",
  "status": "UP",
  "timestamp": "2026-02-09T..."
}
```

### 3. Swagger UI 접속

```
https://your-app.railway.app/swagger-ui/index.html
```

---

## 💡 유용한 명령어

### Railway CLI로 로그 확인

```bash
# 실시간 로그
railway logs --follow

# 최근 100줄
railway logs --tail 100

# 빌드 로그만
railway logs --deployment
```

### 로컬에서 빌드 테스트

```bash
# Railway와 동일한 빌드 실행
./gradlew clean bootJar --no-daemon

# JAR 파일 확인
ls -lh build/libs/

# JAR 실행 테스트
java -jar build/libs/*.jar
```

### Railway 환경에서 테스트

```bash
# Railway Shell 접속
railway shell

# 환경 변수 확인
env | grep DB_

# Java 버전 확인
java -version
```

---

## 📞 추가 도움

문제가 계속되면:

1. **Railway 공식 문서**: https://docs.railway.app/
2. **Railway Discord**: https://discord.gg/railway
3. **Railway Status**: https://status.railway.app/

프로젝트별 로그를 첨부하여 질문하세요!
