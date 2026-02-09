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
