# TASKS.md

본 문서는 실질적인 기술 구현 사항을 체크리스트 형태로 관리하는 문서입니다. 각 태스크는 `PLAN.md`의 페이즈와 연동됩니다.

## Phase 1. 프로젝트 기초 설정 및 사용자 인증 (Auth)

- [ ] **프로젝트 초기화**
    - [ ] `build.gradle.kts` 의존성 추가 (Spring Security, springmockk, JPA, H2, Validation, kotlin-logging)
    - [ ] 패키지 구조 생성 (`application`, `domain`, `adapter.in`, `adapter.out`)
    - [ ] `application.yaml`: SQL 초기화 프로퍼티 설정 (`defer-datasource-initialization: true` 등)
- [ ] **사용자 도메인 및 초기 데이터**
    - [ ] `User` 엔티티 및 `UserRole` 열거형 정의 (Default: MEMBER)
    - [ ] `src/main/resources/data.sql`: 관리자 계정 및 기본 데이터 작성
    - [ ] 패스워드 암호화 로직 (`DelegatingPasswordEncoder`) 적용
- [ ] **보안 체계 구축**
    - [ ] JWT Provider (생성-Access 5m/Refresh 24h, 검증, 파싱) 구현
    - [ ] `SecurityFilter` 및 `AuthenticationEntryPoint` 설정
- [ ] **인증 API 구현**
    - [ ] `POST /api/v1/auth/signup`: 회원가입 기능 (TDD)
    - [ ] `POST /api/v1/auth/login`: 로그인 및 토큰 발급 (TDD)
    - [ ] `POST /api/v1/auth/refresh`: 토큰 재생성 기능 (TDD)

## Phase 2. 대화 관리 기능 (Chat & Thread)

- [ ] **대화 모델 설계**
    - [ ] `Thread`, `Chat` 도메인 및 영속성 어댑터 구현
- [ ] **스레드 엔진 개발**
    - [ ] 마지막 활동 시간 기준 30분 도래 여부에 따른 스레드 조회/생성 로직 서비스 구현
- [ ] **OpenAI 연동**
    - [ ] OpenAI Client 인터페이스 및 구현 (WebClient 사용)
    - [ ] API Key 및 모델 설정 구성
- [ ] **대화 API 구현**
    - [ ] `POST /api/v1/chats`: 대화 생성 (일반 응답)
    - [ ] `POST /api/v1/chats`: SSE 기반 스트리밍 응답 처리 (TDD)
    - [ ] `GET /api/v1/chats`: 스레드 그룹화 및 페이징 조회 구현 (TDD)
    - [ ] `DELETE /api/v1/threads/{threadId}`: 본인 소유 확인 후 삭제 (TDD)

## Phase 3. 사용자 피드백 관리 (Feedback)

- [ ] **피드백 도메인 개발**
    - [ ] `Feedback` 엔티티 및 `FeedbackStatus` 정의
- [ ] **피드백 API 구현**
    - [ ] `POST /api/v1/feedbacks`: 중복 피드백 방지 로직 포함 (TDD)
    - [ ] `GET /api/v1/feedbacks`: 필터 및 페이징 조회 기능 (TDD)
    - [ ] `PATCH /api/v1/feedbacks/{feedbackId}`: 관리자 권한 체크 및 상태 변경 (TDD)

## Phase 4. 분석 및 데이터 보고 (Admin & Statistics)

- [ ] **활동 로깅 시스템**
    - [ ] 활동(Join, Login, Chat) 카운팅을 위한 리포지토리 쿼리 또는 별도 로그 테이블 설계
- [ ] **통계 및 보고서 API**
    - [ ] `GET /api/v1/stats/activities`: 기간별 집계 API (TDD)
    - [ ] `GET /api/v1/reports/chats`: 대화 데이터 CSV 변환 및 다운로드 스트림 구현 (TDD)

## Phase 5. 품질 최적화 및 문서화 (Refactor & Docs)

- [ ] **최종 리팩토링**
    - [ ] 전역 예외 처리 (`@RestControllerAdvice`) 강화
    - [ ] 미사용 Import 제거 및 코드 스타일 점검
- [ ] **문서화**
    - [ ] README.md 업데이트 및 API 사용법 정리
