# 🔒 Git 히스토리에서 민감 정보 제거

## 문제 확인

Git 히스토리에 다음 민감 정보가 포함되어 있습니다:
- 데이터베이스 비밀번호
- OAuth2 클라이언트 시크릿

## 해결 방법

### Option 1: Git Filter-Repo 사용 (권장)

```bash
# 1. git-filter-repo 설치
pip install git-filter-repo

# 2. 백업 생성
git clone . ../my-api-server-backup

# 3. 민감한 파일의 특정 라인만 수정
git filter-repo --invert-paths --path-glob 'src/main/resources/application-dev.properties'
git filter-repo --invert-paths --path-glob 'src/main/resources/application.properties'

# 또는 파일 내용만 교체
git filter-repo --replace-text replacements.txt
```

**replacements.txt 내용:**
```
mGnCZTdilSEekfHOmavezeECINsUloMq==>***REMOVED***
dev-google-secret==>***REMOVED***
```

### Option 2: BFG Repo-Cleaner 사용

```bash
# 1. BFG 다운로드
# https://rtyley.github.io/bfg-repo-cleaner/

# 2. 실행
java -jar bfg.jar --replace-text passwords.txt

# 3. Git 정리
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

### Option 3: 새 저장소로 시작 (가장 간단)

리모트에 아직 푸시하지 않았다면:

```bash
# 1. 현재 커밋 취소
git reset --soft HEAD~2

# 2. 변경사항 확인 후 다시 커밋
git status
git add .
git commit -m "Initial commit with environment variables"
```

## 추가 보안 조치

### 1. Railway 데이터베이스 비밀번호 변경

**Railway Dashboard에서:**
1. Database 설정으로 이동
2. 비밀번호 재생성
3. 새 비밀번호를 `.env` 파일에 업데이트

### 2. .env 파일 업데이트

```bash
# .env 파일의 DB_PASSWORD 업데이트
DB_PASSWORD=새로운비밀번호
```

### 3. GitHub에 이미 푸시한 경우

```bash
# Force push (주의: 협업 중이라면 팀원들과 상의 필요)
git push origin --force --all
git push origin --force --tags
```

**⚠️ Force push 전에 팀원들에게 알리세요!**

## 예방 조치

✅ `.gitignore`에 `.env` 추가됨
✅ 환경 변수 사용하도록 변경됨
✅ `.env.example` 템플릿 생성됨

## GitHub Secret Scanning

GitHub가 자동으로 감지한 경우:
1. GitHub 알림 확인
2. 위 방법으로 히스토리 정리
3. 비밀번호/토큰 즉시 변경
4. GitHub에 "Revoked" 표시
