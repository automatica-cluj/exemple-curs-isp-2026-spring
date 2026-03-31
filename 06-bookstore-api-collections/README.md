# Bookstore API — Exercise 06: Collections

A full-stack bookstore application demonstrating the **Java Collections Framework** (Set, Map, Queue, List) in a Spring Boot REST API with a vanilla JavaScript frontend.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.4.3, Spring Security (JWT), Spring Data JPA
- **Database**: H2 (dev) / PostgreSQL 16 (Docker)
- **Frontend**: Vanilla HTML/CSS/JavaScript SPA (no build tools)

## Quick Start

### Development (H2 in-memory)

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Seed data (3 authors, 3 books) is loaded automatically.

### Docker (PostgreSQL)

```bash
docker compose up
```

App runs at [http://localhost:8080](http://localhost:8080) with PostgreSQL on port 5432.

## First Steps

1. **Browse books and authors** — public, no login required
2. **Register** — first registered user becomes **ADMIN**, all others become USER
3. **Login** — unlocks cart, add-to-cart, and checkout
4. **Admin actions** — create/edit/delete books and authors, view and process orders

## Build & Run

| Command | Description |
|---------|-------------|
| `mvn spring-boot:run` | Run with H2 in-memory database |
| `mvn package` | Build executable JAR in `target/` |
| `java -jar target/bookstore-api-0.0.1-SNAPSHOT.jar` | Run the packaged JAR |
| `docker compose up` | Build and run with PostgreSQL |
| `docker compose down -v` | Stop and remove volumes |
| `mvn test` | Run all tests |

## H2 Console (dev only)

Available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) with JDBC URL `jdbc:h2:mem:bookstore`, user `sa`, no password.

## Further Documentation

- **[DEVELOPER-GUIDE.md](DEVELOPER-GUIDE.md)** — Architecture, backend/frontend design, security, API reference, deployment, and glossary of terms
- **[COLLECTIONS-GUIDE.md](COLLECTIONS-GUIDE.md)** — Detailed walkthrough of Java Collections patterns demonstrated in this exercise
- **[AzureVM_Setup.md](AzureVM_Setup.md)** — Step-by-step guide to deploying on an Azure Student VM with Docker
