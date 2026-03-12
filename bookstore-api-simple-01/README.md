# Bookstore API — Minimal Spring Boot Skeleton

A minimal, runnable Spring Boot 3 application that demonstrates core patterns from the ISP 2026 course (Chapters 16–17): IoC/DI, layered architecture, REST controllers, Bean Validation, and centralized error handling.

No database, no security, no frontend — just the essentials so you can run it immediately and explore.

## Prerequisites

- Java 17+
- Maven 3.8+

## Quick Start

```bash
cd examples/bookstore-api
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

## API Endpoints

| Method | URL | Description | Status |
|--------|-----|-------------|--------|
| `GET` | `/api/books` | List all books | 200 |
| `GET` | `/api/books/{id}` | Get book by ID | 200 / 404 |
| `POST` | `/api/books` | Create a new book | 201 / 400 / 409 |
| `PUT` | `/api/books/{id}` | Update a book | 200 / 400 / 404 / 409 |
| `DELETE` | `/api/books/{id}` | Delete a book | 204 / 404 |

## Example Requests

**Create a book:**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "price": 29.99
  }'
```

**List all books:**

```bash
curl http://localhost:8080/api/books
```

**Get a book by ID:**

```bash
curl http://localhost:8080/api/books/1
```

**Update a book:**

```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "price": 34.99
  }'
```

**Delete a book:**

```bash
curl -X DELETE http://localhost:8080/api/books/1
```

**Trigger a validation error (400):**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "",
    "author": "",
    "isbn": "bad",
    "price": -5
  }'
```

## Request Body Format

| Field | Type | Validation |
|-------|------|------------|
| `title` | `String` | Required, non-blank |
| `author` | `String` | Required, non-blank |
| `isbn` | `String` | Required, 10 or 13 digits |
| `price` | `BigDecimal` | Required, minimum 0.01 |

## Error Responses

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-03-11T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "title: Title is required, isbn: ISBN must be 10 or 13 digits",
  "path": "/api/books"
}
```

| Status | When |
|--------|------|
| 400 | Validation fails (missing/invalid fields) |
| 404 | Book ID not found |
| 409 | Duplicate ISBN |
| 500 | Unexpected server error |

## Project Structure

```
src/main/java/com/bookstore/
├── BookstoreApplication.java          # @SpringBootApplication entry point
├── controller/
│   └── BookController.java            # @RestController — CRUD endpoints
├── service/
│   └── BookService.java               # @Service — business logic, DTO mapping
├── repository/
│   └── BookRepository.java            # @Repository — in-memory ConcurrentHashMap
├── model/
│   └── Book.java                      # Domain entity (plain POJO, no JPA)
├── dto/
│   ├── BookRequest.java               # Inbound record + Bean Validation
│   └── BookResponse.java              # Outbound record
└── exception/
    ├── ResourceNotFoundException.java # → 404
    ├── ConflictException.java         # → 409
    ├── ApiError.java                  # Error response record
    └── GlobalExceptionHandler.java    # @RestControllerAdvice
```

## Course Concepts Demonstrated

| Concept (Chapters 16–17) | Where |
|---------------------------|-------|
| `@SpringBootApplication` + component scanning | `BookstoreApplication.java` |
| `@RestController`, HTTP method mappings | `BookController.java` |
| `@PathVariable`, `@RequestBody`, `@Valid` | `BookController.java` |
| `ResponseEntity` with status codes | `BookController.java` |
| `@Service` + constructor injection (IoC/DI) | `BookService.java` |
| `@Repository` stereotype | `BookRepository.java` |
| Controller → Service → Repository layering | All three layers |
| Java records as DTOs | `BookRequest`, `BookResponse` |
| Bean Validation annotations | `BookRequest.java` |
| `@RestControllerAdvice` error handling | `GlobalExceptionHandler.java` |

## What's Intentionally Omitted

This skeleton is deliberately minimal. The following are covered in later chapters:

- **Database / JPA** (Chapters 11–12) — replaced by an in-memory `ConcurrentHashMap`
- **Security / JWT / OAuth2** (Chapters 19–20)
- **Testing** (Chapter 21)
- **Docker** (Chapter 24)
- **CI/CD** (Chapter 25)
- **Lombok** — omitted so students see explicit getters/setters
- **MapStruct** — omitted so students see manual DTO mapping
- **Pagination, HATEOAS, API versioning** — kept simple for now
