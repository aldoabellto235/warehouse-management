# Warehouse Management System

A RESTful API for managing a shop's warehouse inventory — items, variants, pricing, and stock.

Built with **Java 21**, **Spring Boot 3.2**, **PostgreSQL**, and **Docker Compose**.
Architecture: **Clean Architecture + CQRS**.

---

## How to Run

### Prerequisites
- Docker & Docker Compose

### Steps

```bash
# 1. Clone and enter the directory
git clone git@github-aldo2017:aldoabellto235/warehouse-management.git
cd warehouse-management

# 2. Copy environment file
cp .env.example .env

# 3. Start everything (PostgreSQL + app)
docker compose up --build
```

The API is available at `http://localhost:8585`.

| URL | Description |
|---|---|
| `http://localhost:8585/swagger-ui` | Swagger UI — interactive API explorer |
| `http://localhost:8585/api-docs` | Raw OpenAPI JSON spec |

For local development without Docker:
```bash
# Start only the database
docker compose up postgres -d

# Run the app with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Swagger / API Documentation

The project includes interactive API documentation powered by **SpringDoc OpenAPI (Swagger UI)**.

### Access

| URL | Description |
|---|---|
| `http://localhost:8585/swagger-ui` | Swagger UI — try out every endpoint in the browser |
| `http://localhost:8585/api-docs` | OpenAPI 3.0 JSON spec (importable into Postman / Insomnia) |

### Swagger UI features
- **Try it out** is enabled by default on all endpoints
- All request fields include examples and validation descriptions
- All responses document every possible HTTP status code (200, 201, 204, 400, 404, 409, 422)
- Endpoints are grouped into tags: **Items**, **Stock**, **Sales**, and **Reports**

### Import into Postman
1. Open Postman → **Import**
2. Paste `http://localhost:8585/api-docs`
3. Postman generates a full collection from the OpenAPI spec

---

## Standard Response Format

All endpoints follow a consistent response envelope.

### Success — single data
```json
{
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
  },
  "status": "success"
}
```

### Success — paginated list
```json
{
  "data": [
    { "id": "...", "name": "T-Shirt", "basePrice": 29.99 }
  ],
  "attribute": {
    "page": 0,
    "size": 20,
    "totalSize": 300
  },
  "status": "success"
}
```

### Error
```json
{
  "data": {},
  "error": {
    "timestamp": "2025-01-01T00:00:00Z",
    "status": 404,
    "error": "NOT_FOUND",
    "message": "Item with id '...' not found",
    "path": "/api/v1/items/..."
  },
  "status": "error"
}
```

---

## Design Decisions

### Clean Architecture
The codebase is split into four strict layers with inward-only dependencies:

| Layer | Package | Responsibility |
|---|---|---|
| Domain | `domain` | Pure business rules — entities, value objects, repository interfaces |
| Application | `application` | Use-cases as Commands and Queries (CQRS) |
| Infrastructure | `infrastructure` | JPA entities, Spring Data, DB mappers |
| API | `api` | REST controllers, request/response DTOs |

The **Domain layer has zero framework dependencies** — it is pure Java. JPA annotations live only in `infrastructure/persistence/entity`, never on domain models.

### CQRS
Every use-case is either a **Command** (write, no return data) or a **Query** (read, no state change). Each has its own dedicated handler. This keeps handlers small, focused, and independently testable.

### Out-of-stock prevention
The business rule lives inside the `Stock` domain entity (`Stock.deduct()`). It throws `InsufficientStockException` before any quantity goes below zero. The database also enforces `CHECK (quantity >= 0)` as a safety net. The sell endpoint (`POST /api/v1/items/{itemId}/stock/sell`) returns `422 Unprocessable Entity` when stock is insufficient.

### Variants & Stock
- An item **without** variants tracks stock at item level (`variantId = null`).
- An item **with** variants tracks stock per variant.
- Variant `attributes` (e.g., `{"color": "red", "size": "L"}`) are stored as **JSONB** in PostgreSQL for flexibility.
- SKUs are globally unique and normalised to uppercase.

### Database
- Flyway manages all schema migrations under `resources/db/migration/`.
- UUIDs as primary keys (`gen_random_uuid()`).
- All timestamps use `TIMESTAMPTZ`.
- Cascade delete: deleting an item removes its variants and stock records.

---

## Assumptions

- An item can have zero or more variants. If variants exist, stock should be tracked at variant level.
- "Selling" means deducting from stock. A separate stock-adjust endpoint handles restocking by warehouse staff.
- No authentication/authorisation is required for this assessment.
- Pagination defaults: page 0, size 20, max 100.

---

## API Endpoints

### Items

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/items` | Create a new item |
| `GET` | `/api/v1/items?name=&inStockOnly=&page=&size=` | List / search / filter items (paginated) |
| `GET` | `/api/v1/items/{id}` | Get item by ID |
| `GET` | `/api/v1/items/{id}/summary` | Full item summary — variants, stock, availability |
| `PUT` | `/api/v1/items/{id}` | Update item |
| `DELETE` | `/api/v1/items/{id}` | Delete item |

### Variants

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/items/{itemId}/variants` | Add a variant to an item |
| `GET` | `/api/v1/items/{itemId}/variants` | List all variants for an item |
| `PUT` | `/api/v1/items/{itemId}/variants/{variantId}` | Update a variant |
| `DELETE` | `/api/v1/items/{itemId}/variants/{variantId}` | Delete a variant |

### Stock

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/items/{itemId}/stock` | List all stock entries for an item |
| `GET` | `/api/v1/items/{itemId}/stock/level?variantId=` | Get stock level (variant or item-level) |
| `PATCH` | `/api/v1/items/{itemId}/stock/adjust` | Adjust stock (restock or manual deduct) |
| `POST` | `/api/v1/items/{itemId}/stock/sell` | Sell — prevents overselling (422 if insufficient) |

### Sales

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/items/{itemId}/sales?variantId=&page=&size=` | Sales history for an item (optionally filtered by variant) |

### Reports

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/reports/low-stock?threshold=5&page=&size=` | Low stock report — items/variants at or below threshold, sorted by qty asc |

---

## Example Requests

### Create an item
```bash
curl -X POST http://localhost:8585/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{"name":"T-Shirt","description":"Cotton crew neck","basePrice":29.99}'
```

### Add a variant
```bash
curl -X POST http://localhost:8585/api/v1/items/{itemId}/variants \
  -H "Content-Type: application/json" \
  -d '{"name":"Red / Large","sku":"TSHIRT-RED-L","price":29.99,"attributes":{"color":"red","size":"L"}}'
```

### Restock
```bash
curl -X PATCH http://localhost:8585/api/v1/items/{itemId}/stock/adjust \
  -H "Content-Type: application/json" \
  -d '{"variantId":"{variantId}","delta":100}'
```

### Sell
```bash
curl -X POST http://localhost:8585/api/v1/items/{itemId}/stock/sell \
  -H "Content-Type: application/json" \
  -d '{"variantId":"{variantId}","quantity":1}'
```

### Try to oversell (returns 422)
```bash
curl -X POST http://localhost:8585/api/v1/items/{itemId}/stock/sell \
  -H "Content-Type: application/json" \
  -d '{"variantId":"{variantId}","quantity":9999}'
```

### Search & filter items
```bash
# Filter by name
curl "http://localhost:8585/api/v1/items?name=shirt&page=0&size=10"

# Only items with stock available
curl "http://localhost:8585/api/v1/items?inStockOnly=true"
```

### Item summary (variants + stock)
```bash
curl http://localhost:8585/api/v1/items/{itemId}/summary
```

### Sales history
```bash
# All sales for an item
curl "http://localhost:8585/api/v1/items/{itemId}/sales?page=0&size=20"

# Sales for a specific variant
curl "http://localhost:8585/api/v1/items/{itemId}/sales?variantId={variantId}"
```

### Low stock report
```bash
# Items/variants with stock <= 5 (default threshold)
curl "http://localhost:8585/api/v1/reports/low-stock"

# Custom threshold
curl "http://localhost:8585/api/v1/reports/low-stock?threshold=10"
```
