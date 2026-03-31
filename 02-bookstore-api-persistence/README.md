# Bookstore API — Spring Boot with JPA Persistence

A Spring Boot 3 application that demonstrates core patterns from the ISP 2026 course: layered architecture, REST controllers, Bean Validation, JPA/Hibernate persistence, and entity relationships.

Supports **H2** (default, zero setup) and **PostgreSQL** (via Docker Compose).

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional, for PostgreSQL)

## Quick Start

### H2 (default — no setup needed)

```bash
cd examples/bookstore-api
mvn spring-boot:run
```

The API starts on **http://localhost:8080**. Sample data (3 authors, 3 books) is loaded automatically.

H2 console: **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:bookstore`, user: `sa`, no password)

### PostgreSQL (via Docker Compose)

```bash
cd examples/bookstore-api
docker compose up -d
mvn spring-boot:run -Dspring.profiles.active=docker
```

## API Endpoints

### Authors

| Method | URL | Description | Status |
|--------|-----|-------------|--------|
| `GET` | `/api/authors` | List all authors | 200 |
| `GET` | `/api/authors/{id}` | Get author by ID | 200 / 404 |
| `POST` | `/api/authors` | Create a new author | 201 / 400 |

### Books

| Method | URL | Description | Status |
|--------|-----|-------------|--------|
| `GET` | `/api/books` | List all books | 200 |
| `GET` | `/api/books/{id}` | Get book by ID | 200 / 404 |
| `POST` | `/api/books` | Create a new book | 201 / 400 / 404 / 409 |
| `PUT` | `/api/books/{id}` | Update a book | 200 / 400 / 404 / 409 |
| `DELETE` | `/api/books/{id}` | Delete a book | 204 / 404 |

## Example Requests

**Create an author:**

```bash
curl -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Robert",
    "lastName": "Martin"
  }'
```

**List all authors:**

```bash
curl http://localhost:8080/api/authors
```

**Create a book (using authorId):**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "authorId": 1,
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
    "authorId": 1,
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
    "authorId": null,
    "isbn": "bad",
    "price": -5
  }'
```

## Request Body Formats

### Author

| Field | Type | Validation |
|-------|------|------------|
| `firstName` | `String` | Required, non-blank |
| `lastName` | `String` | Required, non-blank |

### Book

| Field | Type | Validation |
|-------|------|------------|
| `title` | `String` | Required, non-blank |
| `authorId` | `Long` | Required, must reference existing author |
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
| 404 | Book or Author ID not found |
| 409 | Duplicate ISBN or data integrity violation |
| 500 | Unexpected server error |

## Project Structure

```
src/main/java/com/bookstore/
├── BookstoreApplication.java          # @SpringBootApplication entry point
├── controller/
│   ├── AuthorController.java          # @RestController — author endpoints
│   └── BookController.java            # @RestController — book CRUD endpoints
├── service/
│   ├── AuthorService.java             # @Service — author business logic
│   └── BookService.java               # @Service — book business logic, DTO mapping
├── repository/
│   ├── AuthorRepository.java          # JpaRepository<Author, Long>
│   └── BookRepository.java            # JpaRepository<Book, Long>
├── model/
│   ├── Author.java                    # @Entity — authors table
│   └── Book.java                      # @Entity — books table, @ManyToOne to Author
├── dto/
│   ├── AuthorRequest.java             # Inbound record + Bean Validation
│   ├── AuthorResponse.java            # Outbound record
│   ├── BookRequest.java               # Inbound record (authorId) + Bean Validation
│   └── BookResponse.java              # Outbound record (authorId + authorName)
└── exception/
    ├── ResourceNotFoundException.java # → 404
    ├── ConflictException.java         # → 409
    ├── ApiError.java                  # Error response record
    └── GlobalExceptionHandler.java    # @RestControllerAdvice
```

## Course Concepts Demonstrated

| Concept | Where |
|---------|-------|
| `@Entity`, `@Table`, `@Id`, `@GeneratedValue` | `Book.java`, `Author.java` |
| `@ManyToOne` / `@OneToMany` relationships | `Book.author`, `Author.books` |
| `@JoinColumn` foreign key mapping | `Book.java` |
| `JpaRepository` with derived queries | `BookRepository.existsByIsbn()` |
| `@Transactional` on service mutations | `BookService.java` |
| H2 in-memory database + console | `application.yml` |
| Spring profiles for PostgreSQL | `application-docker.yml` |
| Docker Compose for database | `compose.yml` |
| `data.sql` seed data | `data.sql` |
| `@SpringBootApplication` + component scanning | `BookstoreApplication.java` |
| `@RestController`, HTTP method mappings | `BookController.java`, `AuthorController.java` |
| `@Service` + constructor injection (IoC/DI) | `BookService.java`, `AuthorService.java` |
| Java records as DTOs | `BookRequest`, `BookResponse`, `AuthorRequest`, `AuthorResponse` |
| Bean Validation annotations | `BookRequest.java`, `AuthorRequest.java` |
| `@RestControllerAdvice` error handling | `GlobalExceptionHandler.java` |

## What's Intentionally Omitted

This skeleton is deliberately focused. The following are covered in later chapters:

- **Security / JWT / OAuth2** (Chapters 19–20)
- **Testing** (Chapter 21)
- **CI/CD** (Chapter 25)
- **Lombok** — omitted so students see explicit getters/setters
- **MapStruct** — omitted so students see manual DTO mapping
- **Flyway/Liquibase** — schema managed via `ddl-auto` for simplicity
- **Pagination, HATEOAS, API versioning** — kept simple for now
