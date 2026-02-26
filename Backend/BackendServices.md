# Backend Services

## Architecture Overview

All services communicate over HTTP. The API Gateway is the only public entry point — `CoreService` and `CreditService` are internal and accept requests only from trusted callers that present a shared `X-Internal-Api-Key` header.

```
Client / Employee App
        │
        ▼
   API Gateway          (validates JWT, injects X-User-Id / X-User-Role)
   ┌────┴──────────────────────────┐
   ▼                               ▼
UserService                   CoreService  ◄── CreditService
(auth, profiles)              (accounts, transactions)
```

---

## Services

### CoreService — `http://localhost:5208`

The financial core of the bank. Stores all accounts and the full history of every operation. Has no UI — interacted with exclusively via its REST API.

**Responsibilities:**
- CRUD for bank accounts (open, close, get by ID, list by owner, list all)
- Deposits and withdrawals
- Transfers between accounts (atomic — both sides written in a single DB transaction)
- Paginated transaction history per account

**Key design decisions:**
- Uses **CQRS via MediatR** — every operation is an explicit `Command` or `Query` with its own `Handler`. Controllers are thin and only call `_mediator.Send()`.
- Does **not** validate JWTs — that is done once at the Gateway. Trusts `X-User-Id` / `X-User-Role` headers forwarded by the Gateway.
- Secured at the network boundary via `X-Internal-Api-Key` header (checked in middleware before routing).
- No overdraft — balance cannot go negative.
- Accounts can only be closed when balance is zero.

**Database:** PostgreSQL — tables `accounts` and `transactions`.

**Tech stack:** ASP.NET Core 9, Entity Framework Core 9 + Npgsql, MediatR 12, Scalar (API docs at `/scalar`).

---

### UserService — `http://localhost:5004`

Stores user profiles (clients and employees), handles login and JWT issuance, and exposes the public key/secret used by the Gateway to validate tokens.

*Implementation in progress.*

---

### CreditService — `http://localhost:5115`

Internal service that manages credit products: tariff definitions, credit issuance, and scheduled payment deductions. Calls CoreService to debit payment amounts from client accounts.

*Implementation in progress.*

---

## Running Locally

Each service is a standalone ASP.NET Core application. Start all three from the solution root:

```bash
# Run all services (from Backend/)
dotnet run --project MyApp.CoreService
dotnet run --project MyApp.UserService
dotnet run --project MyApp.CreditService
```

Or use `docker compose up` (see [docker-compose.yml](docker-compose.yml)).

---

## Internal API Key

For local development every internal request must include:

```
X-Internal-Api-Key: dev-internal-api-key-12345
```

The value is configured in each service's `appsettings.Development.json`.
