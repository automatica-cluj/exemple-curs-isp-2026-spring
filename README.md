# Course Examples

This folder contains standalone Spring Boot applications that complement the ISP 2026 course material. Each subfolder is an **independent, runnable project** — you can open, build, and run any of them on its own.

The examples follow a progressive structure, building on each other to introduce new concepts incrementally. All projects use **Java 17+**, **Maven**, and **Spring Boot 3**.

| Folder | Description | Key Topics |
|--------|-------------|------------|
| `bookstore-api-simple-01` | Minimal skeleton — no database, no security | IoC/DI, layered architecture, REST controllers, Bean Validation, error handling |
| `bookstore-api-persistence-02` | Adds JPA/Hibernate persistence with H2 and PostgreSQL | `@Entity`, `@ManyToOne`, `JpaRepository`, derived queries, Docker Compose |
| `bookstore-api-with-tests-03` | Adds a comprehensive test suite | JUnit 5, Mockito, `@DataJpaTest`, `@WebMvcTest`, `MockMvc`, AssertJ |
| `bookstore-api-persistence-auth-04` | Adds JWT authentication and role-based authorization | Spring Security, JWT, `@WithMockUser`, filter chains, BCrypt |
| `bookstore-api` | Full reference implementation combining all of the above | Complete bookstore API with persistence, security, testing, and Docker support |

## Getting Started

Each project includes its own `README.md` with setup instructions. The quickest way to run any example:

```bash
cd <example-folder>
mvn spring-boot:run
```

Most examples support both **H2** (default, zero setup) and **PostgreSQL** (via `docker compose up --build`).
