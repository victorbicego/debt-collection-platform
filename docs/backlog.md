# Backlog

Every item is small (1–3 study sessions), adds something real to the product described in
[docs/product/requirements.md](product/requirements.md), and practises specific topics of my study plan
(notes and isolated exercises live in [backend-engineering-studies](https://github.com/victorbicego/backend-engineering-studies)).

## How to use it
1. Take the first item marked ⬜ in the current phase. Mark it 🟨.
2. Do the related lessons first if you have not yet (column **Topics**).
3. Implement until every line of **Done when** is true. Commit with the item ID: `feat(P1-04): add due date to invoices`.
4. Mark it ✅. If you made a decision that is hard to reverse, write an ADR.

Items can be reordered or split. New ideas go to the **Parking lot** at the end, not into the current phase.

Status: ⬜ to do · 🟨 doing · ✅ done

## Definition of done (every item)
- Tests for the behaviour, including at least one failure case. CI is green.
- Requirement IDs (R1, R2…) mentioned in tests or code comments where a rule is implemented.
- README, http file and diagrams updated if the API or architecture changed.
- No personal data in logs. No TODO left without a backlog item.

---

## Phase 1 — Invoices and collection cases in one service (topics 1–7)
Goal: a single, well-tested service that handles invoices, opens collection cases and registers payments correctly.

| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P1-01 | Invoice status as a sealed interface instead of an enum | 1 | Status transitions are handled with exhaustive `switch`; an invalid transition does not compile or throws a domain exception; tests cover every transition. | ⬜ |
| P1-02 | `Money` value object in the domain (amount + currency) | 1, 4 | Entities use `Money`, mapped with an embeddable or converter; rounding is half-even to the currency's decimals; no `double` anywhere. | ⬜ |
| P1-03 | Creditors and debtors | 6, 7 | `POST/GET /creditors` and `/debtors`; invoices reference both by ID; foreign keys and indexes in a migration. | ⬜ |
| P1-04 | Invoice number, issue date and due date; idempotent registration (R1, R2) | 6, 7, 8 | Unique `(creditor_id, invoice_number)`; registering the same invoice twice returns `200` with the existing one; validation errors as Problem Details. | ⬜ |
| P1-05 | Open collection cases for overdue invoices (R3, R4) | 6, 3 | Daily scheduled job opens cases; runs safely if two instances start at once (lock or `SELECT … FOR UPDATE SKIP LOCKED`); clock is injected so tests control "today". | ⬜ |
| P1-06 | Case history / audit trail | 1, 7 | Every state change writes a history row (time, actor, reason); `GET /cases/{id}/history`. | ⬜ |
| P1-07 | Fees and interest calculation (R6–R9) | 4, 1, 5 | Strategy per calculation; unit tests with exact cents for leap years and month ends; legal cap respected; written test-first. | ⬜ |
| P1-08 | Manual payment registration and allocation (R10–R13) | 4, 6 | `POST /cases/{id}/payments`; allocation fees → interest → principal; zero balance → Paid; overpayment flagged. | ⬜ |
| P1-09 | Optimistic locking on cases | 7, 3 | `@Version` on the case; test with two concurrent payments proves no update is lost; `409` Problem Details on conflict. | ⬜ |
| P1-10 | Case search with pagination and filters | 7, 6 | `GET /cases?status=&creditorId=&page=`; seed script with 1 million cases; `EXPLAIN ANALYZE` of the 3 main queries saved in `docs/performance/`, with the indexes that fixed them. | ⬜ |
| P1-11 | No N+1 queries | 6, 7 | SQL logging shows a fixed number of queries for the search and the history endpoints; a test asserts the query count. | ⬜ |
| P1-12 | Architecture rules | 5, 4 | ArchUnit tests: domain does not depend on web or persistence; controllers do not use repositories directly. | ⬜ |
| P1-13 | Mutation testing on the domain | 5 | PIT runs on the domain package; mutation score recorded in the README; surviving mutants either killed or explained. | ⬜ |
| P1-14 | Profile and fix one hotspot | 2 | JFR recording under load (e.g. with k6 or Gatling) of the search endpoint; one measured improvement documented with before/after numbers. | ⬜ |
| P1-15 | Zero-downtime column rename | 7 | Rename `debtor` text to a proper reference using expand-contract across 3 migrations; app works between each step. | ⬜ |
| P1-16 | Phase wrap-up | 24 | ADR 0002 decided; C4 container diagram updated; README updated; tag `v0.1.0`. | ⬜ |

## Phase 2 — Payments, notifications and events (topics 8–13)
Goal: three services that talk through APIs and events, survive failures, and never charge or notify twice.

| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P2-01 | Payments API designed contract-first | 8 | OpenAPI spec reviewed before code; server stubs or validation generated from it; spec published in the repo. | ⬜ |
| P2-02 | Extract the `payments` service | 8, 20 | Payments live in their own service and database; invoices no longer write payments directly. | ⬜ |
| P2-03 | Idempotency keys on payment creation (R11) | 8 | `Idempotency-Key` header; the same request retried returns the same result; test with parallel retries. | ⬜ |
| P2-04 | Events with Kafka and the Outbox pattern | 9 | `InvoiceRegistered`, `CaseOpened`, `PaymentReceived` published via outbox table; killing the service between commit and publish loses nothing. | ⬜ |
| P2-05 | Idempotent consumers | 9 | Processed-events table; replaying a topic from the start does not change balances. | ⬜ |
| P2-06 | Retries, backoff and dead letter topic | 9, 12 | Poison message ends in the DLT after N attempts; a runbook explains how to replay it. | ⬜ |
| P2-07 | `notifications` service with the contact strategy (R14, R17, R18) | 9, 4 | Reminders scheduled from events; fake email/SMS providers; templates per language; channel fallback. | ⬜ |
| P2-08 | Contact rules (R15, R16) | 4, 1 | Quiet hours, holidays and weekly cap enforced with time zones; tests with fixed clocks. | ⬜ |
| P2-09 | Payment plan Saga (R20–R22) | 9 | Plan creation → instalment schedule → notification, with compensation if a step fails; broken plan returns the case to Reminding. | ⬜ |
| P2-10 | Resilience on every remote call | 12, 10 | Timeouts, retry with jitter, circuit breaker, bulkhead; Toxiproxy test shows graceful degradation when the SMS provider is slow. | ⬜ |
| P2-11 | Cache creditor configuration | 11 | Caffeine + Redis; invalidation on change via event; stampede test. | ⬜ |
| P2-12 | Rate limiting on public APIs | 11, 3 | Per-client limit in Redis; `429` with `Retry-After`. | ⬜ |
| P2-13 | Read model for case search via CDC | 13 | Debezium streams changes to a search index or read table; search no longer hits the write database. | ⬜ |
| P2-14 | Breaking API change with a migration plan | 8 | `v2` of one endpoint, `v1` marked deprecated with a sunset date; both tested. | ⬜ |
| P2-15 | Phase wrap-up | 24, 10 | Sequence diagrams of the main flows; DDIA notes linked; tag `v0.2.0`. | ⬜ |

## Phase 3 — Running it like production (topics 14–19)
Goal: the platform runs on Kubernetes and AWS, is observable, secure and deployed by a pipeline.

| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P3-01 | Small, secure images for all services | 14 | Layered images under 250 MB, non-root, scanned; one `docker compose up` starts everything. | ⬜ |
| P3-02 | Helm charts and local Kubernetes | 14 | All services on kind; correct liveness/readiness probes; HPA scales under load. | ⬜ |
| P3-03 | AWS infrastructure in Terraform | 15, 16 | VPC, private RDS, cluster or ECS, IAM least privilege; `terraform destroy` + `apply` recreates everything; cost estimate in the docs. | ⬜ |
| P3-04 | Monthly creditor report with SQS + Lambda | 15 | Report generated asynchronously and stored in S3; retries and DLQ configured. | ⬜ |
| P3-05 | Distributed tracing and structured logs | 17 | One trace follows a payment from API to notification; logs in JSON with trace ID; no personal data (R31). | ⬜ |
| P3-06 | Dashboards, SLOs and alerts | 17 | RED dashboard per service; SLOs from section 6 of the requirements; burn-rate alerts each linked to a runbook. | ⬜ |
| P3-07 | CI/CD pipeline | 18 | Build → test → scan → image → deploy on every merge; canary with automatic rollback on error rate. | ⬜ |
| P3-08 | Feature flag | 18 | New reminder template released to 10% of debtors, switchable without deploy. | ⬜ |
| P3-09 | Login and authorisation with Keycloak (OIDC) | 19 | Roles creditor, agent, admin; a creditor only sees its own cases; tests for forbidden access. | ⬜ |
| P3-10 | Threat model and supply chain | 19 | STRIDE document; Trivy or Dependency-Check fails the build on critical issues; secrets in a secrets manager. | ⬜ |
| P3-11 | GDPR features (R30, R32) | 19 | Data export endpoint; anonymisation job; audit of personal-data reads. | ⬜ |
| P3-12 | Simulated incident and postmortem | 17, 12 | Break something on purpose, detect it via alerts, write a blameless postmortem in `docs/postmortems/`; tag `v0.3.0`. | ⬜ |

## Phase 4 — Architecture (topics 20–24)
Goal: the design is deliberate, documented and explainable.

| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P4-01 | Event Storming and context map | 21 | Board exported to `docs/architecture/`; context map with relationship types. | ⬜ |
| P4-02 | Collection case as a proper aggregate | 21 | Invariants enforced only inside the aggregate; tests prove it can never become invalid. | ⬜ |
| P4-03 | Hexagonal architecture in one service | 20 | Domain and application code free of Spring; adapters for web, persistence, messaging; ArchUnit enforces it. | ⬜ |
| P4-04 | Legacy import with Strangler Fig | 22 | A fake "legacy system" (nightly CSV/SOAP batch) with an anti-corruption layer; traffic moved gradually to the new API. | ⬜ |
| P4-05 | Architecture documentation | 24 | C4 levels 1–3 current; ADRs for the 5 most important decisions. | ⬜ |
| P4-06 | Design review at 10× scale | 23 | Written system design of the platform for 10× creditors and debtors: bottlenecks, what changes, cost; tag `v0.4.0`. | ⬜ |

## Phase 5 — Showing the work (topics 25–28, in parallel with the others)
| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P5-01 | Professional README | 26 | A reader understands what, why and how to run it in 5 minutes; architecture tour with diagrams. | ⬜ |
| P5-02 | Design doc before a phase-6 feature | 26 | Written with the template in `docs/design/`, reviewed (by a colleague or by me after a week), then implemented. | ⬜ |
| P5-03 | Business metrics | 27, 17 | Collection rate, average days to pay and cost per case exposed as metrics and on a dashboard. | ⬜ |
| P5-04 | Cost review | 27 | Monthly cloud cost per service and one optimisation with numbers. | ⬜ |
| P5-05 | Three articles about the project | 26, 25 | Published (blog, dev.to or Medium) and linked from the README. | ⬜ |

## Phase 6 — Multi-tenancy, financial core and AI (topics 30–32)
Goal: the hardest and most valuable parts, especially for fintech roles.
Topic 29 (algorithms) is practised in the studies repository, not here.

| ID | Item | Topics | Done when | Status |
|----|------|--------|-----------|--------|
| P6-01 | Creditors as tenants (R33) | 31 | `tenant_id` everywhere with PostgreSQL row-level security; tenant comes from the token. | ⬜ |
| P6-02 | Tenant context propagation | 31, 3 | Tenant flows through HTTP, scheduled jobs and Kafka headers; a job without tenant fails fast. | ⬜ |
| P6-03 | Isolation and noisy-neighbour tests (R34) | 31, 12 | Test proves tenant A cannot read B's data via API, events or reports; per-tenant rate limits under load. | ⬜ |
| P6-04 | Double-entry ledger (R28) | 32 | Every money movement creates balanced entries; a property-based test proves the ledger always sums to zero. | ⬜ |
| P6-05 | Bank statement import and reconciliation (R25–R27) | 32 | Parses camt.053 samples; matches by RF/QR reference, case number, then amount + name; unmatched queue for agents. | ⬜ |
| P6-06 | Swiss QR-bill for CHF invoices | 32 | QR-bill generated with a valid QR reference; payments with that reference match automatically. | ⬜ |
| P6-07 | Payouts to creditors (R29) | 32, 9 | Weekly payout minus commission; idempotent; survives a crash in the middle. | ⬜ |
| P6-08 | Classify incoming debtor emails (R35) | 30 | Structured output into a Java record; eval set of at least 50 fictitious emails with accuracy tracked in CI; low confidence → agent. | ⬜ |
| P6-09 | Case summaries for agents (R36, R37) | 30, 19 | RAG over case history with pgvector; personal data redacted before the prompt; prompt-injection tests; cost per request tracked with budget alert. | ⬜ |
| P6-10 | Version 1.0 | 24, 26 | All ADRs and diagrams current; README tells the full story; tag `v1.0.0`. | ⬜ |

---

## Parking lot
Ideas that are not planned yet. Move one into a phase only when the current phase is done.

-
