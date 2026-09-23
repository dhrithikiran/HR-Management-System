# HRMS Microservices Architecture

## 1. System Overview & Evolution

This document details the transition of the HR Management System from a single-JVM modular monolith into a distributed microservices architecture consisting of independently runnable services communicating over standard HTTP/REST network protocols.

---

## 2. Architecture Comparison: Before vs. After

### Before Architecture (Monolithic JVM)
* **Runtime**: Single Java Virtual Machine (JVM) executing all modules inside one desktop process (`EmployeeManagementApp`).
* **Deployment Unit**: Single monolithic JAR file (`eims-desktop-1.0-SNAPSHOT.jar`).
* **Communication**: In-process Java method calls (e.g. `CandidateServiceImpl` directly instantiated `new OnboardingServiceImpl()` and called in-memory methods).
* **Service Boundaries**: Leaky; Onboarding service directly instantiated `CandidateRepositoryImpl` and queried Recruitment's `candidate` table.
* **Failure Blast Radius**: An issue or crash in any module halts the entire application.

```
+-------------------------------------------------------------------+
|                     Single Monolithic JVM Process                 |
|                                                                   |
|   [ CandidateServiceImpl ]  ---(In-Memory Direct Method Call)---> |
|                                   [ OnboardingServiceImpl ]       |
|              |                                 |                  |
|              v                                 v                  |
|   [ CandidateRepositoryImpl ]       [ CandidateRepositoryImpl ]   |
|              \                                 /                  |
|               v                               v                   |
|                      [ Shared MySQL Database ]                    |
+-------------------------------------------------------------------+
```

---

### After Architecture (Distributed Microservices)
* **Runtime**: Independent Operating System processes with dedicated network ports.
  * **Recruitment Microservice**: Runs on port `8081`.
  * **Onboarding Microservice**: Runs on port `8082`.
* **Deployment Units**: Independent Maven projects (`microservices/recruitment-service` and `microservices/onboarding-service`).
* **Communication**: Real HTTP REST network requests over TCP/IP sockets (`POST http://localhost:8082/onboarding`) using `java.net.http.HttpClient` with JSON serialization.
* **Service Boundaries**: Strict isolation. Onboarding Service has zero dependencies on Recruitment repositories or entities. All candidate information needed for onboarding is passed via the HTTP request payload.
* **Fault Isolation & Resilience**: If Onboarding Service goes down, Recruitment Service remains online, handles the failure gracefully with HTTP 503, and allows retry without crashing.

```
   [ Client / Desktop App / Integration Test ]
                 |
                 | HTTP REST (Port 8081)
                 v
   +---------------------------------------------+
   |        Recruitment Microservice             |
   |              (Port 8081)                    |
   |                                             |
   |   [ CandidateController ]                   |
   |   [ CandidateServiceImpl ]                  |
   |   [ OnboardingServiceClient ]               |
   |   [ CandidateRepositoryImpl ]               |
   +---------------------------------------------+
                 |
                 | HTTP POST /onboarding (Port 8082)
                 | Payload: { candidateId, candidateName, contactInfo }
                 v
   +---------------------------------------------+
   |        Onboarding Microservice              |
   |              (Port 8082)                    |
   |                                             |
   |   [ OnboardingController ]                  |
   |   [ OnboardingServiceImpl ]                 |
   |   [ OnboardingRepositoryImpl ]              |
   +---------------------------------------------+
        |                               |
        v                               v
 [ candidate table ]         [ onboarding_record table ]
```

---

## 3. Why These Components Are Genuinely Microservices

1. **Independently Runnable**: Each service contains its own `main()` entrypoint (`RecruitmentServiceApp` and `OnboardingServiceApp`) and embedded HTTP server.
2. **Independently Deployable**: Each service has its own `pom.xml`, build lifecycle, dependencies, and can be started, stopped, updated, or scaled on its own port without restarting the other.
3. **Separate Processes & Network Ports**: They run as distinct OS processes with dedicated TCP ports (`8081` vs `8082`).
4. **Network-Based Inter-Service Communication**: Service-to-service communication occurs strictly via HTTP REST over network sockets using standard JSON payloads. No shared in-memory object references or direct Java method invocations exist between services.
5. **Decoupled Data Boundaries**: Onboarding Service does not access the `candidate` table or Recruitment repository. Candidate data is received over HTTP.

---

## 4. Exact Request Flow for Candidate Selection

When a candidate is selected:

1. **Client Request**: Client sends `POST http://localhost:8081/candidates/{id}/select`.
2. **Recruitment Processing**: `CandidateController` delegates to `CandidateServiceImpl`.
3. **Candidate Status Transition**: `CandidateServiceImpl` verifies the candidate exists in the database and updates its status to `SELECTED`.
4. **Inter-Service HTTP Call**: `OnboardingServiceClient` constructs a JSON payload:
   ```json
   {
     "candidateId": "CND-001",
     "candidateName": "Dhrithi Kiran",
     "contactInfo": "dhrithi@example.com"
   }
   ```
   and sends an HTTP POST request to `http://localhost:8082/onboarding` with a 3-second connection timeout.
5. **Onboarding Processing**: `OnboardingController` receives the request on port `8082`, validates the payload, generates an `ONB-XXX` record, and persists it to the `onboarding_record` table.
6. **Onboarding Response**: Onboarding Service responds with HTTP 201 Created and the created onboarding record JSON.
7. **Recruitment Final Response**: Recruitment Service receives the 201 response and returns HTTP 200 OK to the client:
   ```json
   {
     "status": "SUCCESS",
     "message": "Candidate selected and onboarding record created successfully via HTTP inter-service call.",
     "candidate": {
       "candidateId": "CND-001",
       "candidateName": "Dhrithi Kiran",
       "contactInfo": "dhrithi@example.com",
       "applicationStatus": "SELECTED"
     },
     "onboarding": {
       "onboardingId": "ONB-001",
       "assignedEmployeeId": "CND-001",
       "employeeName": "Dhrithi Kiran",
       "backgroundCheckStatus": "PENDING",
       "documentVerificationStatus": "PENDING",
       "verifiedRecord": false,
       "pipelineStatus": "EMPLOYEE_ASSIGNED"
     },
     "onboardingInitiated": true
   }
   ```

---

## 5. Failure Handling & Resilience

When the Onboarding Service is unreachable or down:
* **No In-Memory Cascading Failure**: Recruitment Service does not crash.
* **Network Exception Catching**: `OnboardingServiceClient` catches `IOException` / connection timeouts.
* **Transparent Error Reporting**: Recruitment Service logs the network failure and returns an **HTTP 503 Service Unavailable** response:
  ```json
  {
    "status": "PARTIAL_SUCCESS",
    "message": "Candidate status updated to SELECTED in Recruitment database, but inter-service HTTP call to Onboarding Service failed.",
    "error": "Onboarding Service unreachable at http://localhost:8082/onboarding",
    "candidate": {
      "candidateId": "CND-002",
      "candidateName": "Alex Rivera",
      "applicationStatus": "SELECTED"
    },
    "onboardingInitiated": false
  }
  ```
* **Service Recovery**: Once Onboarding Service is restarted, subsequent or retried requests succeed immediately with HTTP 200 and create the onboarding record.

---

## 6. REST API Endpoint Reference

### Recruitment Microservice (Port 8081)

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/health` | Service health check | `200 OK` |
| `GET` | `/candidates` | List all candidates (optional `?status=...`) | `200 OK` |
| `GET` | `/candidates/{id}` | Get candidate by ID | `200 OK`, `404 Not Found` |
| `POST` | `/candidates` | Create new candidate | `201 Created`, `400 Bad Request` |
| `POST` | `/candidates/{id}/select` | Select candidate and initiate onboarding via HTTP | `200 OK`, `404 Not Found`, `503 Service Unavailable` |
| `DELETE` | `/candidates/{id}` | Delete candidate | `200 OK`, `404 Not Found` |

### Onboarding Microservice (Port 8082)

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/health` | Service health check | `200 OK` |
| `GET` | `/onboarding` | List all onboarding records | `200 OK` |
| `GET` | `/onboarding/{id}` | Get onboarding record by candidate or onboarding ID | `200 OK`, `404 Not Found` |
| `POST` | `/onboarding` | Create onboarding record from candidate payload | `201 Created`, `400 Bad Request` |
| `DELETE` | `/onboarding/{id}` | Delete onboarding record | `200 OK`, `404 Not Found` |

---

## 7. How to Build, Run, and Test

### Prerequisites
* Java 21+
* Maven 3.6+
* MySQL 8.0 running on `localhost:3306` with database `hr_ooad`
* Set `DB_PASSWORD` environment variable:
  ```bash
  export DB_PASSWORD="your_mysql_password"
  ```

### Start Services Individually

**Terminal 1 — Recruitment Service (Port 8081):**
```bash
export DB_PASSWORD="your_mysql_password"
./run-recruitment-service.sh
```

**Terminal 2 — Onboarding Service (Port 8082):**
```bash
export DB_PASSWORD="your_mysql_password"
./run-onboarding-service.sh
```

### Start All Services in Background
```bash
export DB_PASSWORD="your_mysql_password"
./run-all-services.sh
```

### Execute Automated Integration / Smoke Test
```bash
export DB_PASSWORD="your_mysql_password"
./smoke-test.sh
```

---

## 8. Realistic Limitations & Architecture Trade-offs

* **Scale**: Built as a clean, educational-scale distributed microservices prototype using lightweight, built-in Java HTTP servers and standard JDBC/Jackson without heavy cloud dependencies.
* **Shared Database Instance**: Both services currently connect to the same MySQL database instance (`localhost:3306/hr_ooad`) for local simplicity, though their table ownership and repository code boundaries are strictly partitioned. In an enterprise production deployment, each service would have its own dedicated database instance.
* **Synchronous REST vs. Asynchronous Messaging**: Candidate selection triggers onboarding via synchronous HTTP REST with timeout handling. In higher-throughput systems, an asynchronous message broker (e.g. RabbitMQ/Kafka) could be introduced for event-driven candidate selection events.
