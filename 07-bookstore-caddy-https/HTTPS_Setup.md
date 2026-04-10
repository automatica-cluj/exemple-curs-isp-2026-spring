# HTTPS Setup with Caddy and DuckDNS

This guide explains how to serve the bookstore application over HTTPS using **Caddy** as a reverse proxy and **DuckDNS** for a free subdomain with automatic Let's Encrypt certificates.

> **Prerequisites:** You already have a VM with Docker installed and the bookstore application ready to deploy. If not, follow [AzureVM_Setup.md](AzureVM_Setup.md) first.

---

## Table of Contents

1. [How It Works](#1-how-it-works)
2. [Create a Free Subdomain on DuckDNS](#2-create-a-free-subdomain-on-duckdns)
3. [Open Ports on Azure](#3-open-ports-on-azure)
4. [Configure the Caddyfile](#4-configure-the-caddyfile)
5. [Deploy](#5-deploy)
6. [Verify](#6-verify)
7. [What If My VM IP Changes?](#7-what-if-my-vm-ip-changes)
8. [Troubleshooting](#8-troubleshooting)
9. [Architecture Overview](#9-architecture-overview)
10. [Terms and Abbreviations](#terms-and-abbreviations)

---

## 1. How It Works

Without Caddy, the Spring Boot app is accessed directly over plain HTTP:

```
Browser ──HTTP:8080──▶ Spring Boot ──▶ PostgreSQL
```

With Caddy + DuckDNS, traffic is encrypted end-to-end between the browser and the server:

```
Browser ──HTTPS:443──▶ Caddy ──HTTP:8090──▶ Spring Boot ──▶ PostgreSQL
                      (TLS termination)     (internal)      (internal)
```

- **Caddy** is a web server that acts as a reverse proxy and automatically obtains free TLS certificates from Let's Encrypt
- **DuckDNS** provides a free subdomain (e.g. `my-app.duckdns.org`) that points to your VM's IP address
- **Let's Encrypt** is a free Certificate Authority — Caddy handles the entire certificate lifecycle (request, validation, renewal) automatically

The Spring Boot app itself is not modified — it still runs on its internal port. Only Caddy is exposed to the internet.

---

## 2. Create a Free Subdomain on DuckDNS

1. Go to [www.duckdns.org](https://www.duckdns.org)
2. Sign in with your **GitHub** account (or Google, Reddit, etc.)
3. In the **domains** section, type a subdomain name in the text field (e.g. `my-bookstore`) and click **add domain**
4. In the **current ip** field next to your new domain, enter your VM's public IP address
5. Click **update ip**

Your subdomain is now live. Verify it resolves correctly:

```bash
nslookup my-bookstore.duckdns.org
```

You should see your VM's IP address in the output. DuckDNS updates are instant — if it does not resolve, wait 1-2 minutes for DNS caches to clear, then try:

```bash
nslookup my-bookstore.duckdns.org 8.8.8.8
```

> **Note:** The free tier allows up to 5 subdomains, all of which can point to the same IP.

---

## 3. Open Ports on Azure

> **Run on: Azure Portal** (web browser)

Caddy needs two ports open:

| Port | Purpose |
|------|---------|
| **80** | Let's Encrypt certificate validation (HTTP-01 challenge) |
| **443** | HTTPS traffic from browsers |

### Add the Rules

1. Go to **Azure Portal** > Your VM > **Networking** > **Network security group**
2. Click **Add inbound port rule** and create a rule for port **80**:
   - **Source**: Any
   - **Destination port ranges**: `80`
   - **Protocol**: TCP
   - **Action**: Allow
   - **Name**: `allow-http`
3. Repeat for port **443**:
   - **Destination port ranges**: `443`
   - **Name**: `allow-https`

> **Note:** You no longer need port 8080 open — Caddy handles all external traffic on ports 80 and 443. You can remove the old 8080 rule if you had one.

---

## 4. Configure the Caddyfile

> **Run on: Azure VM** — connect via SSH first.

Edit the `Caddyfile` in your project directory:

```bash
nano Caddyfile
```

Replace the content with your DuckDNS subdomain:

```
my-bookstore.duckdns.org {
    reverse_proxy app:8090
}
```

That's the entire configuration. Caddy will:

- Listen on ports 80 and 443
- Automatically obtain a Let's Encrypt TLS certificate for your subdomain
- Redirect all HTTP traffic to HTTPS
- Proxy requests to the Spring Boot app on port 8090
- Automatically renew the certificate before it expires

Save with `Ctrl+O` > `Enter` > `Ctrl+X`.

---

## 5. Deploy

> **Run on: Azure VM**

```bash
cd ~/your-project-directory/07-bookstore-caddy-https

# Build and start all services
docker compose up -d --build

# Wait a few seconds for Caddy to obtain the certificate, then check logs
sleep 5
docker compose logs caddy --tail 20
```

Look for this line in the logs — it confirms the certificate was obtained:

```
"msg":"certificate obtained successfully","identifier":"my-bookstore.duckdns.org"
```

---

## 6. Verify

Open your browser and go to:

```
https://my-bookstore.duckdns.org
```

You should see:

- A **padlock icon** in the address bar (valid TLS certificate, no warnings)
- The bookstore application loading normally

You can also verify from the command line:

```bash
# From the VM
curl -v https://my-bookstore.duckdns.org/

# From your local machine
curl -v https://my-bookstore.duckdns.org/
```

Both should show a successful TLS handshake with a certificate issued by Let's Encrypt.

---

## 7. What If My VM IP Changes?

If you stop and deallocate your Azure VM, it may get a new public IP when restarted. When this happens:

1. Note the new IP from the Azure Portal (VM > Overview)
2. Go to [www.duckdns.org](https://www.duckdns.org)
3. Update the IP field next to your subdomain
4. Click **update ip**
5. On the VM, restart Caddy to get a fresh certificate:

```bash
docker compose restart caddy
```

> **Tip:** If you assign a **static public IP** in Azure, the IP will not change between restarts — but static IPs cost ~$3-4/month while the VM is deallocated.

---

## 8. Troubleshooting

### Certificate not obtained

Check Caddy logs:

```bash
docker compose logs caddy --tail 30
```

| Symptom | Likely cause | Fix |
|---------|-------------|-----|
| `ACME challenge failed` | Port 80 is blocked | Open port 80 in Azure NSG |
| `DNS name not found` | DuckDNS subdomain not set up | Verify at duckdns.org |
| `too many certificates` | Rate limit hit | Wait 1 hour and retry |

### App not loading (502 Bad Gateway)

Caddy is working but cannot reach Spring Boot:

```bash
# Check if the app is running
docker compose ps

# Check app logs
docker compose logs app --tail 20
```

Make sure the port in the Caddyfile (`reverse_proxy app:8090`) matches the port in `application.yml` (`server.port`).

### DNS not resolving

```bash
# Check against Google's DNS (bypasses local cache)
nslookup my-bookstore.duckdns.org 8.8.8.8
```

If it resolves against `8.8.8.8` but not your default DNS, wait a few minutes for the cache to expire.

### Full reset

If something is stuck, wipe everything and start fresh:

```bash
docker compose down
docker volume rm $(docker volume ls -q | grep caddy)
docker compose up -d
```

This removes cached certificates and forces Caddy to re-obtain them.

---

## 9. Architecture Overview

### Docker Compose Services

| Service | Image | Ports | Role |
|---------|-------|-------|------|
| `caddy` | `caddy:2-alpine` | 80, 443 (public) | Reverse proxy + TLS termination |
| `app` | Built from Dockerfile | 8090 (internal only) | Spring Boot REST API + frontend |
| `postgres` | `postgres:16-alpine` | 5432 (internal only) | Database |

### Network Flow

```
Internet
   │
   ├──:80──▶  Caddy ──▶ redirects to HTTPS
   │
   └──:443──▶ Caddy ──reverse_proxy──▶ app:8090 (Spring Boot)
                                            │
                                            └──▶ postgres:5432
```

Only Caddy is exposed to the internet. The Spring Boot app and PostgreSQL communicate on Docker's internal network and are not reachable from outside.

### Key Files

| File | Purpose |
|------|---------|
| `Caddyfile` | Caddy configuration — subdomain and reverse proxy target |
| `compose.yml` | Defines all three services and their networking |
| `Dockerfile` | Builds the Spring Boot app image |

---

## Terms and Abbreviations

| Term | Definition |
|------|-----------|
| ACME | Automatic Certificate Management Environment — the protocol Let's Encrypt uses to issue certificates |
| CA | Certificate Authority — an organization that issues TLS certificates (e.g. Let's Encrypt) |
| Caddy | A web server with automatic HTTPS — obtains and renews TLS certificates without configuration |
| DuckDNS | A free dynamic DNS service that provides subdomains under `duckdns.org` |
| HTTP-01 | A Let's Encrypt challenge type where the CA verifies domain ownership by making an HTTP request to port 80 |
| HTTPS | HTTP over TLS — encrypted web traffic, indicated by the padlock icon in browsers |
| Let's Encrypt | A free, automated Certificate Authority run by the nonprofit ISRG |
| Reverse proxy | A server that sits in front of an application and forwards client requests to it |
| SAN | Subject Alternative Name — a field in a TLS certificate listing the hostnames/IPs the certificate is valid for |
| TLS | Transport Layer Security — the protocol that encrypts HTTPS connections |
| TLS termination | The point where encrypted traffic is decrypted — in this setup, Caddy handles it so Spring Boot receives plain HTTP |
