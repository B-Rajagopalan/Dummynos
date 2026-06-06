# 🍕 Dummynos: Pizza Ordering Web Application

A full-stack, distributed pizza ordering web application demonstrating microservice design patterns, perimeter security, and resilient database caching.

The application features an **Angular 17** frontend client and a **Spring Boot / Spring Cloud** backend registry, configuration management server, and service cluster.

---

## 🏗️ Architecture & Component Layout

<table align="center">
<tr>
<td>
<pre>
                  ┌───────────────────────┐
                  │   Angular 17 Client   │ (Port 4200)
                  └───────────┬───────────┘
                              │ Proxy (/pizza) -> Port 8090
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
</pre>
</td>
</tr>
</table>

### 📂 Directory Structure
* **`frontend/`**: Angular 17 Single Page Application.
* **`backend/EurekaServer`**: Service Registration and Discovery server (running on port `7090`).
* **`backend/ConfigServer`**: Config Server hosting dynamic properties in `native` (local file-system) profile (running on port `8888`).
* **`backend/GitFiles`**: Configuration properties files read by the Config Server.
* **`backend/Pizza_Consumer`**: Client-facing public gateway service (running on port `8090`).
* **`backend/Pizza_Producer`**: Internal business service connected to MySQL and Redis (isolated from public traffic).

---

## 🛡️ Enterprise Patterns Implemented

1. **Network Isolation**: The internal `Pizza_Producer` microservice is kept completely inside a private Docker subnet with no port exposed to the host machine.
2. **Cluster Security**: Transmissions from `Pizza_Consumer` to `Pizza_Producer` are signed with a security header token (`x-top-secret-id`) via a Feign `RequestInterceptor` and verified at the Producer gateway.
3. **Distributed Caching (Redis)**: Database retrieval uses the cache-aside pattern. Frequent read requests are cached in Redis and evicted dynamically when new orders are placed.
4. **Resilience**: Client-side traffic throttling and connection fail-safes are managed using **Resilience4j Circuit Breakers** and **Rate Limiters**.

---

## 🚀 How to Run the Application

### Running with Docker Compose

To run the application using Docker, **Docker must be installed, and the Docker Engine/Desktop must be opened and in a running state**.

1. **Build backend packages (JAR)**:
   Since each service is a separate Maven project, you must run the `clean package` script for each microservice. 

    **For Windows PowerShell:**
    ```powershell
    cd backend/EurekaServer; mvn clean package -DskipTests
    cd ../ConfigServer; mvn clean package -DskipTests
    cd ../Pizza_Producer; mvn clean package -DskipTests
    cd ../Pizza_Consumer; mvn clean package -DskipTests
    cd ..
    ```
 
    **For Command Prompt (CMD) or Git Bash / Linux / macOS:**
    ```bash
    cd backend/EurekaServer && mvn clean package -DskipTests
    cd ../ConfigServer && mvn clean package -DskipTests
    cd ../Pizza_Producer && mvn clean package -DskipTests
    cd ../Pizza_Consumer && mvn clean package -DskipTests
    cd ..
    ```

   You will get the JAR files in the target folder of each microservice.
   Example: `backend/Pizza_Producer/target/Pizza_Producer-0.0.1-SNAPSHOT.jar`

   > [!IMPORTANT]
   > **Code Changes**: Whenever you make any changes to the source code of any backend microservice, you must re-run the respective `mvn clean package` command to generate the latest JAR files before running Docker Compose to ensure the changes are reflected.

2. **Build and start containers**:
   From the root directory, navigate to the `backend/` directory and start the services using Docker Compose:
   ```bash
   cd backend
   docker compose up --build -d
   ```
3. **Monitor and Manage Containers**:
   * **Docker Desktop**: You can view and manage the running containers and images directly in the **Docker Desktop** app.
   * **Stopping the application**: To stop and bring down the running containers, run the following command in the `backend/` directory:
     ```bash
     docker compose down
     ```

---

### Option 2: Running Locally (Without Docker)

To run the services locally without containerization:

1. **Prerequisites & Databases Setup**:
   * **Redis**: Ensure a Redis instance is running locally on port `6379`.
   * **MySQL Setup**: A local MySQL server instance running on port `3306` is required:
     * Create a database named `pizza_db`.
     * Ensure the username is `root` and password is `raja` (default credentials). If using different credentials, update them in [pizzaproducer.properties](file:///d:/Git%20projects/Dummynos/backend/GitFiles/pizzaproducer.properties).
     * Initialize the schema by executing the SQL script found in [Pizza.sql](file:///d:/Git%20projects/Dummynos/backend/Pizza_Producer/src/main/resources/Pizza.sql).
2. **Run Backend Services**:
   Start each of the backend services individually in your IDE or from the command line in the following order:
    * **Eureka Registry**: Run the main application in `backend/EurekaServer`.
    * **Config Server**: Run `backend/ConfigServer` (reads configuration from `backend/GitFiles`).
   * **Pizza Producer**: Run `backend/Pizza_Producer`.
   * **Pizza Consumer**: Run `backend/Pizza_Consumer`.

---

### 🖥️ Running the Angular Frontend (Common to both options)

Once the backend services are up and running (either via Docker Compose or running locally), start the Angular application:

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install the frontend dependencies:
   ```bash
   npm install
   ```
3. Start the frontend development server:
   ```bash
   npm start
   ```
4. Access the web application in your browser at:
   **`http://localhost:4200`**

> [!TIP]
> **🌐 Access the Pre-Hosted Frontend (Alternative)**
> If you do not wish to build and run the Angular application locally, the frontend has been compiled and pre-hosted on GitHub Pages:
> 👉 **[Dummynos Web Portal](https://b-rajagopalan.github.io/Dummynos/)**
> 
> *To use the hosted frontend, simply launch the local backend containers on your machine (`docker compose up -d`). The hosted portal will communicate directly with your local API gateway!*
> 
> > [!IMPORTANT]
> > **First-Time Browser Warning:** Since the hosted frontend is served over HTTPS (`https://...`) while requesting data from your local machine over HTTP (`http://localhost:8090`), modern browsers (like Chrome or Edge) will trigger a security prompt asking you to allow local network/insecure content access (Private Network Access). Please **allow/approve** this connection when prompted so the portal can communicate with your local Docker backend.

---

## 🧪 Quick Test Endpoints

* **Eureka Server Console**: `http://localhost:7090`
* **GET All Pizza Orders**: `GET http://localhost:8090/pizza/controller/getPizzaDetails`
* **ADD Pizza Order**: `PUT http://localhost:8090/pizza/controller/addPizza`
  * **JSON Body**:
    ```json
    {
      "pizzaName": "BBQ Chicken",
      "quantity": 2,
      "bill": 400.0,
      "customerContactNumber": "9543214753"
    }
    ```
* **GET Orders by Pizza Name**: `POST http://localhost:8090/pizza/controller/getDetailsByPizzaName/{pizzaName}`
  * Example: `POST http://localhost:8090/pizza/controller/getDetailsByPizzaName/BBQ Chicken`
* **GET Orders by Contact Number**: `POST http://localhost:8090/pizza/controller/getDetailsByContactNumber/{contactNumber}`
  * Example: `POST http://localhost:8090/pizza/controller/getDetailsByContactNumber/9543214753`

