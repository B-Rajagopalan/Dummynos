# Pizza Ordering Web Application

A production-grade, distributed microservices application for managing pizza orders, built with **Spring Boot** and **Spring Cloud**. 

This repository demonstrates modern cloud-native architecture patterns, security boundaries, and resilience strategies, running inside a fully containerized environment orchestrated by **Docker Compose**.

---

## 🏗️ Architecture Overview

The system consists of four custom microservices along with supporting database and caching infrastructures:

```
                  ┌───────────────────────┐
                  │      Web Client       │
                  └───────────┬───────────┘
                              │ Port 8082 (Public API)
                              ▼
┌───────────────────────────────────────────────────────────┐
│ PUBLIC NETWORK                                            │
│   ┌───────────────────────────────────────────────────┐   │
│   │                  Pizza Consumer                   │   │
│   │   - Exposes REST endpoints to the outer world     │   │
│   │   - Client-side load balancing via OpenFeign      │   │
│   │   - Resilience4j Circuit Breaker & Rate Limiter   │   │
│   └─────────────────────────┬─────────────────────────┘   │
└─────────────────────────────┼─────────────────────────────┘
                              │ Feign Connection (Private Subnet)
┌─────────────────────────────┼─────────────────────────────┐
│ PRIVATE NETWORK             ▼                             │
│   ┌───────────────────────────────────────────────────┐   │
│   │                  Pizza Producer                   │   │
│   │   - Isolated domain logic (Internal service)      │   │
│   │   - Spring Cache abstraction backing Database     │   │
│   │   - Custom API Key filter boundary (Security)     │   │
│   └───────────┬───────────────────────────┬───────────┘   │
│               │ DB Connection             │ Cache Sync    │
│               ▼                           ▼               │
│   ┌───────────────────────┐   ┌───────────────────────┐   │
│   │    MySQL Database     │   │      Redis Cache      │   │
│   └───────────────────────┘   └───────────────────────┘   │
│                                                           │
│   ┌───────────────────────┐   ┌───────────────────────┐   │
│   │     Eureka Server     │   │     Config Server     │   │
│   │  (Service Discovery)  │   │  (Global properties)  │   │
│   └───────────────────────┘   └───────────────────────┘   │
└───────────────────────────────────────────────────────────┘
```

### 🛰️ Core Microservices
* **Eureka Server (`1001_EurekaServer`):** Manages dynamic service registration and discovery.
* **Config Server (`1021_Config_Server_Eureka`):** Centralizes application settings from a local Git repository.
* **Pizza Consumer (`Pizza_Consumer`):** The edge service exposing client-facing controllers.
* **Pizza Producer (`Pizza_Producer`):** The internal data processor communicating with MySQL and Redis.

---

## 🛡️ Enterprise Patterns Implemented

### 1. Perimeter Security (Network Isolation)
To protect internal databases and sensitive domain services, the `Pizza_Producer` has **no mapped host ports** in the `docker-compose.yml` configuration. It is attached strictly to a `private-net` Docker network. The only way to call the Producer is through the `Pizza_Consumer`, which spans both public (`public-net`) and private networks.

### 2. Service Boundary Interceptors
In addition to network rules, application-level security prevents unauthorized traffic inside the cluster:
* **Consumer-Side:** A Feign `RequestInterceptor` automatically signs all outgoing requests with a custom token header (`x-top-secret-id`).
* **Producer-Side:** A Spring `HandlerInterceptor` intercepts all incoming requests to ensure they carry a valid signature header, returning a `403 Forbidden` response to unauthorized clients.

### 3. Distributed Cache-Aside Pattern (Redis)
Database queries are optimized using **Spring Cache backed by a Redis container**:
* **Cache Read (`@Cacheable`):** Pizza list queries first query Redis. If present (cache hit), data returns instantly, bypassing the database.
* **Cache Eviction (`@CacheEvict`):** Whenever a new order is placed, the cache key is immediately deleted from Redis. The next read operation will perform a database fetch and populate the cache with fresh data, ensuring data consistency.

### 4. Circuit Breaker & Rate Limiting (Resilience4j)
* **Circuit Breaker:** If the Producer slows down or crashes, the Consumer automatically opens the circuit and diverts requests to a local fallback method, preventing system-wide thread starvation.
* **Rate Limiting:** Protects the Consumer from DDoS attacks by limiting client request throughput.

### 5. Orchestrated Container Startup Order
Uses Docker Compose **healthchecks** to orchestrate startup dependencies:
1. **Eureka** starts and registers health.
2. **Config Server** launches only after Eureka is healthy.
3. **Database & Cache** start up.
4. **Producer** launches only after Config Server, DB, and Cache are ready.
5. **Consumer** starts up last.

---

## 🚀 How to Run Locally

### Prerequisites
* Java 17+ (JDK)
* Maven 3.x
* Docker Desktop & WSL 2 (for Windows)

### 1. Compile the Source Code
Build the `.jar` binaries for the services:
```bash
mvn clean package -DskipTests
```

### 2. Launch the Application Container
Start the infrastructure and microservice containers in the background:
```bash
docker compose up --build -d
```

### 3. (Optional) Scale the Producer
To run client-side load-balancing locally across multiple Producer instances without port conflicts:
```bash
docker compose up --scale pizza-producer=3 -d
```

---

## 🧪 Testing & Verification

### 1. Eureka Registry
Verify that all services are online:
👉 Open **`http://localhost:7090`** in your browser.

### 2. Adding a Pizza (PUT)
Submit an order through the consumer:
* **Endpoint:** `PUT http://localhost:8082/pizza/controller/addPizza`
* **JSON Body:**
  ```json
  {
    "pizzaName": "VegMedium",
    "quantity": 2,
    "bill": 400.0,
    "customerContactNumber": "9543214753"
  }
  ```

### 3. Fetching Pizza Details (GET)
* **Endpoint:** `GET http://localhost:8082/pizza/controller/getPizzaDetails`
* **First Request (Cache Miss):** Fetches from database (see `"--> Fetching pizza details from the Database..."` print in `pizza-producer` logs).
* **Second Request (Cache Hit):** Bypasses the database entirely, reading directly from Redis.

### 4. Verify Network Isolation
Test direct access to the Producer:
* **Endpoint:** `GET http://localhost:8081/pizza/controller/getPizzaDetails`
* **Expected Result:** Connection Refused (port blocked by design).