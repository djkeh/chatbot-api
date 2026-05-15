# AGENTS.md

Guidelines for AI agents on tech stack, design, testing, and workflow.

## 1. Tech Stack

* **Language:** Kotlin 1.9 (JVM 21)
* **Framework:** Spring Boot 3
* **Testing:** JUnit 5, AssertJ, MockK(via springmockk)
* **Build:** Gradle (Kotlin DSL)
    * Manage root package and dependencies in `/build.gradle.kts`.

## 2. Design & Development Principles

* **Mandatory:** Always refer to and follow `@/kotlin-spring-developer` for architecture (Hexagonal), DDD, and TDD.

## 3. Workflow

1. **Context:** Read `/PRD.md` and `/SPEC.md`.
2. **Requirements:** Read `/PLAN.md` and `/TASKS.md`.
3. **Issue/Branch:** Use existing GitHub issues. Create a branch `feature/{ISSUE_NUMBER}`.
4. **TDD (Red):** Write failing unit/integration tests first.
5. **Implement (Green):** Write minimal code to pass tests. Check dependencies.
6. **Refactor:** Clean code, remove unused imports, and verify tests.
7. **Document:** Update `/TASKS.md` and project docs.
8. **Commit/Push:** Follow Git rules. Push to remote.
9. **PR:** Request review. Direct merge to `main` is forbidden.

## 4. Configuration Management

### 4.1 Git Usage

* **Mandatory:** Always refer to and follow `@/git-commit` for staging and structure.
* **Commit Message Language:** Write all commit message bodies in **Korean (한국어)**.

### 4.2 GitHub Usage

* **Strategy:** GitHub Flow.
* **Naming:** `feature/{GITHUB_ISSUE_NUMBER}` (e.g., `feature/1`).
* **Issues:** Link every task to a GitHub issue.
    * **Labels:** `enhancement` (features), `documentation`, `bug`.
* **Pull Requests:**
    * **Title:** Phase title (e.g., `Phase 1. Project Setup`).
    * **Content:**
        * Purpose (max 3 lines).
        * Key changes (list).
        * `This closes #{ISSUE_NUMBER}` footer.
    * **Assignee:** `djkeh`.
    * **Labels:** Same as the linked issue.

## 5. Standards

* **Logging:** Use `kotlin-logging`. Define outside implementation classes:
    * `private val logger = KotlinLogging.logger {}`
* **API Design:** Follow RESTful rules in `/SPEC.md`.

## 6. Strict Prohibitions

* Never perform any work not explicitly defined in `/TASKS.md`.
* Never change anything when asked a question.
