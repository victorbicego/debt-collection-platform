# 0003 — Flyway owns the database schema

- Status: accepted
- Date: 2026-10-14

## Context
Hibernate can generate the schema (`ddl-auto`), but generated DDL is not reviewed, not versioned and cannot do zero-downtime changes.

## Decision
All schema changes are versioned SQL migrations in `src/main/resources/db/migration`, applied by Flyway.
Hibernate runs with `ddl-auto: validate`, so the application fails at startup if the entities and the schema disagree.

## Consequences
Every entity change needs a migration. Migrations that were already applied are never edited; fixes are new migrations.
Breaking changes (renaming or dropping columns) follow expand-contract.
