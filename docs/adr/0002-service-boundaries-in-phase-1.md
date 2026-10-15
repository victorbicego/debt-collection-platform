# 0002 — Service boundaries in phase 1

- Status: proposed
- Date: 2026-10-14

## Context
The domain has clear contexts (invoices, payments, notifications, payment plans), but at the start there is one developer and little traffic.

## Options considered
1. Microservices from day one — shows Kafka and independent deployment early, but multiplies infrastructure before the domain exists.
2. One service, organised as a modular monolith (Spring Modulith), extracting services later — lower cost, requires discipline at module boundaries.

## Decision
To be decided at the end of phase 1, based on what I learn in the architecture topics.

## Consequences
To be filled in.
