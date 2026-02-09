# Spring Boot REST API 서버 생성 가이드 (MyBatis + Full Stack 버전)

## 프로젝트 기본 정보
- **프로젝트명**: my-api-server
- **Java 버전**: 17
- **Spring Boot 버전**: 3.2.x
- **빌드 도구**: Gradle (Kotlin DSL)
- **패키지 구조**: com.example.api
- **ORM**: MyBatis
- **Database**: PostgreSQL
- **Batch**: Spring Batch
- **Session**: Spring Session JDBC
- **Security**: OAuth2
- **Message Queue**: RabbitMQ
- **Email**: Java Mail Sender
- **Kakao**: 카카오톡 알림톡/친구톡 발송

## 기본 요구사항

### 1. 프로젝트 구조 생성
```
src/
├── main/
│   ├── java/com/example/api/
│   │   ├── ApiApplication.java
│   │   ├── config/
│   │   │   ├── WebConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── OAuth2Config.java
│   │   │   ├── MyBatisConfig.java
│   │   │   ├── BatchConfig.java
│   │   │   ├── SessionConfig.java
│   │   │   ├── RabbitMqConfig.java
│   │   │   ├── MailConfig.java
│   │   │   └── KakaoConfig.java
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── BatchController.java
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   └── NotificationController.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── EmailService.java
│   │   │   ├── MessageQueueService.java
│   │   │   └── KakaoService.java
│   │   ├── mapper/
│   │   │   └── UserMapper.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── KakaoMessage.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── UserCreateRequest.java
│   │   │   │   ├── UserUpdateRequest.java
│   │   │   │   ├── EmailRequest.java
│   │   │   │   └── KakaoMessageRequest.java
│   │   │   └── response/
│   │   │       ├── UserResponse.java
│   │   │       ├── ErrorResponse.java
│   │   │       ├── OAuth2UserInfo.java
│   │   │       └── KakaoMessageResponse.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── OAuth2UserService.java
│   │   │   ├── OAuth2SuccessHandler.java
│   │   │   └── JwtTokenProvider.java
│   │   ├── batch/
│   │   │   ├── job/
│   │   │   │   ├── UserDataMigrationJob.java
│   │   │   │   └── DailyReportJob.java
│   │   │   ├── step/
│   │   │   ├── reader/
│   │   │   │   └── UserItemReader.java
│   │   │   ├── processor/
│   │   │   │   └── UserItemProcessor.java
│   │   │   ├── writer/
│   │   │   │   └── UserItemWriter.java
│   │   │   └── listener/
│   │   │       └── JobCompletionListener.java
│   │   ├── messaging/
│   │   │   ├── producer/
│   │   │   │   └── MessageProducer.java
│   │   │   ├── consumer/
│   │   │   │   └── MessageConsumer.java
│   │   │   └── dto/
│   │   │       ├── EmailMessage.java
│   │   │       └── KakaoMessage.java
│   │   ├── kakao/
│   │   │   ├── client/
│   │   │   │   └── KakaoApiClient.java
│   │   │   ├── dto/
│   │   │   │   ├── AlimtalkRequest.java
│   │   │   │   ├── FriendtalkRequest.java
│   │   │   │   └── KakaoResponse.java
│   │   │   └── template/
│   │   │       └── KakaoTemplateCode.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── CustomException.java
│   │       └── KakaoApiException.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── mail-templates/
│       │   ├── welcome.html
│       │   └── notification.html
│       ├── kakao-templates/
│       │   ├── welcome.json
│       │   └── notification.json
│       └── mapper/
│           └── UserMapper.xml
└── test/
```

### 2. build.gradle.kts 구성
```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Spring Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")
    
    // Spring Security + OAuth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    
    // Spring Session JDBC
    implementation("org.springframework.session:spring-session-jdbc")
    
    // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    
    // Java Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // MyBatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // HTTP Client (Kakao API 호출)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // JSON Processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### 3. application.properties 설정
```properties
# Server
server.port=8080
server.servlet.context-path=/
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.force=true

# Application
spring.application.name=my-api-server
spring.profiles.active=dev

# DataSource
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=postgres

# HikariCP
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# MyBatis
mybatis.mapper-locations=classpath:mapper/**/*.xml
mybatis.type-aliases-package=com.example.api.entity
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.jdbc-type-for-null=NULL
mybatis.configuration.call-setters-on-nulls=true
mybatis.type-handlers-package=com.example.api.typehandler

# Spring Batch
spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always

# Spring Session JDBC
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
spring.session.jdbc.table-name=SPRING_SESSION
spring.session.timeout=1800s

# Security
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
spring.security.oauth2.client.registration.github.scope=read:user,user:email
spring.security.oauth2.client.registration.github.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

spring.security.oauth2.client.registration.kakao.client-id=YOUR_KAKAO_CLIENT_ID
spring.security.oauth2.client.registration.kakao.client-secret=YOUR_KAKAO_CLIENT_SECRET
spring.security.oauth2.client.registration.kakao.client-authentication-method=client_secret_post
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.kakao.scope=profile_nickname,account_email

spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
spring.security.oauth2.client.provider.kakao.user-name-attribute=id

# JWT
jwt.secret=your-256-bit-secret-key-here-please-change-this-in-production
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/

# RabbitMQ Queue Configuration
rabbitmq.queue.email=email.queue
rabbitmq.queue.notification=notification.queue
rabbitmq.queue.kakao=kakao.queue
rabbitmq.exchange=api.exchange
rabbitmq.routing-key.email=email.routing.key
rabbitmq.routing-key.notification=notification.routing.key
rabbitmq.routing-key.kakao=kakao.routing.key

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Mail Configuration
mail.from.address=noreply@example.com
mail.from.name=My API Server

# Kakao Message API
kakao.api.base-url=https://kapi.kakao.com
kakao.api.admin-key=YOUR_KAKAO_ADMIN_KEY
kakao.api.sender-key=YOUR_KAKAO_SENDER_KEY
kakao.api.alimtalk-url=${kakao.api.base-url}/v1/api/talk/friends/message/default/send
kakao.api.friendtalk-url=${kakao.api.base-url}/v1/api/talk/friends/message/default/send
kakao.api.timeout=5000

# Kakao Business Message (알림톡)
kakao.bizmessage.base-url=https://api.solapi.com
kakao.bizmessage.api-key=YOUR_SOLAPI_API_KEY
kakao.bizmessage.api-secret=YOUR_SOLAPI_API_SECRET
kakao.bizmessage.sender-key=YOUR_BIZMESSAGE_SENDER_KEY

# Logging
logging.level.root=INFO
logging.level.com.example.api=DEBUG
logging.level.com.example.api.mapper=DEBUG
logging.level.com.example.api.kakao=DEBUG
logging.level.org.springframework.jdbc=DEBUG
logging.level.org.springframework.batch=INFO
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.amqp=DEBUG
logging.level.org.springframework.web.reactive.function.client=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30
```

### 4. application-dev.properties
```properties
# DataSource
spring.datasource.url=jdbc:postgresql://localhost:5432/railway
spring.datasource.username=postgres
spring.datasource.password=postgres

# Security (개발 환경)
spring.security.oauth2.client.registration.google.client-id=dev-google-client-id
spring.security.oauth2.client.registration.google.client-secret=dev-google-secret

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Mail (개발 환경 - 콘솔 출력)
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false

# Kakao (개발 환경)
kakao.api.admin-key=dev-kakao-admin-key
kakao.bizmessage.api-key=dev-bizmessage-api-key

# Logging
logging.level.root=INFO
logging.level.com.example.api=DEBUG
logging.level.com.example.api.kakao=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.batch=DEBUG
```

### 5. application-prod.properties
```properties
# DataSource
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20

# Spring Batch
spring.batch.jdbc.initialize-schema=never

# Spring Session
spring.session.timeout=3600s

# Security (환경변수)
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
spring.security.oauth2.client.registration.kakao.client-id=${KAKAO_CLIENT_ID}
spring.security.oauth2.client.registration.kakao.client-secret=${KAKAO_CLIENT_SECRET}

# JWT
jwt.secret=${JWT_SECRET}

# RabbitMQ
spring.rabbitmq.host=${RABBITMQ_HOST}
spring.rabbitmq.port=${RABBITMQ_PORT}
spring.rabbitmq.username=${RABBITMQ_USERNAME}
spring.rabbitmq.password=${RABBITMQ_PASSWORD}

# Mail
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# Kakao
kakao.api.admin-key=${KAKAO_ADMIN_KEY}
kakao.api.sender-key=${KAKAO_SENDER_KEY}
kakao.bizmessage.api-key=${BIZMESSAGE_API_KEY}
kakao.bizmessage.api-secret=${BIZMESSAGE_API_SECRET}
kakao.bizmessage.sender-key=${BIZMESSAGE_SENDER_KEY}

# Logging
logging.level.root=WARN
logging.level.com.example.api=INFO
logging.level.org.springframework.batch=WARN
logging.level.org.springframework.security=WARN
```

### 6. DB 테이블 스키마
```sql
-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255),
    name VARCHAR(100),
    provider VARCHAR(20),
    provider_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_provider ON users(provider, provider_id);

-- 카카오톡 메시지 발송 로그
CREATE TABLE kakao_message_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    message_type VARCHAR(20) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    template_code VARCHAR(50),
    subject VARCHAR(200),
    content TEXT,
    button_data JSONB,
    status VARCHAR(20) DEFAULT 'PENDING',
    error_code VARCHAR(50),
    error_message TEXT,
    request_id VARCHAR(100),
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_kakao_log_user_id ON kakao_message_log(user_id);
CREATE INDEX idx_kakao_log_status ON kakao_message_log(status);
CREATE INDEX idx_kakao_log_sent_at ON kakao_message_log(sent_at);
CREATE INDEX idx_kakao_log_request_id ON kakao_message_log(request_id);

-- Spring Session 테이블
CREATE TABLE SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BYTEA NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) 
        REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);

-- Batch 처리용 임시 테이블
CREATE TABLE user_import_temp (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    name VARCHAR(100),
    import_status VARCHAR(20) DEFAULT 'PENDING',
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Batch 처리 결과 로그 테이블
CREATE TABLE batch_job_log (
    id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100),
    job_execution_id BIGINT,
    status VARCHAR(20),
    total_count INT,
    success_count INT,
    fail_count INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    error_message TEXT
);

-- 이메일 발송 로그 테이블
CREATE TABLE email_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    recipient VARCHAR(100) NOT NULL,
    subject VARCHAR(200),
    status VARCHAR(20),
    error_message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

### 7. Kakao Config

**KakaoConfig.java**
```java
@Configuration
@Slf4j
public class KakaoConfig {
    
    @Value("${kakao.api.base-url}")
    private String baseUrl;
    
    @Value("${kakao.api.timeout}")
    private int timeout;
    
    @Bean
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(timeout))
                ))
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.debug("Kakao API Request: {} {}", 
                                    clientRequest.method(), 
                                    clientRequest.url());
                            return Mono.just(clientRequest);
                        }
                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        clientResponse -> {
                            log.debug("Kakao API Response: {}", 
                                    clientResponse.statusCode());
                            return Mono.just(clientResponse);
                        }
                ))
                .build();
    }
}
```

### 8. Kakao API Client

**KakaoApiClient.java**
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClient {
    
    private final WebClient kakaoWebClient;
    
    @Value("${kakao.api.admin-key}")
    private String adminKey;
    
    @Value("${kakao.api.sender-key}")
    private String senderKey;
    
    @Value("${kakao.bizmessage.api-key}")
    private String bizApiKey;
    
    @Value("${kakao.bizmessage.api-secret}")
    private String bizApiSecret;
    
    /**
     * 카카오 친구톡 발송 (카카오톡 채널 친구에게만 발송 가능)
     */
    public Mono<KakaoResponse> sendFriendtalk(FriendtalkRequest request) {
        return kakaoWebClient.post()
                .uri("/v1/api/talk/friends/message/default/send")
                .header("Authorization", "KakaoAK " + adminKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Friendtalk API Error: {}", body);
                                    return Mono.error(new KakaoApiException(
                                            "Failed to send friendtalk: " + body));
                                })
                )
                .bodyToMono(KakaoResponse.class)
                .doOnSuccess(response -> 
                        log.info("Friendtalk sent successfully: {}", response))
                .doOnError(error -> 
                        log.error("Failed to send friendtalk", error));
    }
    
    /**
     * 카카오 알림톡 발송 (사전 승인된 템플릿으로만 발송 가능)
     * Solapi(구 Coolsms) 등의 대행사 API 사용
     */
    public Mono<KakaoResponse> sendAlimtalk(AlimtalkRequest request) {
        // Solapi 기준 예시
        String authHeader = generateSolapiAuthHeader();
        
        return WebClient.create("https://api.solapi.com")
                .post()
                .uri("/kakao/v1/alimtalk/send")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Alimtalk API Error: {}", body);
                                    return Mono.error(new KakaoApiException(
                                            "Failed to send alimtalk: " + body));
                                })
                )
                .bodyToMono(KakaoResponse.class)
                .doOnSuccess(response -> 
                        log.info("Alimtalk sent successfully: {}", response))
                .doOnError(error -> 
                        log.error("Failed to send alimtalk", error));
    }
    
    /**
     * Solapi 인증 헤더 생성
     */
    private String generateSolapiAuthHeader() {
        try {
            String salt = UUID.randomUUID().toString().replaceAll("-", "");
            String date = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ISO_INSTANT);
            
            String signature = getSignature(date, salt);
            
            return String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    bizApiKey, date, salt, signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate auth header", e);
        }
    }
    
    private String getSignature(String date, String salt) throws Exception {
        String message = date + salt;
        
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                bizApiSecret.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256");
        mac.init(secretKey);
        
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(rawHmac);
    }
}
```

### 9. Kakao DTO

**AlimtalkRequest.java**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimtalkRequest {
    
    @JsonProperty("to")
    private String recipient;  // 수신자 전화번호 (01012345678 형식)
    
    @JsonProperty("from")
    private String sender;  // 발신자 전화번호
    
    @JsonProperty("kakaoOptions")
    private KakaoOptions kakaoOptions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoOptions {
        
        @JsonProperty("pfId")
        private String senderKey;  // 발신 프로필 키
        
        @JsonProperty("templateId")
        private String templateCode;  // 템플릿 코드
        
        @JsonProperty("variables")
        private Map<String, String> variables;  // 템플릿 치환 변수
        
        @JsonProperty("buttons")
        private List<Button> buttons;  // 버튼 정보
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Button {
        
        @JsonProperty("buttonType")
        private String type;  // WL(웹링크), AL(앱링크), DS(배송조회), BK(봇키워드), MD(메시지전달)
        
        @JsonProperty("buttonName")
        private String name;  // 버튼명
        
        @JsonProperty("linkMo")
        private String linkMobile;  // 모바일 웹 링크
        
        @JsonProperty("linkPc")
        private String linkPc;  // PC 웹 링크
        
        @JsonProperty("linkAnd")
        private String linkAndroid;  // 안드로이드 앱 링크
        
        @JsonProperty("linkIos")
        private String linkIos;  // iOS 앱 링크
    }
}
```

**FriendtalkRequest.java**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendtalkRequest {
    
    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;  // 수신자 UUID 목록
    
    @JsonProperty("template_object")
    private TemplateObject templateObject;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateObject {
        
        @JsonProperty("object_type")
        private String objectType;  // "text", "list", "commerce" 등
        
        @JsonProperty("text")
        private String text;  // 메시지 내용
        
        @JsonProperty("link")
        private Link link;
        
        @JsonProperty("button_title")
        private String buttonTitle;  // 버튼 제목
        
        @JsonProperty("buttons")
        private List<Button> buttons;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {
        
        @JsonProperty("web_url")
        private String webUrl;
        
        @JsonProperty("mobile_web_url")
        private String mobileWebUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Button {
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("link")
        private Link link;
    }
}
```

**KakaoResponse.java**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoResponse {
    
    @JsonProperty("successful_receiver_uuids")
    private List<String> successfulReceiverUuids;
    
    @JsonProperty("groupId")
    private String groupId;
    
    @JsonProperty("count")
    private Integer count;
    
    @JsonProperty("statusCode")
    private String statusCode;
    
    @JsonProperty("statusMessage")
    private String statusMessage;
    
    @JsonProperty("requestId")
    private String requestId;
    
    @JsonProperty("accountId")
    private String accountId;
}
```

**KakaoMessageRequest.java**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoMessageRequest {
    
    @NotBlank
    private String recipient;  // 수신자 전화번호
    
    @NotBlank
    private String messageType;  // ALIMTALK, FRIENDTALK
    
    private String templateCode;  // 알림톡 템플릿 코드
    
    @NotBlank
    private String content;  // 메시지 내용
    
    private String subject;  // 알림톡 제목
    
    private Map<String, String> variables;  // 템플릿 치환 변수
    
    private List<ButtonDto> buttons;  // 버튼 정보
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ButtonDto {
        private String type;
        private String name;
        private String linkMobile;
        private String linkPc;
    }
}
```

### 10. Kakao Service

**KakaoService.java**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {
    
    private final KakaoApiClient kakaoApiClient;
    private final MessageProducer messageProducer;
    
    @Value("${kakao.api.sender-key}")
    private String senderKey;
    
    /**
     * 알림톡 발송 (동기)
     */
    public KakaoResponse sendAlimtalk(String recipient, String templateCode, 
                                      Map<String, String> variables, 
                                      List<AlimtalkRequest.Button> buttons) {
        
        AlimtalkRequest.KakaoOptions kakaoOptions = AlimtalkRequest.KakaoOptions.builder()
                .senderKey(senderKey)
                .templateCode(templateCode)
                .variables(variables)
                .buttons(buttons)
                .build();
        
        AlimtalkRequest request = AlimtalkRequest.builder()
                .recipient(recipient)
                .kakaoOptions(kakaoOptions)
                .build();
        
        try {
            KakaoResponse response = kakaoApiClient.sendAlimtalk(request).block();
            
            // 발송 로그 저장
            saveKakaoLog(recipient, "ALIMTALK", templateCode, 
                        "SUCCESS", response.getRequestId());
            
            return response;
        } catch (Exception e) {
            log.error("Failed to send alimtalk to {}", recipient, e);
            saveKakaoLog(recipient, "ALIMTALK", templateCode, 
                        "FAILED", null);
            throw new RuntimeException("Failed to send alimtalk", e);
        }
    }
    
    /**
     * 알림톡 발송 (비동기 - RabbitMQ)
     */
    public void sendAlimtalkAsync(String recipient, String templateCode, 
                                   Map<String, String> variables, 
                                   List<AlimtalkRequest.Button> buttons) {
        
        KakaoMessage message = KakaoMessage.builder()
                .recipient(recipient)
                .messageType("ALIMTALK")
                .templateCode(templateCode)
                .variables(variables)
                .buttons(convertToButtonDto(buttons))
                .build();
        
        messageProducer.sendKakaoMessage(message);
    }
    
    /**
     * 친구톡 발송 (동기)
     */
    public KakaoResponse sendFriendtalk(List<String> receiverUuids, String text, 
                                        String buttonTitle, String webUrl) {
        
        FriendtalkRequest.Link link = FriendtalkRequest.Link.builder()
                .webUrl(webUrl)
                .mobileWebUrl(webUrl)
                .build();
        
        FriendtalkRequest.TemplateObject templateObject = 
                FriendtalkRequest.TemplateObject.builder()
                .objectType("text")
                .text(text)
                .link(link)
                .buttonTitle(buttonTitle)
                .build();
        
        FriendtalkRequest request = FriendtalkRequest.builder()
                .receiverUuids(receiverUuids)
                .templateObject(templateObject)
                .build();
        
        try {
            KakaoResponse response = kakaoApiClient.sendFriendtalk(request).block();
            
            // 발송 로그 저장
            receiverUuids.forEach(uuid -> 
                    saveKakaoLog(uuid, "FRIENDTALK", null, 
                                "SUCCESS", response.getRequestId()));
            
            return response;
        } catch (Exception e) {
            log.error("Failed to send friendtalk", e);
            receiverUuids.forEach(uuid -> 
                    saveKakaoLog(uuid, "FRIENDTALK", null, "FAILED", null));
            throw new RuntimeException("Failed to send friendtalk", e);
        }
    }
    
    /**
     * 친구톡 발송 (비동기)
     */
    public void sendFriendtalkAsync(List<String> receiverUuids, String text, 
                                     String buttonTitle, String webUrl) {
        
        KakaoMessage message = KakaoMessage.builder()
                .receiverUuids(receiverUuids)
                .messageType("FRIENDTALK")
                .content(text)
                .buttonTitle(buttonTitle)
                .webUrl(webUrl)
                .build();
        
        messageProducer.sendKakaoMessage(message);
    }
    
    /**
     * 회원가입 환영 알림톡 발송
     */
    public void sendWelcomeAlimtalk(String phone, String name) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("date", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        
        List<AlimtalkRequest.Button> buttons = Arrays.asList(
                AlimtalkRequest.Button.builder()
                        .type("WL")
                        .name("서비스 시작하기")
                        .linkMobile("https://example.com/app")
                        .linkPc("https://example.com/web")
                        .build()
        );
        
        sendAlimtalkAsync(phone, "WELCOME_TEMPLATE", variables, buttons);
    }
    
    /**
     * 주문 완료 알림톡 발송
     */
    public void sendOrderConfirmationAlimtalk(String phone, String orderNumber, 
                                              String productName, int amount) {
        Map<String, String> variables = new HashMap<>();
        variables.put("orderNumber", orderNumber);
        variables.put("productName", productName);
        variables.put("amount", String.format("%,d", amount));
        
        List<AlimtalkRequest.Button> buttons = Arrays.asList(
                AlimtalkRequest.Button.builder()
                        .type("WL")
                        .name("주문 상세보기")
                        .linkMobile("https://example.com/order/" + orderNumber)
                        .linkPc("https://example.com/order/" + orderNumber)
                        .build(),
                AlimtalkRequest.Button.builder()
                        .type("DS")
                        .name("배송 조회")
                        .build()
        );
        
        sendAlimtalkAsync(phone, "ORDER_CONFIRMATION_TEMPLATE", variables, buttons);
    }
    
    private void saveKakaoLog(String recipient, String messageType, 
                             String templateCode, String status, String requestId) {
        // DB에 발송 로그 저장 (MyBatis 사용)
        log.info("Saved kakao log - recipient: {}, type: {}, status: {}", 
                recipient, messageType, status);
    }
    
    private List<KakaoMessage.ButtonDto> convertToButtonDto(
            List<AlimtalkRequest.Button> buttons) {
        if (buttons == null) return null;
        
        return buttons.stream()
                .map(btn -> KakaoMessage.ButtonDto.builder()
                        .type(btn.getType())
                        .name(btn.getName())
                        .linkMobile(btn.getLinkMobile())
                        .linkPc(btn.getLinkPc())
                        .build())
                .collect(Collectors.toList());
    }
}
```

### 11. RabbitMQ Config 확장

**RabbitMqConfig.java** (카카오 큐 추가)
```java
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    
    @Value("${rabbitmq.queue.email}")
    private String emailQueue;
    
    @Value("${rabbitmq.queue.notification}")
    private String notificationQueue;
    
    @Value("${rabbitmq.queue.kakao}")
    private String kakaoQueue;
    
    @Value("${rabbitmq.exchange}")
    private String exchange;
    
    @Value("${rabbitmq.routing-key.email}")
    private String emailRoutingKey;
    
    @Value("${rabbitmq.routing-key.notification}")
    private String notificationRoutingKey;
    
    @Value("${rabbitmq.routing-key.kakao}")
    private String kakaoRoutingKey;
    
    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue, true);
    }
    
    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue, true);
    }
    
    @Bean
    public Queue kakaoQueue() {
        return new Queue(kakaoQueue, true);
    }
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }
    
    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(exchange())
                .with(emailRoutingKey);
    }
    
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(exchange())
                .with(notificationRoutingKey);
    }
    
    @Bean
    public Binding kakaoBinding() {
        return BindingBuilder
                .bind(kakaoQueue())
                .to(exchange())
                .with(kakaoRoutingKey);
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
```

### 12. Message Producer 확장

**MessageProducer.java** (카카오 메시지 추가)
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange}")
    private String exchange;
    
    @Value("${rabbitmq.routing-key.email}")
    private String emailRoutingKey;
    
    @Value("${rabbitmq.routing-key.notification}")
    private String notificationRoutingKey;
    
    @Value("${rabbitmq.routing-key.kakao}")
    private String kakaoRoutingKey;
    
    public void sendEmailMessage(EmailMessage message) {
        log.info("Sending email message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, message);
    }
    
    public void sendNotificationMessage(String message) {
        log.info("Sending notification message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, message);
    }
    
    public void sendKakaoMessage(KakaoMessage message) {
        log.info("Sending kakao message to queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, kakaoRoutingKey, message);
    }
}
```

### 13. Message Consumer 확장

**MessageConsumer.java** (카카오 메시지 처리 추가)
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {
    
    private final EmailService emailService;
    private final KakaoApiClient kakaoApiClient;
    
    @Value("${kakao.api.sender-key}")
    private String senderKey;
    
    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void receiveEmailMessage(EmailMessage message) {
        log.info("Received email message from queue: {}", message);
        try {
            emailService.sendEmail(
                    message.getRecipient(),
                    message.getSubject(),
                    message.getContent()
            );
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }
    
    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void receiveNotificationMessage(String message) {
        log.info("Received notification message from queue: {}", message);
    }
    
    @RabbitListener(queues = "${rabbitmq.queue.kakao}")
    public void receiveKakaoMessage(KakaoMessage message) {
        log.info("Received kakao message from queue: {}", message);
        try {
            if ("ALIMTALK".equals(message.getMessageType())) {
                sendAlimtalkFromQueue(message);
            } else if ("FRIENDTALK".equals(message.getMessageType())) {
                sendFriendtalkFromQueue(message);
            }
        } catch (Exception e) {
            log.error("Failed to send kakao message", e);
        }
    }
    
    private void sendAlimtalkFromQueue(KakaoMessage message) {
        List<AlimtalkRequest.Button> buttons = null;
        if (message.getButtons() != null) {
            buttons = message.getButtons().stream()
                    .map(btn -> AlimtalkRequest.Button.builder()
                            .type(btn.getType())
                            .name(btn.getName())
                            .linkMobile(btn.getLinkMobile())
                            .linkPc(btn.getLinkPc())
                            .build())
                    .collect(Collectors.toList());
        }
        
        AlimtalkRequest.KakaoOptions kakaoOptions = 
                AlimtalkRequest.KakaoOptions.builder()
                .senderKey(senderKey)
                .templateCode(message.getTemplateCode())
                .variables(message.getVariables())
                .buttons(buttons)
                .build();
        
        AlimtalkRequest request = AlimtalkRequest.builder()
                .recipient(message.getRecipient())
                .kakaoOptions(kakaoOptions)
                .build();
        
        kakaoApiClient.sendAlimtalk(request)
                .doOnSuccess(response -> 
                        log.info("Alimtalk sent from queue: {}", response))
                .doOnError(error -> 
                        log.error("Failed to send alimtalk from queue", error))
                .subscribe();
    }
    
    private void sendFriendtalkFromQueue(KakaoMessage message) {
        FriendtalkRequest.Link link = FriendtalkRequest.Link.builder()
                .webUrl(message.getWebUrl())
                .mobileWebUrl(message.getWebUrl())
                .build();
        
        FriendtalkRequest.TemplateObject templateObject = 
                FriendtalkRequest.TemplateObject.builder()
                .objectType("text")
                .text(message.getContent())
                .link(link)
                .buttonTitle(message.getButtonTitle())
                .build();
        
        FriendtalkRequest request = FriendtalkRequest.builder()
                .receiverUuids(message.getReceiverUuids())
                .templateObject(templateObject)
                .build();
        
        kakaoApiClient.sendFriendtalk(request)
                .doOnSuccess(response -> 
                        log.info("Friendtalk sent from queue: {}", response))
                .doOnError(error -> 
                        log.error("Failed to send friendtalk from queue", error))
                .subscribe();
    }
}
```

### 14. Kakao Message DTO

**KakaoMessage.java**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoMessage implements Serializable {
    
    private String recipient;  // 수신자 전화번호 (알림톡)
    private List<String> receiverUuids;  // 수신자 UUID 목록 (친구톡)
    private String messageType;  // ALIMTALK, FRIENDTALK
    private String templateCode;  // 알림톡 템플릿 코드
    private String content;  // 메시지 내용
    private String subject;  // 제목
    private String buttonTitle;  // 버튼 제목 (친구톡)
    private String webUrl;  // 웹 링크 (친구톡)
    private Map<String, String> variables;  // 템플릿 치환 변수
    private List<ButtonDto> buttons;  // 버튼 정보
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ButtonDto implements Serializable {
        private String type;
        private String name;
        private String linkMobile;
        private String linkPc;
    }
}
```

### 15. Notification Controller

**NotificationController.java**
```java
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final KakaoService kakaoService;
    private final EmailService emailService;
    
    /**
     * 알림톡 발송
     */
    @PostMapping("/kakao/alimtalk")
    public ResponseEntity<?> sendAlimtalk(
            @Valid @RequestBody KakaoMessageRequest request) {
        
        try {
            List<AlimtalkRequest.Button> buttons = null;
            if (request.getButtons() != null) {
                buttons = request.getButtons().stream()
                        .map(btn -> AlimtalkRequest.Button.builder()
                                .type(btn.getType())
                                .name(btn.getName())
                                .linkMobile(btn.getLinkMobile())
                                .linkPc(btn.getLinkPc())
                                .build())
                        .collect(Collectors.toList());
            }
            
            KakaoResponse response = kakaoService.sendAlimtalk(
                    request.getRecipient(),
                    request.getTemplateCode(),
                    request.getVariables(),
                    buttons
            );
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", response.getRequestId()
            ));
        } catch (Exception e) {
            log.error("Failed to send alimtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 알림톡 비동기 발송
     */
    @PostMapping("/kakao/alimtalk/async")
    public ResponseEntity<?> sendAlimtalkAsync(
            @Valid @RequestBody KakaoMessageRequest request) {
        
        try {
            List<AlimtalkRequest.Button> buttons = null;
            if (request.getButtons() != null) {
                buttons = request.getButtons().stream()
                        .map(btn -> AlimtalkRequest.Button.builder()
                                .type(btn.getType())
                                .name(btn.getName())
                                .linkMobile(btn.getLinkMobile())
                                .linkPc(btn.getLinkPc())
                                .build())
                        .collect(Collectors.toList());
            }
            
            kakaoService.sendAlimtalkAsync(
                    request.getRecipient(),
                    request.getTemplateCode(),
                    request.getVariables(),
                    buttons
            );
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Alimtalk queued for sending"
            ));
        } catch (Exception e) {
            log.error("Failed to queue alimtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 친구톡 발송
     */
    @PostMapping("/kakao/friendtalk")
    public ResponseEntity<?> sendFriendtalk(
            @RequestParam List<String> receiverUuids,
            @RequestParam String text,
            @RequestParam(required = false) String buttonTitle,
            @RequestParam(required = false) String webUrl) {
        
        try {
            KakaoResponse response = kakaoService.sendFriendtalk(
                    receiverUuids, text, buttonTitle, webUrl);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", response.getRequestId()
            ));
        } catch (Exception e) {
            log.error("Failed to send friendtalk", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 멀티채널 알림 발송 (이메일 + 카카오톡)
     */
    @PostMapping("/multi-channel")
    public ResponseEntity<?> sendMultiChannelNotification(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String name,
            @RequestParam String subject,
            @RequestParam String content) {
        
        try {
            // 이메일 발송
            emailService.sendEmailAsync(email, subject, content);
            
            // 카카오톡 알림톡 발송
            Map<String, String> variables = new HashMap<>();
            variables.put("name", name);
            variables.put("content", content);
            
            kakaoService.sendAlimtalkAsync(
                    phone, 
                    "NOTIFICATION_TEMPLATE", 
                    variables, 
                    null
            );
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Multi-channel notification queued"
            ));
        } catch (Exception e) {
            log.error("Failed to send multi-channel notification", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
```

### 16. Kakao Exception

**KakaoApiException.java**
```java
public class KakaoApiException extends RuntimeException {
    public KakaoApiException(String message) {
        super(message);
    }
    
    public KakaoApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 17. Kakao Template Code (Enum)

**KakaoTemplateCode.java**
```java
@Getter
@RequiredArgsConstructor
public enum KakaoTemplateCode {
    
    WELCOME_TEMPLATE("WELCOME_001", "회원가입 환영 메시지"),
    ORDER_CONFIRMATION_TEMPLATE("ORDER_001", "주문 확인 메시지"),
    PAYMENT_CONFIRMATION_TEMPLATE("PAYMENT_001", "결제 완료 메시지"),
    SHIPPING_NOTIFICATION_TEMPLATE("SHIPPING_001", "배송 시작 알림"),
    DELIVERY_COMPLETE_TEMPLATE("DELIVERY_001", "배송 완료 알림"),
    PASSWORD_RESET_TEMPLATE("PASSWORD_001", "비밀번호 재설정"),
    NOTIFICATION_TEMPLATE("NOTIFICATION_001", "일반 알림");
    
    private final String code;
    private final String description;
}
```

### 18. Kakao 템플릿 JSON 예시

**resources/kakao-templates/welcome.json**
```json
{
  "template_code": "WELCOME_001",
  "template_name": "회원가입 환영",
  "content": "안녕하세요, #{name}님!\n\n#{appName}에 가입해주셔서 감사합니다.\n가입일: #{date}\n\n지금 바로 서비스를 시작해보세요!",
  "buttons": [
    {
      "type": "WL",
      "name": "서비스 시작하기",
      "link_mobile": "https://example.com/app",
      "link_pc": "https://example.com/web"
    }
  ],
  "variables": {
    "name": "사용자 이름",
    "appName": "서비스 이름",
    "date": "가입 날짜"
  }
}
```

**resources/kakao-templates/notification.json**
```json
{
  "template_code": "NOTIFICATION_001",
  "template_name": "일반 알림",
  "content": "#{name}님께 알림이 도착했습니다.\n\n#{content}\n\n확인 부탁드립니다.",
  "buttons": [
    {
      "type": "WL",
      "name": "자세히 보기",
      "link_mobile": "https://example.com/notifications",
      "link_pc": "https://example.com/notifications"
    }
  ],
  "variables": {
    "name": "사용자 이름",
    "content": "알림 내용"
  }
}
```

### 19. API 테스트

#### 알림톡 발송 (동기)
```bash
curl -X POST http://localhost:8080/api/notifications/kakao/alimtalk \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "01012345678",
    "messageType": "ALIMTALK",
    "templateCode": "WELCOME_001",
    "variables": {
      "name": "홍길동",
      "appName": "My API Server",
      "date": "2024년 2월 9일"
    },
    "buttons": [
      {
        "type": "WL",
        "name": "서비스 시작하기",
        "linkMobile": "https://example.com/app",
        "linkPc": "https://example.com/web"
      }
    ]
  }'
```

#### 알림톡 발송 (비동기)
```bash
curl -X POST http://localhost:8080/api/notifications/kakao/alimtalk/async \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "01012345678",
    "messageType": "ALIMTALK",
    "templateCode": "ORDER_001",
    "variables": {
      "orderNumber": "ORD20240209001",
      "productName": "상품명",
      "amount": "50000"
    }
  }'
```

#### 멀티채널 알림 발송
```bash
curl -X POST "http://localhost:8080/api/notifications/multi-channel" \
  -d "email=user@example.com" \
  -d "phone=01012345678" \
  -d "name=홍길동" \
  -d "subject=중요 공지사항" \
  -d "content=서비스 점검 안내입니다."
```

### 20. 카카오 API 설정 가이드

#### 20.1 카카오 개발자 센터 설정
1. https://developers.kakao.com 접속
2. 내 애플리케이션 > 애플리케이션 추가하기
3. 앱 키 > REST API 키 복사 → `kakao.api.admin-key`에 설정
4. 카카오톡 채널 > 카카오톡 채널 추가
5. 발신 프로필 키 복사 → `kakao.api.sender-key`에 설정

#### 20.2 알림톡 사용 (대행사 필요)
- Solapi (구 Coolsms): https://solapi.com
- Bizppurio: https://www.bizppurio.com
- 카카오 비즈메시지: https://center-pf.kakao.com

1. 대행사 가입 및 API 키 발급
2. 발신 프로필 등록 (사업자 인증 필요)
3. 템플릿 등록 및 승인
4. API 키를 properties에 설정

---

## 카카오톡 발송 관련 주의사항

### 1. 알림톡 vs 친구톡
- **알림톡**: 사전 등록된 템플릿으로만 발송, 광고 아님, 사업자 인증 필요
- **친구톡**: 카카오톡 채널 친구에게만 발송, 자유로운 메시지, 광고 가능

### 2. 템플릿 등록
- 알림톡은 반드시 템플릿 사전 등록 및 승인 필요
- 변수는 #{변수명} 형식으로 사용
- 버튼은 최대 5개까지 등록 가능

### 3. 발송 제한
- 1일 발송량 제한 확인 (대행사별 상이)
- 수신거부 처리 필수
- 야간 발송 제한 (21:00 ~ 08:00)

### 4. 비용
- 알림톡: 건당 6~9원
- 친구톡: 건당 15원
- LMS 대체발송: 건당 20원

이제 카카오톡 알림톡/친구톡 발송 기능까지 완벽하게 통합된 엔터프라이즈급 Spring Boot 서버 가이드입니다!

## Claude Code 사용 시 프롬프트 예시

"위 가이드에 따라 MyBatis, Spring Batch, OAuth2, Spring Session JDBC, RabbitMQ, Java Mail Sender,
--------------------------------
카카오톡 알림톡/친구톡 발송 기능을 포함한 풀스택 Spring Boot REST API 서버를 생성해주세요.
--------------------------------
프로젝트 구조와 build.gradle.kts를 먼저 만들고,
--------------------------------
application.properties 설정 파일들을 생성한 후,
--------------------------------
모든 설정 클래스들과 기본 API, 배치 Job, 메시징, 이메일, 카카오톡 발송 기능을 구현해주세요."
--------------------------------