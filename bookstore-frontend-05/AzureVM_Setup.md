# 🚀 Azure Student VM — Ubuntu + Docker Setup Guide

A beginner-friendly guide to spinning up an Ubuntu VM on Azure Student subscription, connecting via SSH, and deploying Docker services.

---

## 📋 Table of Contents

1. [Create an Ubuntu VM on Azure](#1-create-an-ubuntu-vm-on-azure)
2. [Connect via SSH from macOS](#2-connect-via-ssh-from-macos)
3. [Configure SSH for Easy Access](#3-configure-ssh-for-easy-access)
4. [Install Git](#4-install-git)
5. [Install Docker & Docker Compose](#5-install-docker--docker-compose)
6. [Network Security & Opening Ports](#6-network-security--opening-ports)
7. [Cost Management — Stopping Your VM](#7-cost-management--stopping-your-vm)

---

## 1. Create an Ubuntu VM on Azure

1. Go to [portal.azure.com](https://portal.azure.com) and sign in with your student account
2. Click **Create a resource** → **Virtual Machine**
3. Fill in the basics:
   - **Region**: Pick one close to you
   - **Image**: Ubuntu Server 22.04 LTS
   - **Size**: Something small works for most student projects (e.g. 2 vCPUs / 4GB RAM)
   - **Authentication**: Choose **SSH public key**
   - **Username**: Azure will suggest `azureuser` — this is the standard default
4. Download the `.pem` key file when prompted — **keep it safe, you can't get it again**
5. Click **Review + Create** → **Create**

> 💡 Your student subscription gives you free credits — check [Azure for Students](https://azure.microsoft.com/en-us/free/students/) for what's included.

---

## 2. Connect via SSH from macOS

### Step 1 — Secure your key file

```bash
# Move the key to your SSH directory
mv ~/Downloads/isp2026-01_key.pem ~/.ssh/

# Set correct permissions (SSH will refuse to use the key otherwise)
chmod 600 ~/.ssh/isp2026-01_key.pem
```

### Step 2 — Connect

```bash
ssh -i ~/.ssh/isp2026-01_key.pem azureuser@<YOUR_VM_IP>
```

Replace `<YOUR_VM_IP>` with your VM's public IP address (visible in the Azure Portal under your VM's **Overview** tab).

> 💡 The default username for Azure Ubuntu VMs is **`azureuser`**. If that doesn't work, check the Azure Portal → VM → Overview.

### Common SSH Errors

| Error | Fix |
|---|---|
| `WARNING: UNPROTECTED PRIVATE KEY FILE` | Run `chmod 600 ~/.ssh/your-key.pem` |
| `Permission denied (publickey)` | Wrong username — double-check in Azure Portal |
| `Connection timed out` | Port 22 is blocked — see [Network Security](#6-network-security--opening-ports) |

---

## 3. Configure SSH for Easy Access

Instead of typing the full command every time, save a shortcut in your SSH config.

```bash
nano ~/.ssh/config
```

Add this block (replace values with yours):

```
Host my-azure-vm
    HostName      <YOUR_VM_IP>
    User          azureuser
    IdentityFile  ~/.ssh/isp2026-01_key.pem
    IdentitiesOnly yes
```

Save with `Ctrl+O` → `Enter` → `Ctrl+X`, then secure the config file:

```bash
chmod 600 ~/.ssh/config
```

Now connect with just:

```bash
ssh my-azure-vm
```

---

## 4. Install Git

```bash
sudo apt-get update
sudo apt-get install -y git

# Verify
git --version
```

---

## 5. Install Docker & Docker Compose

### Step 1 — Update & install dependencies

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
```

### Step 2 — Add Docker's official GPG key & repository

```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

### Step 3 — Install Docker Engine

```bash
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

### Step 4 — Run Docker without `sudo`

```bash
sudo usermod -aG docker $USER
newgrp docker
```

### Verify the installation

```bash
docker --version        # Should print Docker version
docker compose version  # Should print Docker Compose version
docker run hello-world  # Should run a test container
```

> 💡 Docker Compose is now built into Docker as a plugin. Use `docker compose` (space) instead of the old `docker-compose` (hyphen).

---

## 6. Network Security & Opening Ports

When you run Docker services, they listen on ports (e.g. port `80` for a web app, `3000` for Node.js, `8080` for APIs). Azure blocks all ports by default — you need to open them manually.

### Check Current Rules

1. Go to **Azure Portal** → Your VM → **Networking** → **Network security group**
2. You'll see **Inbound port rules** — by default only port **22 (SSH)** is open

### Open a Port (Azure Portal)

1. Click **Add inbound port rule**
2. Fill in:
   - **Source**: Any (or your IP for extra security)
   - **Destination port ranges**: e.g. `80` or `8080` or `3000`
   - **Protocol**: TCP
   - **Action**: Allow
   - **Priority**: any number (lower = higher priority)
   - **Name**: something descriptive like `allow-http` or `allow-app-port`
3. Click **Add**

### Open a Port (Azure CLI)

```bash
az network nsg rule create \
  --resource-group <YOUR_RESOURCE_GROUP> \
  --nsg-name <YOUR_NSG_NAME> \
  --name allow-http \
  --priority 1001 \
  --protocol Tcp \
  --destination-port-range 80 \
  --access Allow
```

### Common Ports to Open

| Service | Port |
|---|---|
| HTTP (web) | 80 |
| HTTPS (secure web) | 443 |
| Node.js / dev servers | 3000 |
| Alternative HTTP | 8080 |
| PostgreSQL | 5432 |
| MySQL | 3306 |
| MongoDB | 27017 |

> ⚠️ **Security tip**: Only open ports you actually need. Avoid opening port ranges or using `*` (any port) for inbound rules in production environments.

### Verify a Port is Reachable (from your Mac)

```bash
# Check if a port is open and responding
nc -zv <YOUR_VM_IP> 80
```

---

## 7. Cost Management — Stopping Your VM

### ✅ Always stop from the Azure Portal

Click **Stop** in the Azure Portal. This **deallocates** the VM — you stop paying for compute.

> ⚠️ If you run `sudo shutdown now` from inside the VM, it stops the OS but **the VM stays allocated** and **you keep getting charged**.

### What you pay while stopped (deallocated)

| Resource | Cost |
|---|---|
| Compute (CPU/RAM) | ✅ Free — no charge |
| OS Disk (storage) | ⚠️ ~$2–3/month |
| Public IP (static) | ⚠️ ~$3–4/month |
| **Total while stopped** | **~$5–7/month** |

### Check the state in the Portal

Your VM should show **"Stopped (deallocated)"** — not just "Stopped".

### 💡 Save even more

If you don't need to keep the same IP address, **release the public IP** when stopped to save an extra ~$3–4/month. When you restart, Azure assigns a new IP — just update your SSH config.

---

## Quick Reference Cheatsheet

```bash
# SSH connect (after config setup)
ssh my-azure-vm

# Update system
sudo apt-get update && sudo apt-get upgrade -y

# Docker basics
docker ps                        # list running containers
docker ps -a                     # list all containers
docker images                    # list images
docker compose up -d             # start services in background
docker compose down              # stop services
docker compose logs -f           # follow logs

# Check open ports on the VM
sudo ss -tlnp                    # show listening ports
```

---

*Guide based on Azure Student Subscription setup — Ubuntu 22.04 LTS, Docker Engine (latest), macOS SSH client. Small adjustments needed if you will do the setup from Windows (ssh setup part to connect from you local machine to azure vm), howevere this guide maynly explain steps to perform to setup the remote ubuntu machine so can be followed with no issue from any OS host machine with small adjustments.*
