---
name: kotlin-spring-developer
description: Enforce Hexagonal Architecture, DDD, and TDD in Kotlin/Spring Boot projects. Use this skill for Kotlin source code tasks, refactoring, and test generation to maintain architectural integrity.
---

# Kotlin Spring Developer

Enforce architectural integrity and code quality. Apply these rules without exception.

## 1. Architecture & Design

### 1.1 Hexagonal Architecture
- **domain**: Pure logic. No framework (Spring/JPA) dependencies.
- **adapter.inbound.web**: Controllers and DTOs.
- **adapter.inbound.application**: Usecase implementations (Service).
- **adapter.outbound.persistence**: JPA entities and persistence adapters.
- **Layer Transition**: Use `-Mapper` classes for all data conversions between layers.

### 1.2 DDD & TDD
- **DDD**: Separate Domain Model (behavior) from JPA Entity (persistence).
- **TDD**: Follow Red-Green-Refactor. **ALWAYS use the `@/tdd` skill for this workflow.**

## 2. Kotlin Style Guide

- **Immutability**:
  - Use `data class` with `val` for VOs and DTOs.
  - Return new objects via `copy()` for domain state changes.
- **Syntax**:
  - Use **Trailing Commas** for all lists and parameters.
  - Separate 3+ digit literals with underscores (`1_000`).
  - Default nullable properties to `null`.
- **JPA**:
  - Entity suffix: `-Entity`. Table name: singular snake_case.
  - **Constructor**: Include **required properties** in the constructor. Do NOT include `id` or **nullable properties** in the constructor (define them in the class body).
  - Encapsulate `@Id` with `private set`.
  - Inherit `AuditingFields` and override `toString()` in multi-line format.

## 3. Testing Standards

- **Setup**: Use MockK. Name the system under test `sut`.
- **Injection**: Use **Constructor Injection** in test classes.
- **Display**: Class level: `@DisplayName("[Label] ... Test")`.
- **Naming (Method)**: Use backticks (`` ` ``) with specific templates:
  - **Unit Test**: Input and output style - `` `~~~가 주어지면, ~~~를 반환한다` ``
  - **WebMvc Slice Test**: Request and response style - `` `~~~를 요청하면, ~~~하고 ~~~를 응답한다` ``
  - **JPA Slice Test**: Input and output style - `` `~~~가 주어지면, ~~~를 반환한다` ``
  - **Integration Test**: Focus on cause and effect - `` `~~~하면, ~~~한다` ``
- **Assertions**: Use AssertJ chaining and `hasFieldOrPropertyWithValue`.

## 4. Templates

Reference these for structure:
- Entity: `resources/templates/jpa-entity.kt`
- Unit Test: `resources/templates/unit-test.kt`
- WebMvc Slice Test: `resources/templates/web-mvc-test.kt`
- JPA Slice Test: `resources/templates/data-jpa-test.kt`
- Integration Test: `resources/templates/integration-test.kt`

## 5. Final Polish

- Remove unused imports immediately.
- POSIX trailing newline. No double blank lines.
