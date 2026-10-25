# Payment Processing API

A Java 21 and Spring Boot REST API that demonstrates backend patterns for a simplified payment service: authentication, access control, transaction processing, idempotency, audit logging, and resilience around a bank gateway.

## What it does

Users can register and authenticate, then create transactions and view their own payment history. Administrators can inspect transactions across accounts. Transaction creation is protected with an idempotency key so clients can safely retry requests without creating duplicate records.

The service also includes:

- BCrypt password hashing and JWT access tokens
- Revocable, rotating refresh tokens
- Role-based authorization for transaction operations
- ISO 4217 currency validation and positive-amount validation
- Transaction status tracking (`PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`)
- A persistent audit log for transaction creation and gateway outcomes
- A simulated bank gateway wrapped with retry and circuit-breaker behavior
- Login rate limiting
- OpenAPI documentation with Swagger UI
- Micrometer timing instrumentation for transaction processing
- PostgreSQL schema migrations managed with Flyway

The bank gateway is a local simulation rather than a connection to a real payment provider. It randomly simulates gateway timeouts to exercise resilience behavior.

## Roles and access

- **USER**: can register, log in, create transactions, and view their own transactions.
- **ADMIN**: can view transactions across accounts. Admin accounts are created through the admin-registration route, which requires an existing administrator.

Transaction list responses are paginated and can be sorted using Spring's `page`, `size`, and `sort` query parameters.

## Technology

- Java 21
- Spring Boot 4.x, Spring Security, Spring Data JPA
- PostgreSQL and Flyway
- JWT (jjwt)
- Resilience4j
- SpringDoc OpenAPI / Swagger UI
- Micrometer and Spring Boot Actuator
- Docker Compose

## Run locally with Docker

Requirements: Docker with the Docker Compose plugin.

1. Create a local environment file and replace its placeholders with suitable values:

   ```bash
   cp .env.example .env
   ```

   For Docker Compose, set `DB_HOST=postgres`. Keep `.env` local and do not commit secrets.
2. Build and start the API and PostgreSQL containers:

   ```bash
   make up
   ```
3. The API listens on the port configured by `API_PORT` (8080 by default).

Stop the containers with:

```bash
make down
```

## API documentation

When the application is running, visit:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Register a USER account |
| `POST` | `/api/v1/auth/register/admin` | Register an ADMIN account (ADMIN only) |
| `POST` | `/api/v1/auth/login` | Authenticate and receive access and refresh tokens |
| `POST` | `/api/v1/auth/refresh` | Rotate a refresh token and issue new tokens |
| `POST` | `/api/v1/auth/logout` | Revoke the current user's refresh tokens |
| `POST` | `/api/v1/transactions` | Create a transaction (requires `Idempotency-Key`) |
| `GET` | `/api/v1/transactions` | List transactions visible to the authenticated user |
| `GET` | `/api/v1/transactions/{id}` | Retrieve a transaction the authenticated user may access |

Registration and login are public. Refresh requests use a refresh token in the request body. Logout, admin registration, and transaction requests require an access token in the `Authorization: Bearer <access_token>` header.

## Example requests

Register and log in:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "change-this-password"}'

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "change-this-password"}'
```

Use the access token returned by login to create and retrieve a transaction:

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access_token>" \
  -H "Idempotency-Key: 1111-2222-3333-4444" \
  -d '{"amount": 120.50, "currency": "USD"}'

curl "http://localhost:8080/api/v1/transactions?page=0&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer <access_token>"

curl http://localhost:8080/api/v1/transactions/<TRANSACTION_ID> \
  -H "Authorization: Bearer <access_token>"
```

## Run tests

From the `api/` directory, run the Maven test suite with the included wrapper:

```bash
./mvnw test
```

## Project layout

- `api/`: Spring Boot application, migrations, and tests
- `database/`: PostgreSQL container setup
- `docs/`: project brief and supporting notes
- `docker-compose.yaml`: local API and database services

## Possible next steps

- Add load, stress, and peak traffic testing.
- Add a continuous integration pipeline.
- Expand gateway integration tests and document operational monitoring.
