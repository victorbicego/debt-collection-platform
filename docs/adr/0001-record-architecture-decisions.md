# 0001 — Record architecture decisions

- Status: accepted
- Date: 2026-10-14

## Context
The project will grow over six phases. Without a record, the reasons behind choices get lost and cannot be explained later.

## Decision
Every decision that is hard to reverse becomes a short ADR in `docs/adr/`, numbered and never deleted.
A reversed decision gets a new ADR that supersedes the old one.

## Consequences
Writing takes a few minutes per decision. In return, the history shows how the system evolved and why.
