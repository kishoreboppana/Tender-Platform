# TMS Phase 0 — Local Environment (No DB, No Git, No Docker)

Minimal **Tender Management Platform** demo for local review:

- **3 sample screens:** Login, Dashboard, Tender List
- **Spring Boot** backend with mock REST APIs (no database)
- **Static HTML/JS** UI served by Spring Boot (no Node.js required for this phase)
- **No** PostgreSQL, **no** Docker, **no** Git setup in this phase

Full v9.2.2 stack (Java 21, React/Vite, PostgreSQL, Docker) will be added in later phases.

---

## 1. Required stack (install on Windows)

| Tool | Phase 0 minimum | Verify |
|------|-----------------|--------|
| **JDK** | 8+ (Java 8 works with current Maven setup) | `java -version` |
| **Maven** | 3.6+ | `mvn -version` |

**Note:** This Phase 0 build targets **Java 8** so it works when Maven uses JDK 8 (common on corporate machines). For the full v9.2.2 project you will upgrade to JDK 21 later.

### Optional (not needed for Phase 0)

| Tool | When |
|------|------|
| JDK 21 | Full project (freeze spec) |
| Node.js LTS | React/Vite frontend in Phase 1 |
| Docker Desktop | Container deployment in Phase 1 |
| Git | Version control when you choose |

### Install JDK 11/21 (if missing)

- Download [Eclipse Temurin](https://adoptium.net/) or Amazon Corretto JDK 11 or 21
- Install and set `JAVA_HOME` to the JDK folder (not JRE)

### Install Maven (if missing)

- Download from https://maven.apache.org/download.cgi
- Add `bin` folder to PATH

---

## 2. Project location

```
C:\Workspace\SRC\Kishore\TMS\tender-platform\backend\
```

---

## 3. Run the application

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend

# Ensure Java 11+ for Maven (see section 1)
mvn clean spring-boot:run
```

Wait until you see: `Started TenderPlatformApplication`

---

## 4. Open sample screens

| URL | Screen |
|-----|--------|
| http://localhost:8080 | **Login** — click "Continue as Demo User" |
| (after login) | **Dashboard** — KPIs from mock API |
| Sidebar → Tender List | **Tender List** — 3 sample tenders |

### API endpoints (mock data)

| Endpoint | Purpose |
|----------|---------|
| http://localhost:8080/api/health | Health + phase info |
| http://localhost:8080/api/dashboard/summary | Dashboard KPIs |
| http://localhost:8080/api/tenders | Tender list |
| http://localhost:8080/actuator/health | Spring Actuator |

---

## 5. Run tests

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn test
```

---

## 6. Stop the server

Press `Ctrl+C` in the terminal where Spring Boot is running.

---

## 7. What is NOT in Phase 0

- PostgreSQL / Flyway / JPA
- Docker / Nginx compose stack
- Git repository initialization
- Entra ID / real authentication
- Tender create/edit, workflow commands, admin screens
- Multi-tenant database routing (skeleton only in later phases)

---

## 8. Next steps (Phase 1)

1. Install JDK 21 and Node.js LTS
2. Add PostgreSQL + Flyway (Stage 02)
3. React/Vite SPA replacing static pages
4. Docker Compose for full local stack

See `TMS_v9.2.2_Implementation_Plan.md` on Desktop for the full roadmap.
