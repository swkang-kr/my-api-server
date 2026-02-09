# My API Server

Spring Boot 기반 엔터프라이즈급 REST API 서버

## 기술 스택

- **Java**: 17
- **Spring Boot**: 3.2.5
- **Database**: PostgreSQL
- **ORM**: MyBatis
- **Batch**: Spring Batch
- **Security**: Spring Security + OAuth2 (Google, GitHub, Kakao)
- **Session**: Spring Session JDBC
- **Message Queue**: RabbitMQ
- **Email**: Java Mail Sender
- **Kakao**: 알림톡/친구톡 발송
- **JWT**: Token 기반 인증

## 주요 기능

### 1. 사용자 관리
- 회원가입/로그인
- OAuth2 소셜 로그인 (Google, GitHub, Kakao)
- JWT 토큰 기반 인증

### 2. 알림 시스템
- 이메일 발송 (동기/비동기)
- 카카오톡 알림톡 발송
- 카카오톡 친구톡 발송
- 멀티채널 알림 (이메일 + 카카오톡)

### 3. 배치 처리
- 사용자 데이터 마이그레이션
- 일일 리포트 생성
- Chunk 기반 대용량 데이터 처리

### 4. 메시지 큐
- RabbitMQ를 통한 비동기 메시지 처리
- 이메일/알림 큐 분리

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/api/
│   │   ├── config/              # 설정 클래스
│   │   ├── controller/          # REST API 컨트롤러
│   │   ├── service/             # 비즈니스 로직
│   │   ├── mapper/              # MyBatis 매퍼
│   │   ├── entity/              # 엔티티
│   │   ├── dto/                 # DTO (request/response)
│   │   ├── security/            # 보안 관련
│   │   ├── batch/               # Spring Batch
│   │   ├── messaging/           # RabbitMQ
│   │   ├── kakao/               # 카카오톡 API
│   │   └── exception/           # 예외 처리
│   └── resources/
│       ├── application.properties
│       ├── mail-templates/      # 이메일 템플릿
│       ├── kakao-templates/     # 카카오톡 템플릿
│       ├── mapper/              # MyBatis XML
│       └── schema.sql           # DB 스키마
└── test/
```

## 시작하기

### 1. 사전 요구사항

- Java 17
- PostgreSQL 12+
- RabbitMQ 3.8+
- Gradle 7.0+

### 2. 데이터베이스 설정

```bash
# PostgreSQL 데이터베이스 생성
createdb railway

# 스키마 생성
psql -d railway -f src/main/resources/schema.sql
```

### 3. 환경 설정

`src/main/resources/application-dev.properties` 파일을 수정하여 다음을 설정합니다:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/railway
spring.datasource.username=your_username
spring.datasource.password=your_password

# OAuth2 (선택사항)
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Kakao
kakao.api.admin-key=YOUR_KAKAO_ADMIN_KEY
kakao.api.sender-key=YOUR_KAKAO_SENDER_KEY
```

### 4. RabbitMQ 시작

```bash
# Docker를 사용하는 경우
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 또는 로컬 설치 후
rabbitmq-server
```

### 5. 애플리케이션 실행

```bash
# Gradle 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 또는
java -jar build/libs/my-api-server-0.0.1-SNAPSHOT.jar
```

서버가 시작되면 http://localhost:8080 에서 접근 가능합니다.

## API 엔드포인트

### 헬스체크
```bash
GET /api/health
```

### 인증
```bash
# 회원가입
POST /api/auth/register
Content-Type: application/json
{
  "username": "user1",
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동",
  "phone": "01012345678"
}

# 로그인
POST /api/auth/login?username=user1&password=password123
```

### 사용자 관리
```bash
# 전체 사용자 조회
GET /api/users

# 특정 사용자 조회
GET /api/users/{id}

# 사용자 정보 수정
PUT /api/users/{id}
```

### 알림 발송
```bash
# 알림톡 발송 (동기)
POST /api/notifications/kakao/alimtalk
Content-Type: application/json
{
  "recipient": "01012345678",
  "messageType": "ALIMTALK",
  "templateCode": "WELCOME_001",
  "content": "환영 메시지",
  "variables": {
    "name": "홍길동",
    "date": "2024-02-09"
  }
}

# 알림톡 발송 (비동기)
POST /api/notifications/kakao/alimtalk/async

# 멀티채널 알림
POST /api/notifications/multi-channel?email=user@example.com&phone=01012345678&name=홍길동&subject=알림&content=내용
```

### 배치 작업
```bash
# 배치 작업 실행
POST /api/batch/run/{jobName}

# 배치 작업 상태 조회
GET /api/batch/status/{jobName}
```

## Swagger UI

API 문서는 다음 URL에서 확인할 수 있습니다:
- http://localhost:8080/swagger-ui.html

## 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests UserServiceTest
```

## 배포

### 환경 변수 설정

**로컬 개발:**
```bash
# .env.example을 복사
cp .env.example .env

# .env 파일 수정 후 실행
./gradlew bootRun
```

**Railway 배포:**
```bash
# Railway CLI 설치
npm install -g @railway/cli

# 로그인
railway login

# 환경 변수 설정 (자동)
./railway-setup.sh  # Linux/Mac
railway-setup.bat   # Windows

# 또는 Railway Dashboard에서 수동 설정
# Variables 탭에서 환경 변수 추가
```

**상세 가이드:**
- 📚 [배포 가이드](DEPLOYMENT.md)
- 🚂 [Railway 배포](docs/railway-deployment.md)
- 🔒 [보안 설정](SETUP.md)

### 프로덕션 빌드

```bash
# JAR 빌드
./gradlew clean bootJar

# 빌드된 JAR는 build/libs/ 디렉토리에 생성됩니다
```

### Docker 배포

```bash
# Docker 이미지 빌드
docker build -t my-api-server .

# 환경 변수와 함께 실행
docker run -d \
  -e DB_URL="jdbc:postgresql://..." \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="your-secret" \
  -p 8080:8080 \
  my-api-server
```

## 문제 해결

### PostgreSQL 연결 오류
- PostgreSQL이 실행 중인지 확인
- 데이터베이스와 사용자가 생성되었는지 확인
- application-dev.properties의 연결 정보 확인

### RabbitMQ 연결 오류
- RabbitMQ가 실행 중인지 확인 (포트 5672)
- 관리 콘솔 확인: http://localhost:15672 (guest/guest)

### OAuth2 로그인 실패
- 각 플랫폼(Google, GitHub, Kakao)에서 Client ID/Secret 발급 확인
- Redirect URI가 올바르게 설정되었는지 확인

## 라이센스

MIT License

## 기여

이슈와 Pull Request를 환영합니다!
