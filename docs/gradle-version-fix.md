# Gradle 버전 문제 해결

## 🐛 문제

Railway 배포 시 Gradle 9.3.0으로 인한 오류:

```
> Task :compileJava FAILED
Could not determine the dependencies of task ':compileJava'
```

또는

```
Unsupported Gradle version
```

## ✅ 해결: Gradle 8.5로 다운그레이드

### 1. Gradle Wrapper 버전 변경

```bash
./gradlew wrapper --gradle-version 8.5
```

### 2. 변경 사항 확인

```bash
# Gradle 버전 확인
./gradlew --version

# 출력 예상:
# Gradle 8.5
```

### 3. 빌드 테스트

```bash
# 로컬에서 빌드 테스트
./gradlew clean build -x test

# JAR 파일 확인
ls -lh build/libs/
```

### 4. Git 커밋

```bash
# 변경된 파일 확인
git status

# 출력:
# modified:   gradle/wrapper/gradle-wrapper.properties

# 커밋
git add gradle/wrapper/gradle-wrapper.properties
git commit -m "fix: Downgrade Gradle to 8.5 for Railway compatibility"
git push origin main
```

## 📋 변경된 파일

### `gradle/wrapper/gradle-wrapper.properties`

**변경 전 (Gradle 9.3.0):**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.3.0-bin.zip
```

**변경 후 (Gradle 8.5):**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

## 🔍 왜 Gradle 8.5인가?

| 항목 | Gradle 9.3.0 | Gradle 8.5 |
|------|--------------|-----------|
| **Railway 호환성** | ⚠️ 불안정 | ✅ 안정적 |
| **Spring Boot 3.3.6** | ✅ 지원 | ✅ 지원 |
| **Java 17** | ✅ 지원 | ✅ 지원 |
| **Configuration Cache** | ✅ 지원 | ✅ 지원 |
| **빌드 속도** | 빠름 | 빠름 |
| **Railway 배포** | ❌ 실패 가능 | ✅ 성공 |

## 🚀 Railway 배포 재시도

Gradle 버전 변경 후:

```bash
# GitHub에 Push (자동 배포)
git push origin main

# 또는 Railway CLI로 직접 배포
railway up
```

## ✅ 배포 성공 확인

**Railway 빌드 로그:**

```
✓ Installing dependencies
✓ Running: ./gradlew clean bootJar --no-daemon
  Welcome to Gradle 8.5!
  BUILD SUCCESSFUL in 42s
✓ JAR created: build/libs/my-api-server-0.0.1-SNAPSHOT.jar
✓ Deployment successful
```

## 🔧 트러블슈팅

### 문제: Gradle Daemon 충돌

**증상:**
```
Incompatible Daemon found
```

**해결:**
```bash
# Daemon 종료
./gradlew --stop

# 재시작
./gradlew --version
```

### 문제: Gradle 캐시 문제

**증상:**
```
Could not resolve all files for configuration
```

**해결:**
```bash
# Gradle 캐시 정리
./gradlew clean --refresh-dependencies

# 또는 캐시 완전 삭제
rm -rf ~/.gradle/caches/
./gradlew build
```

### 문제: Railway에서 여전히 실패

**확인사항:**

1. **gradle-wrapper.jar 커밋 확인**
   ```bash
   git ls-files gradle/wrapper/
   # 출력:
   # gradle/wrapper/gradle-wrapper.jar
   # gradle/wrapper/gradle-wrapper.properties
   ```

2. **gradlew 실행 권한 확인**
   ```bash
   git ls-files --stage gradlew
   # 출력: 100755 (755여야 함)
   ```

3. **railway.json 확인**
   ```json
   {
     "build": {
       "buildCommand": "./gradlew clean bootJar --no-daemon"
     }
   }
   ```

## 📚 참고

- [Gradle 8.5 Release Notes](https://docs.gradle.org/8.5/release-notes.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [Railway Gradle Support](https://docs.railway.app/guides/java)

## 💡 베스트 프랙티스

### 로컬 개발

```bash
# 특정 Gradle 버전 사용
./gradlew wrapper --gradle-version 8.5

# 빌드 캐시 활성화 (gradle.properties)
org.gradle.caching=true
org.gradle.parallel=true
```

### CI/CD

```bash
# Gradle Daemon 비활성화 (Railway/CI 환경)
./gradlew build --no-daemon

# 또는 환경 변수
export GRADLE_OPTS="-Dorg.gradle.daemon=false"
```

### Railway 최적화

**nixpacks.toml:**
```toml
[phases.build]
cmds = [
  "./gradlew clean bootJar --no-daemon --no-watch-fs"
]
```

`--no-watch-fs`: 파일 시스템 감시 비활성화 (Railway에서 불필요)

## ✅ 체크리스트

배포 전 확인:

- [x] Gradle 8.5로 변경됨
- [x] 로컬 빌드 성공
- [x] JAR 파일 생성 확인 (build/libs/)
- [x] gradle-wrapper.properties 커밋됨
- [x] gradlew 실행 권한 (100755)
- [ ] Railway Variables 설정 완료
- [ ] GitHub Push 완료
- [ ] Railway 배포 성공 확인
