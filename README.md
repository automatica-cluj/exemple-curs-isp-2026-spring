# Course Examples

This folder contains standalone Spring Boot applications that complement the ISP 2026 course material. Each subfolder is an **independent, runnable project** — you can open, build, and run any of them on its own.

The examples follow a progressive structure, building on each other to introduce new concepts incrementally. All projects use **Java 17+**, **Maven**, and **Spring Boot 3**.

| Folder | Description | Key Topics |
|--------|-------------|------------|
| `01-bookstore-api-simple` | Minimal skeleton — no database, no security | IoC/DI, layered architecture, REST controllers, Bean Validation, error handling |
| `02-bookstore-api-persistence` | Adds JPA/Hibernate persistence with H2 and PostgreSQL | `@Entity`, `@ManyToOne`, `JpaRepository`, derived queries, Docker Compose |
| `03-bookstore-api-with-tests` | Adds a comprehensive test suite | JUnit 5, Mockito, `@DataJpaTest`, `@WebMvcTest`, `MockMvc`, AssertJ |
| `04-bookstore-api-persistence-auth` | Adds JWT authentication and role-based authorization | Spring Security, JWT, `@WithMockUser`, filter chains, BCrypt |
| `05-bookstore-frontend` | Adds a vanilla HTML/CSS/JavaScript SPA frontend | `fetch()` API, JWT in `localStorage`, DOM manipulation, SPA routing |
| `06-bookstore-api-collections` | Demonstrates Java Collections Framework in a real app | Set, Map, Queue, List, `@ElementCollection`, `Collectors.groupingBy()` |

## Getting Started

Each project includes its own `README.md` with setup instructions. The quickest way to run any example:

```bash
cd <example-folder>
mvn spring-boot:run
```

Most examples support both **H2** (default, zero setup) and **PostgreSQL** (via `docker compose up --build`).
