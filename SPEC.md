# SPEC.md

## 1. 프로젝트 개요

본 프로젝트는 고객사가 AI를 활용할 수 있도록 챗봇 API를 제공하는 서비스입니다. 사용자는 회원가입 및 로그인을 통해 챗봇과 대화할 수 있으며, 대화 내역에 대한 피드백을 남길 수 있습니다. 관리자는 사용자 활동 및 보고서를 조회할 수 있습니다.

## 2. 기술 스택

- **Language:** Kotlin 1.9 (JVM 21)
- **Framework:** Spring Boot 3
- **Database:** H2
- **Build:** Gradle (Kotlin DSL)
- **Architecture:** Hexagonal Architecture
- **Authentication:** JWT (JSON Web Token)

## 3. 도메인 설계 (Entity & Value Object)

### 3.1 사용자 (User)

- `id`: Long (Primary Key, Auto-increment)
- `email`: String (Unique)
- `password`: String (Encoded)
- `name`: String
- `role`: UserRole (MEMBER, ADMIN)
- `createdAt`: OffsetDateTime

### 3.2 스레드 (Thread)

- `id`: UUID (Primary Key)
- `userId`: Long (Foreign Key)
- `createdAt`: OffsetDateTime
- `lastActivityAt`: OffsetDateTime (스레드 유지 시간 판단 기준)

### 3.3 대화 (Chat)

- `id`: UUID (Primary Key)
- `threadId`: UUID (Foreign Key)
- `question`: String (텍스트)
- `answer`: String (AI 생성 답변)
- `model`: String (OpenAI 모델 명)
- `createdAt`: OffsetDateTime

### 3.4 피드백 (Feedback)

- `id`: UUID (Primary Key)
- `userId`: Long (Foreign Key)
- `chatId`: UUID (Foreign Key)
- `isPositive`: Boolean
- `status`: FeedbackStatus (PENDING, RESOLVED)
- `createdAt`: OffsetDateTime
- `updatedAt`: OffsetDateTime

## 4. API 목록

| 카테고리         | Method | Path                             | Description         | Access        | Parameters / Notes                             |
|:-------------|:-------|:---------------------------------|:--------------------|:--------------|:-----------------------------------------------|
| **Auth**     | POST   | `/api/v1/auth/signup`            | 회원가입                | PUBLIC        | 이메일, 패스워드, 이름                                  |
| **Auth**     | POST   | `/api/v1/auth/login`             | 로그인 (JWT 발급)        | PUBLIC        | 이메일, 패스워드                                      |
| **Auth**     | POST   | `/api/v1/auth/refresh`           | 토큰 재발급              | PUBLIC        | 리프레시 토큰 필요                                     |
| **Chat**     | POST   | `/api/v1/chats`                  | 대화 생성 (질문 전송)       | AUTHENTICATED | `question`, `isStreaming`(옵션), `model`(옵션)     |
| **Chat**     | GET    | `/api/v1/chats`                  | 대화 목록 조회 (스레드별 그룹화) | USER/ADMIN    | `page`, `size`, `sort`. MEMBER는 본인만, ADMIN은 전체 |
| **Chat**     | DELETE | `/api/v1/threads/{threadId}`     | 스레드 삭제              | OWNER         | 본인 소유의 스레드만 삭제 가능                              |
| **Feedback** | POST   | `/api/v1/feedbacks`              | 피드백 생성              | USER/ADMIN    | `chatId`, `isPositive`. 대화당 사용자별 1개 제한         |
| **Feedback** | GET    | `/api/v1/feedbacks`              | 피드백 목록 조회           | USER/ADMIN    | `page`, `size`, `sort`, `isPositive`(필터)       |
| **Feedback** | PATCH  | `/api/v1/feedbacks/{feedbackId}` | 피드백 상태 변경           | ADMIN         | `status` (PENDING, RESOLVED)                   |
| **Admin**    | GET    | `/api/v1/stats/activities`       | 사용자 활동 기록 조회        | ADMIN         | `period` (ISO 8601, 옵션, 기본값: P1D)              |
| **Admin**    | GET    | `/api/v1/reports/chats`          | 대화 목록 CSV 보고서 다운로드  | ADMIN         | `period` (ISO 8601, 옵션, 기본값: P1D)              |

### 4.1 인증 API (Auth)

사용자 가입 및 시스템 접근을 위한 토큰 발급을 담당합니다.

#### [POST] `/api/v1/auth/signup`

- **설명**: 신규 회원을 등록합니다.
- **요청 본문 (JSON)**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123!",
    "name": "홍길동"
  }
  ```
- **응답 본문 (JSON)**:
  ```json
  {
    "email": "user@example.com",
    "name": "홍길동",
    "role": "MEMBER",
    "createdAt": "2024-05-15T10:00:00+09:00"
  }
  ```

#### [POST] `/api/v1/auth/login`

- **설명**: 이메일과 비밀번호로 인증하고 액세스 및 리프레시 토큰을 발급받습니다.
- **요청 본문 (JSON)**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123!"
  }
  ```
- **응답 본문 (JSON)**:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 300,
    "refreshExpiresIn": 86400
  }
  ```

#### [POST] `/api/v1/auth/refresh`

- **설명**: 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.
- **요청 본문 (JSON)**:
  ```json
  {
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```
- **응답 본문 (JSON)**:
  ```json
  {
    "accessToken": "new_eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "new_eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 300,
    "refreshExpiresIn": 86400
  }
  ```

#### **공통 요청 헤더 (Authenticated APIs)**

회원가입과 로그인을 제외한 모든 API 요청 시 아래 헤더가 필수입니다.

- **Header**: `Authorization: Bearer {accessToken}`

### 4.2 대화 API (Chat)

질문을 전송하여 AI 답변을 생성하고, 이전 대화 내역을 관리합니다.

#### [POST] `/api/v1/chats`

- **설명**: 신규 질문을 전송하고 AI 답변을 생성합니다. (스레드 관리 로직 포함)
- **요청 본문 (JSON)**:
  ```json
  {
    "question": "오늘 서울 날씨는 어때?",
    "isStreaming": false,
    "model": "gpt-3.5-turbo"
  }
  ```
- **일반 응답 본문 (isStreaming: false)**:
  ```json
  {
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "threadId": "321e8400-e29b-41d4-a716-446655441111",
    "question": "오늘 서울 날씨는 어때?",
    "answer": "오늘 서울은 맑고 기온은 20도입니다.",
    "model": "gpt-3.5-turbo",
    "createdAt": "2024-05-15T10:05:00+09:00"
  }
  ```
- **스트리밍 응답 상세 (isStreaming: true)**:
  - **Content-Type**: `text/event-stream`
  - **규격**: SSE (Server-Sent Events)를 통해 텍스트 조각(Delta)을 실시간 전송합니다.
  - **스트림 데이터 예시**:
    ```http
    event: message
    data: {"chatId": "...", "content": "안녕", "isFirst": true}

    event: message
    data: {"content": "하세"}

    event: message
    data: {"content": "요!"}

    event: message
    data: {"content": "", "isLast": true, "createdAt": "..."}

    data: [DONE]
    ```

#### [GET] `/api/v1/chats`

- **설명**: 대화 목록을 스레드별로 그룹화하여 조회합니다. (페이징 및 정렬 지원)
- **쿼리 파라미터**:
  - `page`: 페이지 번호 (Default: 0)
  - `size`: 페이지당 개수 (Default: 20)
  - `sort`: 생성일시 정렬 방향 (`asc`, `desc`, Default: `desc`)
- **응답 본문 (JSON)**:
  ```json
  {
    "content": [
      {
        "threadId": "321e8400-e29b-41d4-a716-446655441111",
        "chats": [
          {
            "chatId": "550e8400-e29b-41d4-a716-446655440000",
            "question": "오늘 서울 날씨는 어때?",
            "answer": "오늘 서울은 맑고 기온은 20도입니다.",
            "createdAt": "2024-05-15T10:05:00+09:00"
          }
        ],
        "lastActivityAt": "2024-05-15T10:05:00+09:00"
      }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "size": 20,
    "number": 0
  }
  ```

#### [DELETE] `/api/v1/threads/{threadId}`

- **설명**: 특정 스레드와 해당 스레드에 속한 모든 대화 내역을 삭제합니다.
- **경로 변수**: `threadId` (삭제할 스레드의 UUID)
- **성공 응답**: `204 No Content` (응답 본문 없음)

### 4.3 피드백 API (Feedback)

특정 대화에 대한 사용자의 만족도를 기록하고 관리합니다.

#### [POST] `/api/v1/feedbacks`

- **설명**: 특정 대화(Chat)에 대한 피드백을 생성합니다.
- **요청 본문 (JSON)**:
  ```json
  {
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "isPositive": true
  }
  ```
- **응답 본문 (JSON)**:
  ```json
  {
    "feedbackId": "880e8400-e29b-41d4-a716-446655449999",
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "isPositive": true,
    "status": "PENDING",
    "createdAt": "2024-05-15T10:10:00+09:00"
  }
  ```

#### [GET] `/api/v1/feedbacks`

- **설명**: 피드백 목록을 조회합니다. (페이징, 정렬, 긍정/부정 필터 지원)
- **쿼리 파라미터**:
  - `page`: 페이지 번호 (Default: 0)
  - `size`: 페이지당 개수 (Default: 20)
  - `sort`: 생성일시 정렬 방향 (`asc`, `desc`, Default: `desc`)
  - `isPositive`: 긍정/부정 필터 (옵션)
- **응답 본문 (JSON)**:
  ```json
  {
    "content": [
      {
        "feedbackId": "880e8400-e29b-41d4-a716-446655449999",
        "chatId": "550e8400-e29b-41d4-a716-446655440000",
        "isPositive": true,
        "status": "PENDING",
        "createdAt": "2024-05-15T10:10:00+09:00"
      }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "size": 20,
    "number": 0
  }
  ```

#### [PATCH] `/api/v1/feedbacks/{feedbackId}`

- **설명**: 관리자가 피드백의 처리 상태를 업데이트합니다.
- **경로 변수**: `feedbackId` (상태를 변경할 피드백의 UUID)
- **요청 본문 (JSON)**:
  ```json
  {
    "status": "RESOLVED"
  }
  ```
- **응답 본문 (JSON)**:
  ```json
  {
    "feedbackId": "880e8400-e29b-41d4-a716-446655449999",
    "status": "RESOLVED",
    "createdAt": "2024-05-15T10:10:00+09:00",
    "updatedAt": "2024-05-15T10:15:00+09:00"
  }
  ```

### 4.4 분석 및 보고 API (Statistics & Report)

관리자가 서비스 운영 현황을 파악하고 대화 데이터를 추출할 수 있는 기능을 제공합니다.

#### [GET] `/api/v1/stats/activities`

- **설명**: 지정된 기간(ISO 8601) 동안의 주요 사용자 활동 지표를 조회합니다.
- **Access**: `ADMIN`
- **쿼리 파라미터**:
  - `period`: 조회 기간 (ISO 8601 Duration 포맷, 예: `P1D`, `PT12H`. 옵션, 기본값: `P1D`)
- **응답 본문 (JSON)**:
  ```json
  {
    "period": "P1D",
    "stats": {
      "signupCount": 15,
      "loginCount": 42,
      "chatCreationCount": 156
    },
    "generatedAt": "2024-05-15T11:00:00+09:00"
  }
  ```

#### [GET] `/api/v1/reports/chats`

- **설명**: 지정된 기간(ISO 8601) 동안 생성된 모든 대화 내역을 CSV 파일 형태로 다운로드합니다.
- **Access**: `ADMIN`
- **쿼리 파라미터**:
  - `period`: 조회 기간 (ISO 8601 Duration 포맷, 옵션, 기본값: `P1D`)
- **응답 헤더**:
  - `Content-Type`: `text/csv; charset=UTF-8`
  - `Content-Disposition`: `attachment; filename="chat_report_20240515.csv"`
- **CSV 데이터 구성 (Example)**:
  ```csv
  threadId,chatId,email,question,answer,model,createdAt
  321e8400-e29b-41d4-a716-446655441111,550e8400-e29b-41d4-a716-446655440000,user@example.com,오늘 날씨 어때?,맑음입니다,gpt-3.5-turbo,2024-05-15T10:05:00+09:00
  ...
  ```

## 5. 비즈니스 규칙

### 5.1 스레드 정책

- 유저의 첫 질문 시 새로운 스레드 생성.
- 마지막 질문 후 30분이 지난 시점에 질문 시 새로운 스레드 생성.
- 마지막 질문 후 30분 이내 질문 시 기존 스레드 유지.

### 5.2 JWT 인증 및 토큰 정책

- **액세스 토큰 (Access Token)**:
  - 만료 기간: **5분**
  - 모든 인증 필요 API 요청 시 `Authorization` 헤더에 사용됩니다.
- **리프레시 토큰 (Refresh Token)**:
  - 만료 기간: **24시간 (1일)**
  - 액세스 토큰 만료 시 새로운 액세스 토큰을 발급받기 위해 사용됩니다.
- 인증 실패 시 `401 Unauthorized`를 응답하며, 토큰 만료 시 클라이언트는 리프레시 토큰을 사용하여 재발급 절차를 진행해야 합니다.

### 5.3 권한 정책

- **MEMBER**: 본인의 대화/피드백만 조회 및 관리 가능.
- **ADMIN**: 모든 사용자의 대화/피드백 조회 가능 및 관리 기능(피드백 상태 변경, 통계 조회) 수행 가능.

### 5.4 피드백 정책

- 한 명의 사용자는 하나의 대화에 대해 하나의 피드백만 생성 가능.
- 하나의 대화에는 여러 사용자의 피드백이 존재할 수 있음.

### 5.5 기타 정책

- **사용자 권한 부여**: 모든 신규 회원가입 사용자는 기본적으로 `MEMBER` 권한을 가집니다. `ADMIN` 권한은 시스템 보안을 위해 가입 시 직접 호출로 부여할 수 없습니다.
- **테스트용 관리자 계정**: 시스템 기능 테스트 및 관리자 시연을 위해 `ADMIN` 권한을 가진 관리자 계정을 초기 데이터(Default Data)로 제공합니다.
