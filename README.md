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

---

## IoT / Embedded Examples

A second set of examples demonstrating IoT concepts with an **ESP32 microcontroller**, **MQTT messaging**, and a **Spring Boot dashboard**. These projects show how to bridge the gap between embedded devices and web applications.

| Folder | Description | Key Topics |
|--------|-------------|------------|
| `01-IoT-mqtt-esp32-demo` | ESP32 publishes sensor data over MQTT; Spring Boot dashboard visualizes and controls the LED | MQTT, Arduino, PubSubClient, Eclipse Paho, REST API, Docker Compose |
| `02-IoT-mqtt-esp32-OTA` | Extends the demo with wireless firmware updates via the dashboard | OTA, HTTPUpdate, A/B flash partitions, firmware endpoints |

> **No ESP32 hardware?** The `01-IoT-mqtt-esp32-demo` includes a `simulate-device.sh` script that publishes fake sensor data to the MQTT broker, so you can test the full dashboard without a physical board.

---

