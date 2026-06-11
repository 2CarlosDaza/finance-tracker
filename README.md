# finance-tracker

A **Java 21 / Spring Boot 3** REST API for personal finance management. Allows users to record daily income and expenses organized by custom categories, and query summaries by day, month, or year.

Designed as a backend-only service with a clean REST API, ready to be consumed by a frontend (Angular, React, or mobile).

---

## Architecture

```
┌──────────────────────────────────────────────┐
│               finance-tracker                │
│                                              │
│  REST API (Spring MVC)                       │
│       │                                      │
│  CategoryService    TransactionService       │
│       │                    │                 │
│  CategoryRepository  TransactionRepository   │
│              │        │                      │
│           PostgreSQL (Flyway migrations)      │
└──────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 / Mockito |
| Build | Gradle |
| Infrastructure | Docker Compose |

---

## Getting Started

### Prerequisites
- Java 21+
- Docker Desktop

### Run infrastructure

```bash
docker-compose up -d
```

### Run the service

```bash
./gradlew bootRun
```

Service starts on `http://localhost:8080`

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### Categories

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/categories` | Create a category |
| `GET` | `/api/v1/categories` | List all categories |
| `GET` | `/api/v1/categories?type=EXPENSE` | Filter by type |
| `GET` | `/api/v1/categories/{id}` | Get by ID |
| `PUT` | `/api/v1/categories/{id}` | Update category |
| `DELETE` | `/api/v1/categories/{id}` | Delete category |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/transactions` | Register a transaction |
| `GET` | `/api/v1/transactions/{id}` | Get by ID |
| `PUT` | `/api/v1/transactions/{id}` | Update transaction |
| `DELETE` | `/api/v1/transactions/{id}` | Delete transaction |
| `GET` | `/api/v1/transactions/summary/day?date=2026-06-10` | Summary for a day |
| `GET` | `/api/v1/transactions/summary/month?year=2026&month=6` | Summary for a month |
| `GET` | `/api/v1/transactions/summary/year?year=2026` | Summary for a year |

---

## Example Requests

### Create a category

```json
POST /api/v1/categories
{
  "name": "Food & Groceries",
  "description": "Supermarket and restaurants",
  "type": "EXPENSE"
}
```

### Register a transaction

```json
POST /api/v1/transactions
{
  "categoryId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "amount": 45.50,
  "currency": "USD",
  "description": "Lunch at work",
  "transactionDate": "2026-06-10"
}
```

### Monthly summary response

```json
GET /api/v1/transactions/summary/month?year=2026&month=6
{
  "period": "2026-06",
  "totalIncome": 3000.00,
  "totalExpense": 1250.75,
  "balance": 1749.25,
  "byCategory": [
    {
      "categoryId": "...",
      "categoryName": "Salary",
      "type": "INCOME",
      "total": 3000.00,
      "transactionCount": 1
    },
    {
      "categoryId": "...",
      "categoryName": "Food & Groceries",
      "type": "EXPENSE",
      "total": 450.00,
      "transactionCount": 12
    }
  ]
}
```

---

## Data Model

```
categories
├── id (UUID)
├── name (unique)
├── description
├── type (INCOME | EXPENSE)
└── created_at

transactions
├── id (UUID)
├── category_id → categories
├── type (INCOME | EXPENSE)  -- derived from category
├── amount
├── currency
├── description
├── transaction_date
├── created_at
└── updated_at
```

---

## Running Tests

```bash
./gradlew test
```

---

## Roadmap

- [ ] JWT authentication (Spring Security)
- [ ] Monthly budget limits per category with alerts
- [ ] Angular frontend integration
- [ ] CSV/PDF export of transaction history

---

## Author

**Carlos Daza** — Java Backend Engineer  
[LinkedIn](https://www.linkedin.com/in/carlos-alberto-daza-murgas-a4224a217/) · [GitHub](https://github.com/2CarlosDaza)
