# Project Brief: Payment Processing Service (Backend Learning Project)

## Goal

Build a backend REST API for a simplified payment processing system. This project is designed to teach core backend and Spring Boot fundamentals: authentication, authorization, persistence, validation, resilience, and observability — the kind of concerns you'll meet in real fintech-adjacent backend work.

You are **not** given the implementation — only the requirements. Design the entities, endpoints, and internal architecture yourself. Suggested tech stack and stretch goals are listed at the end so you can scope the project to your current skill level.

---

## Domain

Two roles exist in the system:

- **USER** — can register, log in, create their own transactions, and view only their own transactions.
- **ADMIN** — can view *all* users' transactions, but cannot create transactions themselves.

A transaction represents a payment of a given amount in a given currency, with a status and a creation timestamp.

---

## Functional Requirements

### 1. Authentication

- `POST /api/v1/auth/register` — register a new user with a username and password. New users always get the `USER` role.
- `POST /api/v1/auth/login` — authenticate and receive **two tokens**:
  - a short-lived **access token** (e.g. 15 minutes)
  - a long-lived **refresh token** (e.g. 7 days)
- `POST /api/v1/auth/refresh` — exchange a valid refresh token for a new access token. Each use should invalidate the old refresh token and issue a new one (**rotation**) — think through why this matters and what should happen if a stolen refresh token is used.
- `POST /api/v1/auth/logout` — revoke all of the current user's refresh tokens server-side.
- `POST /api/v1/auth/register/admin` — restricted to existing ADMINs; creates a new ADMIN account.

Requirements to think through yourself:
- Passwords must never be stored in plain text.
- Access tokens should be stateless (no server-side session lookup needed to validate them).
- Refresh tokens should be revocable, which implies they can't be *purely* stateless — where do you store them, and what do you store (the raw token, or something derived from it)?

### 2. Authorization

- Only authenticated users may create or view transactions.
- A `USER` may only ever see their **own** transactions — not other users'.
- An `ADMIN` may view transactions belonging to **any** user, but may not create transactions.
- Decide: what should happen if a `USER` requests a transaction ID that exists but belongs to someone else? Should the API reveal that the resource exists at all?

### 3. Transactions

- `POST /api/v1/transactions` — create a transaction (`USER` only). Body includes at least an amount and a currency.
- `GET /api/v1/transactions` — list transactions. `USER` sees only their own; `ADMIN` sees everyone's. Support **pagination and sorting** via query parameters (page number, page size, sort field/direction), and return pagination metadata (total elements, total pages, etc.) alongside the results.
- `GET /api/v1/transactions/{id}` — fetch a single transaction by ID, subject to the same ownership rules as above.

### 4. Idempotent Payment Creation

Clients may retry a failed or ambiguous `POST /api/v1/transactions` request (e.g. after a timeout). Design a mechanism so that retried requests with the same client-supplied idempotency key do **not** create duplicate transactions, and instead return the original result.

Think about:
- Where does the idempotency key come from (header? body?), and how do you enforce "same key = same result"?
- What should happen if the same key is reused with a *different* request body?
- Should idempotency records live forever, or expire?

### 5. Currency Validation

Reject transactions with an invalid currency code. Rather than hardcoding a list of valid currencies, use your language/framework's built-in ISO 4217 currency support so the validation stays correct without manual maintenance. Implement this as a **reusable, declarative validation annotation/constraint** rather than inline `if` checks scattered through the code.

### 6. Resilience

Assume transaction creation calls out to an external "bank gateway" (you can build a mock/stub for this — even one that randomly fails or sleeps, to simulate real network conditions).

- Add a **retry** policy for transient failures when calling the gateway.
- Add a **circuit breaker** so that if the gateway is consistently failing, your service stops hammering it and fails fast instead.
- Add **rate limiting** on the login endpoint specifically (e.g. 5 requests/minute) to slow down brute-force password guessing. Decide what HTTP status and response body a rate-limited client should see.

### 7. Observability

- Log requests with structured, machine-parseable output (e.g. JSON), not free-text strings.
- Every log line belonging to the same request should be traceable back to that request — and, if authenticated, to the user who made it. Research how to attach contextual metadata (like a trace/request ID) to every log statement within a request's lifecycle without manually passing it into every method call.
- Expose basic application metrics (e.g. request counts, latencies) in a format a metrics system could scrape.

### 8. API Documentation

Expose interactive, auto-generated API documentation (e.g. an OpenAPI/Swagger UI) so the endpoints, request/response schemas, and auth requirements are discoverable without reading the source code.

### 9. Schema Management

- Use a database migration tool to version your schema (don't rely on the ORM to auto-generate/alter tables in a way that isn't reviewable).
- Design your schema for: users, transactions, refresh tokens, idempotency keys, and (optionally) an audit log of significant actions.

### 10. Testing

- Write integration tests that exercise the real HTTP layer and a real database (not an in-memory substitute that behaves differently from production).
- Cover at minimum: registration/login happy path, access denied for another user's transaction, idempotent retry behavior, and rate limiting on login.

---

## Design Questions to Answer Before Coding

These mirror real architectural decisions — answer them yourself and be ready to justify them:

1. Should transaction IDs be sequential integers or random (e.g. UUIDs)? What does each choice leak to a client, and what does each cost you at the database level?
2. Should `ADMIN` inherit all `USER` permissions, or should the two roles be entirely separate permission sets? What's the risk of role inheritance in a payments system?
3. Cross-user access to a transaction: `403 Forbidden` or `404 Not Found`? What does each tell an attacker?
4. Where should the idempotency mechanism live — bolted onto the `Transaction` entity, or as an independent concern that other endpoints could reuse later?
5. What's the actual attacker scenario that refresh token rotation defends against, and how would you detect that it happened?

---

## Suggested Tech Stack

(Swap any of these for equivalents you already know, or use this project specifically to learn them.)

| Layer | Suggestion |
|---|---|
| Language/Framework | Java 21 + Spring Boot 3.x |
| Security | Spring Security 6 + JWT |
| Persistence | PostgreSQL + Spring Data JPA + Flyway |
| Resilience | Resilience4j |
| Observability | Micrometer + a log aggregator (e.g. Loki/Grafana) |
| API Docs | SpringDoc OpenAPI |
| Testing | JUnit 5 + MockMvc + Testcontainers |
| Build/Run | Maven, Docker Compose |

---

## Suggested Build Order (Milestones)

Build incrementally and get each stage working before moving on:

1. **Skeleton** — Spring Boot app boots, connects to Postgres, one Flyway migration, a health-check endpoint.
2. **Users & auth** — registration, password hashing, login issuing a JWT access token. No refresh tokens yet.
3. **Authorization** — protect endpoints, add role-based rules, add ownership checks (users can't see each other's data).
4. **Transactions CRUD-lite** — create + list + get-by-id, with pagination and sorting.
5. **Refresh tokens** — add the second token, rotation, logout/revocation.
6. **Idempotency** — add the idempotency key mechanism to transaction creation.
7. **Validation** — currency validation as a reusable annotation.
8. **Resilience** — mock bank gateway + retry + circuit breaker + login rate limiting.
9. **Observability** — structured logging with request/user context, metrics endpoint.
10. **Docs & tests** — OpenAPI/Swagger UI, integration test suite with a real database via containers.

---

## Stretch Goals

- Add an audit log table recording who did what, when (e.g. every login, every transaction creation/view by an admin).
- Add transaction status transitions (e.g. `PENDING → COMPLETED / FAILED`) with a state machine instead of a raw enum field anyone can overwrite.
- Containerize the whole stack (app + DB + log aggregator) with Docker Compose and a one-command startup.
- Load-test the login rate limiter and the circuit breaker under concurrent load; tune the numbers based on real measurements rather than guesses.
- Compare platform threads vs. virtual threads under a simulated slow external dependency and measure the throughput difference yourself.

---

## What "Done" Looks Like

You should be able to: register two users and one admin; log in as each; create transactions as a regular user; confirm that user A cannot see user B's transactions but the admin can see both; retry a transaction creation with the same idempotency key and get back the original result, not a duplicate; get rate-limited after repeated failed logins; and view all of the above endpoints documented in a live Swagger UI.
