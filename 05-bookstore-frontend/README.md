# Bookstore API — Example 05: Frontend

This example extends the bookstore API from Example 04 (JWT authentication) with a **vanilla HTML/CSS/JavaScript frontend** that consumes the REST API.

No Node.js, no npm, no build tools — just static files served by Spring Boot.

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional, for PostgreSQL)

## Quick Start

```bash
cd bookstore-api
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

**Seed data:** The app starts with 3 authors, 3 books, and an admin user (`admin` / `admin123`).

## What to Try

1. **Browse books** — the book list loads without login (public GET endpoint)
2. **Browse authors** — click "Authors" in the nav bar
3. **Register** — create a new account (username >= 3 chars, password >= 6 chars)
4. **Login** — sign in to see Add/Edit/Delete buttons appear
5. **Create a book** — click "+ Add Book", fill the form, submit
6. **Edit a book** — click "Edit" on any book row
7. **Delete a book** — click "Delete" (requires ADMIN role — login as `admin` / `admin123`)
8. **Logout** — clears the JWT token from localStorage

## Frontend Architecture

The frontend is a **single-page application** (SPA) built with plain browser APIs — no framework, no build step. All files live in `src/main/resources/static/` and are served automatically by Spring Boot.

```
static/
  index.html          Single HTML shell — nav bar + content container
  css/style.css       Minimal clean styling (~100 lines)
  js/
    api.js            fetch() wrappers + JWT token management
    auth.js           Login / register / logout UI
    books.js          Book CRUD UI (list, create, edit, delete)
    authors.js        Author list + create UI
    app.js            Page controller + initialization
```

### How it works

- `api.js` provides an `apiFetch()` wrapper that automatically attaches the JWT token from `localStorage` to every request
- `app.js` has a `navigate(page)` function that swaps `innerHTML` of the main content area — no page reloads
- Each UI module (`books.js`, `authors.js`, `auth.js`) renders HTML strings and attaches event listeners
- The nav bar updates dynamically based on login state (decoded from the JWT payload)

## Key Concepts Demonstrated

| Concept | Where |
|---------|-------|
| `fetch()` API + async/await | `js/api.js` — `apiFetch()` wrapper |
| JWT storage in `localStorage` | `js/api.js` — `getToken()`, `setToken()`, `clearToken()` |
| `Authorization: Bearer` header | `js/api.js` — attached automatically by `apiFetch()` |
| DOM manipulation | `js/books.js`, `js/auth.js` — `innerHTML`, event listeners |
| Frontend-backend separation | Static files talk to `/api/*` via HTTP/JSON only |
| CORS configuration | `config/WebConfig.java` — allows cross-origin API calls |
| Single-page application pattern | `js/app.js` — `navigate()` swaps content without page reload |
| XSS prevention | `js/books.js` — `escapeHtml()` for user-generated content |

## Backend Changes from Example 04

Only two backend files were changed:

1. **`SecurityConfig.java`** — added `.requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()` so Spring Security does not block the static frontend files
2. **`WebConfig.java`** (new) — CORS configuration for cross-origin API access

## CORS Configuration

The new `WebConfig.java` allows cross-origin requests from `http://localhost:*`. This is needed if you run the frontend separately (e.g., open `index.html` directly from the filesystem or serve it on a different port).

When served by Spring Boot at `http://localhost:8080`, everything is same-origin so CORS is not strictly required — but it's included to teach the concept.

## API Reference

See the full API documentation in [Example 04's README](../04-bookstore-api-persistence-auth/bookstore-api/README.md).
