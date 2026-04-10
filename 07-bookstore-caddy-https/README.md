# Bookstore API — Exercise 07: HTTPS with Caddy

Same bookstore application as Exercise 06, with **Caddy** added as a reverse proxy providing HTTPS (self-signed TLS certificate).

## What's Different from Exercise 06

- **Caddy** sits in front of Spring Boot and terminates TLS on port 443
- The Spring Boot app is no longer exposed directly — only Caddy's ports (80/443) are published
- PostgreSQL remains internal to the Docker network (unchanged)

### Architecture

```
Browser ──HTTPS:443──▶ Caddy ──HTTP:8080──▶ Spring Boot ──▶ PostgreSQL
                      (TLS termination)     (internal)      (internal)
```

## Quick Start

### Docker (PostgreSQL + Caddy)

```bash
docker compose up
```

Open [https://localhost](https://localhost). Your browser will warn about the self-signed certificate — click **Advanced** > **Proceed** (this is expected).

### Development (H2 in-memory, no Caddy)

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Runs without Caddy, same as Exercise 06.

## Key Files

| File | Purpose |
|------|---------|
| `Caddyfile` | Caddy configuration — TLS mode and reverse proxy target |
| `compose.yml` | Adds Caddy service, removes direct port exposure for the app |

## Deploying to a VM (Azure)

Follow the same steps as [AzureVM_Setup.md](AzureVM_Setup.md), but open ports **80** and **443** in the Azure NSG instead of 8080.

## Build & Run

| Command | Description |
|---------|-------------|
| `docker compose up` | Build and run with PostgreSQL + Caddy (HTTPS) |
| `docker compose down -v` | Stop and remove volumes |
| `mvn spring-boot:run` | Run locally with H2 (no Caddy) |
| `mvn test` | Run all tests |

## Further Documentation

- **[DEVELOPER-GUIDE.md](DEVELOPER-GUIDE.md)** — Architecture, API reference, and glossary
- **[COLLECTIONS-GUIDE.md](COLLECTIONS-GUIDE.md)** — Java Collections patterns walkthrough
- **[AzureVM_Setup.md](AzureVM_Setup.md)** — Azure Student VM deployment guide
