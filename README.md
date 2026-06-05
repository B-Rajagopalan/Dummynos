# 🍕 Dummynos: Pizza Ordering Web Application

A full-stack, distributed pizza ordering web application demonstrating microservice design patterns, perimeter security, and resilient database caching.

The application features an **Angular 17** frontend client and a **Spring Boot / Spring Cloud** backend registry, configuration management server, and service cluster.

---

## 🏗️ Architecture & Component Layout

```
                  ┌───────────────────────┐
                  │   Angular 17 Client   │ (Port 4200)
                  └───────────┬───────────┘
                              │ Proxy (/pizza) -> Port 8090 / 8082
                              ▼
┌───────────────────────────────────────────────────────────┐
│ PUBLIC NETWORK                                            │
│   ┌───────────────────────────────────────────────────┐   │
│   │               Pizza Consumer Service              │   │
│   │   - Public API Gateway & Controller               │   │
│   │   - OpenFeign client-side load balancing          │   │
│   │   - Resilience4j Circuit Breaker & Rate Limiting  │   │
│   └─────────────────────────┬─────────────────────────┘   │
└─────────────────────────────┼─────────────────────────────┘
                              │ Signed Feign connection (private subnet)
┌─────────────────────────────┼─────────────────────────────┐
│ PRIVATE SUBNET              ▼                             │
│   ┌───────────────────────────────────────────────────┐   │
│   │               Pizza Producer Service              │   │
│   │   - Domain Business Logic (Isolated service)      │   │
│   │   - Custom API Key Interceptor boundary           │   │
│   │   - Redis Cache integration                       │   │
│   └───────────┬───────────────────────────┬───────────┘   │
│               │ DB Connection             │ Cache Sync    │
│               ▼                           ▼               │
│   ┌───────────────────────┐   ┌───────────────────────┐   │
│   │    MySQL Database     │   │      Redis Cache      │   │
│   └───────────────────────┘   └───────────────────────┘   │
│                                                           │
│   ┌───────────────────────┐   ┌───────────────────────┐   │
│   │  Eureka Registry      │   │  Config Server        │   │
│   │  (Service Discovery)  │   │  (Native Profile)     │   │
│   └───────────────────────┘   └───────────────────────┘   │
└───────────────────────────────────────────────────────────┘
```

### 📂 Directory Structure
* **`frontend/`**: Angular 17 Single Page Application.
* **`backend/1001_EurekaServer`**: Service Registration and Discovery server (running on port `7090`).
* **`backend/1021_Config_Server_Eureka`**: Config Server hosting dynamic properties in `native` (local file-system) profile (running on port `8888`).
* **`backend/GitFiles`**: Configuration properties files read by the Config Server.
* **`backend/Pizza_Consumer`**: Client-facing public gateway service (running on port `8090` locally / port `8082` in Docker).
* **`backend/Pizza_Producer`**: Internal business service connected to MySQL and Redis (isolated from public traffic).

---

## 🛡️ Enterprise Patterns Implemented

1. **Network Isolation**: The internal `Pizza_Producer` microservice is kept completely inside a private Docker subnet with no port exposed to the host machine.
2. **Cluster Security**: Transmissions from `Pizza_Consumer` to `Pizza_Producer` are signed with a security header token (`x-top-secret-id`) via a Feign `RequestInterceptor` and verified at the Producer gateway.
3. **Distributed Caching (Redis)**: Database retrieval uses the cache-aside pattern. Frequent read requests are cached in Redis and evicted dynamically when new orders are placed.
4. **Resilience**: Client-side traffic throttling and connection fail-safes are managed using **Resilience4j Circuit Breakers** and **Rate Limiters**.

---

## 🚀 How to Run the Application

### Option 1: Running with Docker Compose (Recommended)
You can launch the entire stack (Database, Cache, and all Microservices) in one command:

1. **Build backend packages**:
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```
2. **Build and start containers**:
   ```bash
   docker compose up --build -d
   ```
3. **Start the Frontend development server**:
   ```bash
   cd ../frontend
   npm install
   npm start
   ```

### Option 2: Running Locally (For Development)
Ensure you have **MySQL** and **Redis** running locally on their default ports.

1. **Eureka Registry**: Run the main application in `backend/1001_EurekaServer`.
2. **Config Server**: Run `backend/1021_Config_Server_Eureka`. It will read configuration from `backend/GitFiles`.
3. **Pizza Producer**: Run `backend/Pizza_Producer`.
4. **Pizza Consumer**: Run `backend/Pizza_Consumer`.
5. **Angular App**: Start the frontend development server:
   ```bash
   cd frontend
   npm install
   npm start
   ```
   Open **`http://localhost:4200`** in your browser.

---

## 🧪 Quick Test Endpoints

* **Eureka Server Console**: `http://localhost:7090`
* **API Endpoints (via Consumer Gateway)**:
  * **GET All Pizza Orders**: `GET http://localhost:8090/pizza/controller/getPizzaDetails`
  * **ADD Pizza Order**: `PUT http://localhost:8090/pizza/controller/addPizza`
    * **JSON Body**:
      ```json
      {
        "pizzaName": "VegMedium",
        "quantity": 2,
        "bill": 400.0,
        "customerContactNumber": "9543214753"
      }
      ```
  * **GET Orders by Pizza Name**: `POST http://localhost:8090/pizza/controller/getDetailsByPizzaName/{pizzaName}`
    * Example: `POST http://localhost:8090/pizza/controller/getDetailsByPizzaName/VegMedium`
  * **GET Orders by Contact Number**: `POST http://localhost:8090/pizza/controller/getDetailsByContactNumber/{contactNumber}`
    * Example: `POST http://localhost:8090/pizza/controller/getDetailsByContactNumber/9543214753`

