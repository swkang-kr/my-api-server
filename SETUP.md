# 프로젝트 설정 가이드

## 환경 변수 설정

이 프로젝트는 민감한 정보를 환경 변수로 관리합니다.

### 1. 로컬 개발 환경 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성하세요:

```bash
cp .env.example .env
```

`.env` 파일을 열어 실제 값으로 수정하세요:

```properties
DB_URL=jdbc:postgresql://your-host:5432/your-database
DB_USERNAME=your-username
DB_PASSWORD=your-password
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
JWT_SECRET=your-256-bit-secret-key
```

### 2. 환경 변수 로드 방법

#### Option 1: IDE 설정 (IntelliJ IDEA)
1. Run → Edit Configurations
2. Environment variables 섹션에서 추가
3. 또는 `.env` 플러그인 사용

#### Option 2: Gradle 실행 시
```bash
# Windows
set DB_URL=jdbc:postgresql://...
set DB_USERNAME=postgres
set DB_PASSWORD=yourpassword
./gradlew bootRun

# Linux/Mac
export DB_URL=jdbc:postgresql://...
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
./gradlew bootRun
```

#### Option 3: .env 파일 자동 로드 (권장)
`build.gradle.kts`에 dotenv 플러그인 추가 (다음 단계 참조)

### 3. 프로덕션 환경

프로덕션 환경에서는 다음 방법 중 하나를 사용하세요:
- 클라우드 환경 변수 (AWS Secrets Manager, Railway Environment Variables 등)
- Kubernetes Secrets
- Docker Environment Variables

### 주의사항

⚠️ **절대 커밋하면 안 되는 파일:**
- `.env`
- `application-local.properties`
- `*-secret.properties`

✅ **커밋해도 되는 파일:**
- `.env.example`
- `application.properties` (환경 변수 참조만 포함)
- `application-dev.properties` (환경 변수 참조만 포함)
