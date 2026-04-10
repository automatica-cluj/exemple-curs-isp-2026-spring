# Bookstore API — Exercise 07: HTTPS with Caddy

Same bookstore application as Exercise 06, with **Caddy** added as a reverse proxy providing HTTPS with a free **DuckDNS** subdomain and automatic **Let's Encrypt** certificates.

## What's Different from Exercise 06

- **Caddy** sits in front of Spring Boot and terminates TLS on port 443
- **DuckDNS** provides a free subdomain (e.g. `my-bookstore.duckdns.org`)
- Caddy automatically obtains and renews a real Let's Encrypt certificate — no browser warnings
- The Spring Boot app is no longer exposed directly — only Caddy's ports (80/443) are published
- PostgreSQL remains internal to the Docker network (unchanged)

### Architecture

```
Browser ──HTTPS:443──▶ Caddy ──HTTP:8090──▶ Spring Boot ──▶ PostgreSQL
                      (TLS termination)     (internal)      (internal)
```

## Quick Start

### 1. Get a free subdomain

Go to [duckdns.org](https://www.duckdns.org), sign in with GitHub, create a subdomain, and point it to your VM's IP. See [HTTPS_Setup.md](HTTPS_Setup.md) for detailed instructions.

### 2. Configure the Caddyfile

Edit `Caddyfile` and replace `your-subdomain.duckdns.org` with your actual subdomain.

### 3. Deploy

```bash
docker compose up -d --build
```

Open `https://your-subdomain.duckdns.org` — you should see a padlock icon and no certificate warnings.

### Development (H2 in-memory, no Caddy)

```bash
mvn spring-boot:run
```

Open [http://localhost:8090](http://localhost:8090). Runs without Caddy.

## Key Files

| File | Purpose |
|------|---------|
| `Caddyfile` | Caddy configuration — subdomain and reverse proxy target |
| `compose.yml` | Defines Caddy, Spring Boot, and PostgreSQL services |
| `HTTPS_Setup.md` | Step-by-step HTTPS setup guide with DuckDNS |

## Build & Run

| Command | Description |
|---------|-------------|
| `docker compose up -d --build` | Build and run with PostgreSQL + Caddy (HTTPS) |
| `docker compose down -v` | Stop and remove volumes |
| `mvn spring-boot:run` | Run locally with H2 (no Caddy) |
| `mvn test` | Run all tests |

## Further Documentation

- **[HTTPS_Setup.md](HTTPS_Setup.md)** — HTTPS setup with Caddy and DuckDNS (start here)
- **[CORS.md](CORS.md)** — What CORS is, how it works, and how it's configured in this project
- **[DEVELOPER-GUIDE.md](DEVELOPER-GUIDE.md)** — Architecture, API reference, and glossary
- **[COLLECTIONS-GUIDE.md](COLLECTIONS-GUIDE.md)** — Java Collections patterns walkthrough
- **[AzureVM_Setup.md](AzureVM_Setup.md)** — Azure Student VM deployment guide
