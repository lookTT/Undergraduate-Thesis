# AGENTS.md

This repository contains a university thesis project and its implementation.
Codex should treat the thesis documentation and the application code as one
cohesive deliverable.

## Project Layout

- `backend/`: Spring Boot 3 backend, Java 21, MyBatis-Plus, Spring Security, JWT.
- `frontend/`: Vue 3 + Vite + TypeScript + Element Plus frontend.
- `docs/`: thesis notes, school requirements, planning docs, and chapter drafts.
- `docs/学校要求/`: school-provided requirements and templates. Do not move or rename
  these files.

## Working Rules

- Keep changes consistent with the thesis topic and the school requirements.
- Do not delete, rename, or relocate user-provided documents unless explicitly asked.
- Prefer small, scoped changes that can be verified quickly.
- Use `apply_patch` for manual edits.
- Do not use destructive git commands such as `git reset --hard` or
  `git checkout --` unless explicitly requested.
- Do not overwrite or revert user changes that are unrelated to the task.

## Thesis Documentation Rules

- If the project scope changes, update the planning document:
  `docs/论文规划与技术方案.md`.
- Keep chapter drafts, references, and the implementation in sync.
- When adding thesis content, check that chapter names, terminology, and
  feature descriptions match the actual code.
- Keep the school requirements in `docs/学校要求/` as the source of truth.
- Preserve the existing document locations under `docs/`.

## Backend Rules

- Target Java 21 and Spring Boot 3.
- Prefer straightforward, readable service and controller code.
- Keep authentication and authorization aligned with the minimal JWT-based
  security design already in the project.
- Run backend verification after meaningful backend changes, typically with:
  `mvn test`

## Frontend Rules

- Use Vue 3, Vite, TypeScript, Pinia, Vue Router, and Element Plus.
- Keep the UI practical and readable; this is a graduation project, not a
  showcase site.
- Run frontend verification after meaningful frontend changes, typically with:
  `npm run build`

## Git Rules

- Commit coherent units of work.
- Use clear commit messages that describe the actual change.
- Keep the working tree clean after a finished task whenever possible.

## Output Expectations

- When you change files, summarize the outcome briefly and mention the affected
  paths.
- When you inspect code or documents, prefer exact file references over vague
  descriptions.
- If something is incomplete or blocked, state that clearly and do not pretend
  it is finished.
