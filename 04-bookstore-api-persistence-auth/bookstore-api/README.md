# Bookstore API — Spring Boot with JPA, Security & Testing

A Spring Boot 3 application that demonstrates core patterns from the ISP 2026 course: layered architecture, REST controllers, Bean Validation, JPA/Hibernate persistence, entity relationships, JWT authentication, role-based authorization, and a comprehensive test suite.

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

The API starts on **http://localhost:8080**. Sample data (1 admin user, 3 authors, 3 books) is loaded automatically.

H2 console: **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:bookstore`, user: `sa`, no password)

### PostgreSQL (via Docker Compose)

```bash
cd examples/bookstore-api
docker compose up --build
```

This starts both PostgreSQL and the application. The API is available on **http://localhost:8080**.

## Authentication & Authorization

The API uses **JWT (JSON Web Token)** authentication. Some endpoints are public, while others require a valid token or a specific role.

### Access Rules

| Endpoint | Access |
|----------|--------|
| `POST /api/auth/register` | Public |
| `POST /api/auth/login` | Public |
| `GET /api/books/**` | Public |
| `GET /api/authors/**` | Public |
| `POST /api/books` | Authenticated |
| `PUT /api/books/{id}` | Authenticated |
| `POST /api/authors` | Authenticated |
| `DELETE /api/books/{id}` | `ADMIN` role only |

### Seeded Admin User

The H2 profile seeds an admin user you can use immediately:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | `ADMIN` |

### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "password123"
  }'
```

Response:

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

New users are assigned the `USER` role by default.

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Response:

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Using the Token

Pass the token in the `Authorization` header as a Bearer token:

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MzI5Nzg4NCwiZXhwIjoxNzczMzAxNDg0fQ.19WzfbfoK9fvr6sI4nQR5C30i3LHj_SQWNG1TbRn8pM"

# Create a book (requires authentication)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Clean Code",
    "authorId": 1,
    "isbn": "9780132350884",
    "price": 29.99
  }'

# Delete a book (requires ADMIN role)
curl -X DELETE http://localhost:8080/api/books/1 \
  -H "Authorization: Bearer $TOKEN"
```

Without a valid token, protected endpoints return **401 Unauthorized**. With a valid token but insufficient role (e.g., a `USER` trying to delete), the response is **403 Forbidden**.

## API Endpoints

### Auth

| Method | URL | Description | Status |
|--------|-----|-------------|--------|
| `POST` | `/api/auth/register` | Register a new user | 201 / 400 / 409 |
| `POST` | `/api/auth/login` | Login and get JWT token | 200 / 401 |

### Authors

| Method | URL | Auth | Description | Status |
|--------|-----|------|-------------|--------|
| `GET` | `/api/authors` | None | List all authors | 200 |
| `GET` | `/api/authors/{id}` | None | Get author by ID | 200 / 404 |
| `POST` | `/api/authors` | Bearer token | Create a new author | 201 / 400 |

### Books

| Method | URL | Auth | Description | Status |
|--------|-----|------|-------------|--------|
| `GET` | `/api/books` | None | List all books | 200 |
| `GET` | `/api/books/{id}` | None | Get book by ID | 200 / 404 |
| `POST` | `/api/books` | Bearer token | Create a new book | 201 / 400 / 404 / 409 |
| `PUT` | `/api/books/{id}` | Bearer token | Update a book | 200 / 400 / 404 / 409 |
| `DELETE` | `/api/books/{id}` | Bearer token (ADMIN) | Delete a book | 204 / 404 |

## Example Requests

**List all authors (public):**

```bash
curl http://localhost:8080/api/authors
```

**List all books (public):**

```bash
curl http://localhost:8080/api/books
```

**Get a book by ID (public):**

```bash
curl http://localhost:8080/api/books/1
```

**Create an author (authenticated):**

```bash
curl -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "firstName": "Robert",
    "lastName": "Martin"
  }'
```

**Create a book (authenticated):**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Clean Code",
    "authorId": 1,
    "isbn": "9780132350884",
    "price": 29.99
  }'
```

**Update a book (authenticated):**

```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Clean Code",
    "authorId": 1,
    "isbn": "9780132350884",
    "price": 34.99
  }'
```

**Delete a book (admin only):**

```bash
curl -X DELETE http://localhost:8080/api/books/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Request Body Formats

### Register / Login

| Field | Type | Validation |
|-------|------|------------|
| `username` | `String` | Required, 3–50 characters (register) |
| `password` | `String` | Required, 6–100 characters (register) |

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
| `isbn` | `String` | Required, non-blank |
| `price` | `BigDecimal` | Required, minimum 0.01 |

## Error Responses

Errors use Spring's default error format:

```json
{
  "timestamp": "2026-03-11T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/api/books"
}
```

| Status | When |
|--------|------|
| 400 | Validation fails (missing/invalid fields) |
| 401 | Missing or invalid JWT token |
| 403 | Valid token but insufficient role |
| 404 | Book or Author ID not found |
| 409 | Duplicate ISBN or duplicate username |

## Project Structure

```
src/main/java/com/bookstore/
├── BookstoreApplication.java          # @SpringBootApplication entry point
├── controller/
│   ├── AuthController.java            # @RestController — register + login
│   ├── AuthorController.java          # @RestController — author endpoints
│   └── BookController.java            # @RestController — book CRUD endpoints
├── service/
│   ├── AuthorService.java             # @Service — author business logic
│   └── BookService.java               # @Service — book business logic, DTO mapping
├── repository/
│   ├── AuthorRepository.java          # JpaRepository<Author, Long>
│   ├── BookRepository.java            # JpaRepository<Book, Long>
│   └── UserRepository.java            # JpaRepository<User, Long>
├── model/
│   ├── Author.java                    # @Entity — authors table
│   ├── Book.java                      # @Entity — books table, @ManyToOne to Author
│   ├── User.java                      # @Entity — users table (username, password, role)
│   └── Role.java                      # Enum — USER, ADMIN
├── dto/
│   ├── AuthRequest.java               # Login request (username + password)
│   ├── AuthResponse.java              # Token response
│   ├── RegisterRequest.java           # Registration request + validation
│   ├── AuthorRequest.java             # Inbound record + Bean Validation
│   ├── AuthorResponse.java            # Outbound record
│   ├── BookRequest.java               # Inbound record (authorId) + Bean Validation
│   └── BookResponse.java              # Outbound record (authorId + authorName)
└── security/
    ├── SecurityConfig.java            # Filter chain, endpoint access rules
    ├── JwtAuthFilter.java             # OncePerRequestFilter — extracts/validates JWT
    ├── JwtUtil.java                   # Token generation + parsing (HMAC-SHA256)
    └── CustomUserDetailsService.java  # Loads User entity for Spring Security

src/test/java/com/bookstore/
├── repository/
│   ├── AuthorRepositoryTest.java      # @DataJpaTest — entity persistence
│   └── BookRepositoryTest.java        # @DataJpaTest — existsByIsbn, unique constraint
├── service/
│   ├── AuthorServiceTest.java         # Unit test — Mockito mocks for repository
│   └── BookServiceTest.java           # Unit test — CRUD logic, edge cases
└── controller/
    ├── AuthControllerTest.java        # @WebMvcTest — register, login, validation
    ├── AuthorControllerTest.java      # @WebMvcTest — MockMvc HTTP layer
    └── BookControllerTest.java        # @WebMvcTest — endpoints, validation, auth
```

## Testing

The project includes a comprehensive test suite covering all three layers of the architecture.

### Running Tests

```bash
cd examples/bookstore-api
mvn test
```

### Test Layers

| Layer | Annotation | What it tests | Classes |
|-------|-----------|---------------|---------|
| **Repository** | `@DataJpaTest` | Entity persistence, derived queries, constraints | `AuthorRepositoryTest`, `BookRepositoryTest` |
| **Service** | `@ExtendWith(MockitoExtension.class)` | Business logic with mocked repositories | `AuthorServiceTest`, `BookServiceTest` |
| **Controller** | `@WebMvcTest` | HTTP status codes, JSON serialization, validation, auth | `AuthControllerTest`, `AuthorControllerTest`, `BookControllerTest` |

### Test Patterns Demonstrated

- **`@DataJpaTest`** — auto-configures an embedded H2 database, scans only JPA components
- **`@WebMvcTest`** — loads only the web layer, uses `MockMvc` for HTTP assertions
- **`@MockitoBean`** — replaces Spring beans with Mockito mocks in the application context
- **`@ExtendWith(MockitoExtension.class)`** + `@Mock` / `@InjectMocks` — pure unit tests without Spring context
- **`@WithMockUser`** — simulates authenticated/role-based requests in controller tests
- **`@Nested`** + `@DisplayName` — groups tests by method for readable output
- **AssertJ** — fluent assertions (`assertThat(...).isEqualTo(...)`)

## Course Concepts Demonstrated

| Concept | Where |
|---------|-------|
| `@Entity`, `@Table`, `@Id`, `@GeneratedValue` | `Book.java`, `Author.java`, `User.java` |
| `@ManyToOne` / `@OneToMany` relationships | `Book.author`, `Author.books` |
| `@JoinColumn` foreign key mapping | `Book.java` |
| `JpaRepository` with derived queries | `BookRepository.existsByIsbn()`, `UserRepository.existsByUsername()` |
| `@Transactional` on service mutations | `BookService.java` |
| JWT authentication (stateless) | `JwtUtil.java`, `JwtAuthFilter.java` |
| Role-based authorization | `SecurityConfig.java`, `Role.java` |
| `SecurityFilterChain` configuration | `SecurityConfig.java` |
| `ResponseStatusException` for error handling | `BookService.java`, `AuthorService.java`, `AuthController.java` |
| H2 in-memory database + console | `application.yml` |
| Spring profiles for PostgreSQL | `application-docker.yml` |
| Docker Compose (Postgres + app) | `compose.yml`, `Dockerfile` |
| `data.sql` seed data | `data.sql` |
| `@SpringBootApplication` + component scanning | `BookstoreApplication.java` |
| `@RestController`, HTTP method mappings | `BookController.java`, `AuthorController.java`, `AuthController.java` |
| `@Service` + constructor injection (IoC/DI) | `BookService.java`, `AuthorService.java` |
| Java records as DTOs | `BookRequest`, `BookResponse`, `AuthRequest`, `AuthResponse`, `RegisterRequest` |
| Bean Validation annotations | `BookRequest.java`, `AuthorRequest.java`, `RegisterRequest.java` |
| JUnit 5 + `@Nested` + `@DisplayName` | All test classes |
| Mockito `@Mock` / `@InjectMocks` | `BookServiceTest.java`, `AuthorServiceTest.java` |
| `@DataJpaTest` + `TestEntityManager` | `BookRepositoryTest.java`, `AuthorRepositoryTest.java` |
| `@WebMvcTest` + `MockMvc` + `@MockitoBean` | All controller test classes |
| `@WithMockUser` for security testing | `BookControllerTest.java`, `AuthorControllerTest.java` |
| AssertJ fluent assertions | All test classes |
