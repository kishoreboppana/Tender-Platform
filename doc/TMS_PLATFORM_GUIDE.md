# TMS Tender Platform — Consolidated Guide

Local **Tender Management Platform (TMS)** v9.2.2 Phase 0 implementation: Spring Boot API + PostgreSQL + React UI.

**Workspace root (this project):**

```
C:\Workspace\SRC\Kishore\TMS\tender-platform\
```

---

## 1. Stack information

### 1.1 Current runtime stack (as built)


| Layer           | Technology      | Version / notes                                         |
| --------------- | --------------- | ------------------------------------------------------- |
| **Backend**     | Java            | 8 (Maven `java.version`); JDK 11+ on PATH recommended   |
| **Backend**     | Spring Boot     | 2.7.18                                                  |
| **Backend**     | Spring Data JPA | Hibernate 5.6                                           |
| **Database**    | PostgreSQL      | 14+ (local); schema `tms`                               |
| **Migrations**  | Flyway          | Via `flyway-core`; scripts in `db/migration/`           |
| **Frontend**    | React           | 18.3                                                    |
| **Frontend**    | TypeScript      | 5.6                                                     |
| **Frontend**    | Vite            | 5.4                                                     |
| **Frontend**    | Node.js         | 20.8.0 (Maven build installs via frontend-maven-plugin) |
| **HTTP client** | Axios           | 1.7                                                     |
| **Routing**     | React Router    | 6.28                                                    |
| **Build**       | Maven           | 3.6+                                                    |
| **OS (dev)**    | Windows         | PowerShell commands below                               |




### 1.2 Target stack (v9.2.2 freeze — future phases)


| Item         | Target                           |
| ------------ | -------------------------------- |
| JDK          | 21                               |
| Spring Boot  | 3.x                              |
| Deployment   | Docker, Nginx                    |
| Auth         | Entra ID / IAM (Stage 03)        |
| Multi-tenant | SHARED_DB / DEDICATED_DB routing |


Phase 0 intentionally uses Java 8 + Boot 2.7 for corporate Maven/JDK compatibility.

### 1.3 Ports and URLs


| Service          | URL                                                                            | Purpose                                  |
| ---------------- | ------------------------------------------------------------------------------ | ---------------------------------------- |
| Backend API      | [http://localhost:8080](http://localhost:8080)                                 | REST API + bundled UI (production-style) |
| React dev server | [http://localhost:5173](http://localhost:5173)                                 | UI development (proxies `/api` → 8080)   |
| PostgreSQL       | localhost:5432                                                                 | Database `tms`                           |
| Actuator         | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Spring health                            |


---



## 2. Code layout and paths



### 2.1 Repository structure

```
tender-platform/
├── doc/                          ← This documentation folder
├── backend/                      ← Spring Boot application (Maven)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/company/tender/
│       │   │   ├── TenderPlatformApplication.java    # Main entry
│       │   │   ├── api/                              # REST controllers
│       │   │   │   ├── HealthController.java
│       │   │   │   ├── DashboardController.java
│       │   │   │   ├── TenderController.java         # GET/POST tenders
│       │   │   │   ├── CustomerController.java
│       │   │   │   ├── dto/CreateTenderRequest.java
│       │   │   │   └── ApiExceptionHandler.java
│       │   │   ├── config/
│       │   │   │   ├── SpaForwardConfig.java         # SPA routes on 8080
│       │   │   │   ├── SeedConfig.java
│       │   │   │   └── SeedProperties.java
│       │   │   ├── domain/                           # JPA entities + repos
│       │   │   │   ├── Tenant.java, Customer.java, Tender.java
│       │   │   │   ├── TenantRepository.java
│       │   │   │   ├── CustomerRepository.java
│       │   │   │   └── TenderRepository.java
│       │   │   ├── service/
│       │   │   │   ├── TenderQueryService.java
│       │   │   │   ├── TenderCommandService.java     # Create tender
│       │   │   │   └── CustomerQueryService.java
│       │   │   └── seed/                              # JSON seed loader
│       │   │       ├── demo-data.json (in resources)
│       │   │       ├── DatabaseSeedLoader.java
│       │   │       └── SeedDashboardBootstrap.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       ├── db/migration/                     # Flyway V1–V4
│       │       ├── db/seed/demo-data.json            # Static demo data
│       │       └── static/                           # UI bundle (build output)
│       └── test/
│           ├── java/.../ApiSmokeTest.java
│           └── resources/application-test.yml
├── frontend/                     ← React + Vite UI
│   ├── package.json
│   ├── vite.config.ts            # Dev proxy to :8080
│   └── src/
│       ├── main.tsx
│       ├── App.tsx               # Routes
│       ├── api/client.ts         # API client (axios)
│       ├── components/AppLayout.tsx
│       └── pages/
│           ├── LoginPage.tsx
│           ├── DashboardPage.tsx
│           ├── TenderListPage.tsx
│           └── CreateTenderPage.tsx
├── scripts/postgres/             # Manual SQL (optional)
│   ├── 01_create_tables.sql
│   └── 02_demo_seed_data.sql
└── POSTGRES_SETUP.md             # Database setup (also summarized below)
```



### 2.2 UI routes (React)


| Route          | Screen                     |
| -------------- | -------------------------- |
| `/login`       | Demo login                 |
| `/dashboard`   | Portfolio dashboard (KPIs) |
| `/tenders`     | Tender register list       |
| `/tenders/new` | Create tender form         |




### 2.3 REST API


| Method | Path                     | Description                         |
| ------ | ------------------------ | ----------------------------------- |
| GET    | `/api/health`            | App + DB + seed integration status  |
| GET    | `/api/dashboard/summary` | Dashboard KPIs                      |
| GET    | `/api/tenders`           | List tenders                        |
| POST   | `/api/tenders`           | Create tender (saves to PostgreSQL) |
| GET    | `/api/customers`         | Customer dropdown for create form   |
| GET    | `/actuator/health`       | Spring Actuator                     |




### 2.4 Database

- **Database:** `tms`
- **Schema:** `tms`
- **User / password:** `tms` / `tms`
- **JDBC:** `jdbc:postgresql://localhost:5432/tms`

**Tables:** `tenant`, `customer`, `business_sequence`, `tender`, `audit_event`, `app_user`

**Flyway migrations:** `backend/src/main/resources/db/migration/`


| Version | File                                 | Purpose                    |
| ------- | ------------------------------------ | -------------------------- |
| V1      | `V1__initial_tms_schema.sql`         | Create tables              |
| V2      | `V2__seed_demo_data.sql`             | Legacy inline seed         |
| V3      | `V3__static_seed_reference.sql`      | Marker for file-based seed |
| V4      | `V4__align_demo_approval_status.sql` | Align approval statuses    |


**Static seed file (edit demo data):**

`backend/src/main/resources/db/seed/demo-data.json`

---



## 3. Git — copy and version control

Phase 0 was started without a mandatory Git remote. Use one of the following when you want version control.

### 3.1 Copy project folder (no Git)

Copy the entire folder to backup or another machine:

```
C:\Workspace\SRC\Kishore\TMS\tender-platform\
```

**Do not copy** (regenerate on target machine):

- `frontend/node_modules/`
- `frontend/node/` (Node installed by Maven plugin)
- `backend/target/`



### 3.2 Initialize Git locally

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform
git init
git add backend/src frontend/src frontend/package.json frontend/vite.config.ts frontend/tsconfig*.json scripts doc
git add backend/pom.xml POSTGRES_SETUP.md
git commit -m "TMS Phase 0: Spring Boot + React + PostgreSQL"
```

Suggested `.gitignore` entries:

```
backend/target/
frontend/node_modules/
frontend/node/
frontend/dist/
*.log
```



### 3.3 Remote repository

**GitHub:** https://github.com/kishoreboppana/Tender-Platform.git

```powershell
git clone https://github.com/kishoreboppana/Tender-Platform.git
cd Tender-Platform
```

Then install dependencies (see Build section).


---



## 4. Prerequisites


| Tool       | Verify          | Notes                                                                    |
| ---------- | --------------- | ------------------------------------------------------------------------ |
| JDK        | `java -version` | 8+ for build; 11 common on PATH                                          |
| Maven      | `mvn -version`  | 3.6+                                                                     |
| PostgreSQL | Service running | DB `tms`, user `tms`                                                     |
| Node.js 20 | `node -v`       | Optional for manual frontend build; path often `C:\Program Files\nodejs` |


**Node PATH (if** `node` **not recognized):**

```powershell
$env:PATH = "C:\Program Files\nodejs;" + $env:PATH
```

Or add `C:\Program Files\nodejs` to System Environment Variables → Path.

**Maven corporate mirror:** `pom.xml` includes Maven Central fallback if JFrog returns 403.

---



## 5. Configuration



### 5.1 Backend — `application.yml`


| Setting                         | Default                            | Description                              |
| ------------------------------- | ---------------------------------- | ---------------------------------------- |
| `server.port`                   | `8080`                             | HTTP port                                |
| `spring.profiles.active`        | `dev`                              | Active profile                           |
| `spring.jpa.hibernate.ddl-auto` | `validate`                         | No auto DDL; Flyway owns schema          |
| `tender.seed.enabled`           | `false`                            | Seed loader off unless profile overrides |
| `tender.seed.data-location`     | `classpath:db/seed/demo-data.json` | Seed JSON path                           |




### 5.2 Dev profile — `application-dev.yml`


| Setting                             | Value                                  |
| ----------------------------------- | -------------------------------------- |
| `spring.datasource.url`             | `jdbc:postgresql://localhost:5432/tms` |
| `spring.datasource.username`        | `tms`                                  |
| `spring.datasource.password`        | `tms`                                  |
| `spring.flyway.schemas`             | `tms`                                  |
| `spring.flyway.baseline-on-migrate` | `true`                                 |
| `tender.seed.enabled`               | `true`                                 |




### 5.3 Test profile — `application-test.yml`

- Same PostgreSQL connection as dev
- `spring.flyway.enabled: false` (tests assume DB already migrated)
- `tender.seed.enabled` not set (loader disabled)



### 5.4 Frontend — `vite.config.ts`


| Setting  | Value                                            |
| -------- | ------------------------------------------------ |
| Dev port | `5173`                                           |
| Proxy    | `/api` and `/actuator` → `http://localhost:8080` |




### 5.5 Maven — `backend/pom.xml` properties


| Property       | Default       | Purpose                                  |
| -------------- | ------------- | ---------------------------------------- |
| `skipFrontend` | `false`       | Set `true` to skip UI build during Maven |
| `node.version` | `v20.8.0`     | Node for frontend-maven-plugin           |
| `frontend.dir` | `../frontend` | React project path                       |


**Examples:**

```powershell
mvn spring-boot:run -DskipFrontend=true    # Backend only, faster iteration
mvn clean package -DskipTests               # Full UI + JAR
mvn clean package -DskipTests -DskipFrontend=true
```



### 5.6 What to change per environment


| Environment         | Files to update                                                                                    |
| ------------------- | -------------------------------------------------------------------------------------------------- |
| Local dev           | `application-dev.yml` (DB URL, credentials)                                                        |
| New machine         | PostgreSQL create DB/user; see `POSTGRES_SETUP.md`                                                 |
| Demo data           | `db/seed/demo-data.json`                                                                           |
| API base URL (prod) | Frontend built with `/api` relative paths; serve UI and API same origin OR configure reverse proxy |
| Port conflict       | `server.port` in `application.yml`; `vite.config.ts` `server.port`                                 |


---



## 6. Build



### 6.1 Database (first time)

1. Start PostgreSQL.
2. Create database and user (see `POSTGRES_SETUP.md` or `scripts/postgres/`).
3. Start backend once — Flyway creates schema and seed.



### 6.2 Backend only

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn clean compile -DskipFrontend=true
mvn test -DskipFrontend=true
```



### 6.3 Frontend only

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\frontend
npm install
npm run build
```

Output: `frontend/dist/` (`index.html` + `assets/`).

### 6.4 Full stack (UI embedded in backend JAR)

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn clean package -DskipTests
```

This runs:

1. `npm install` + `npm run build` in `frontend/` (unless `-DskipFrontend=true`)
2. Copies `frontend/dist/` → `target/classes/static/`
3. Produces `target/tender-platform-0.1.0-SNAPSHOT.jar`



### 6.5 Manual UI deploy to running backend (dev shortcut)

After `npm run build` in `frontend/`:

```powershell
Copy-Item -Path dist\* -Destination ..\backend\target\classes\static\ -Recurse -Force
```

Restart backend. Use **Ctrl+F5** in browser on [http://localhost:8080](http://localhost:8080).

---



## 7. Run (development)



### 7.1 Two terminals (recommended for UI work)

**Terminal 1 — Backend:**

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn spring-boot:run -DskipFrontend=true
```

Wait for: `Started TenderPlatformApplication`

**Terminal 2 — Frontend:**

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\frontend
$env:PATH = "C:\Program Files\nodejs;" + $env:PATH
npm run dev
```

Open: **[http://localhost:5173](http://localhost:5173)** → Login → Dashboard / Tender List / Create Tender.

### 7.2 Single server (bundled UI)

After `mvn clean package -DskipTests`:

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn spring-boot:run -DskipFrontend=true
```

Open: **[http://localhost:8080](http://localhost:8080)** (hard refresh if UI looks stale).

### 7.3 Run JAR

```powershell
java -jar target\tender-platform-0.1.0-SNAPSHOT.jar
```

Requires PostgreSQL reachable with configured credentials.

---



## 8. Deploy



### 8.1 Local / POC (current)


| Mode       | UI    | API   | Notes           |
| ---------- | ----- | ----- | --------------- |
| Dev split  | :5173 | :8080 | Vite proxy      |
| Single JVM | :8080 | :8080 | UI in `static/` |


No Docker or Nginx in Phase 0.

### 8.2 Deployment artifact

- **JAR:** `backend/target/tender-platform-0.1.0-SNAPSHOT.jar`
- Contains: Spring Boot app + Flyway migrations + bundled React `static/` (if built with frontend)



### 8.3 Production-oriented steps (outline)

1. Set production profile YAML (DB URL, secrets — not committed).
2. `mvn clean package -DskipTests` on build agent.
3. Deploy JAR to app server or container (future: Docker image).
4. Run PostgreSQL with `tms` schema; Flyway applies on startup.
5. Put Nginx in front for TLS and static caching (future phase).



### 8.4 Environment variables (optional override)

Spring Boot accepts env vars, e.g.:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/tms
SPRING_DATASOURCE_USERNAME=tms
SPRING_DATASOURCE_PASSWORD=<secret>
SERVER_PORT=8080
```

---



## 9. Seed data and create tender



### 9.1 Loading demo data

- **Automatic (dev):** `tender.seed.enabled=true` in `application-dev.yml`
- **Source file:** `backend/src/main/resources/db/seed/demo-data.json`
- **Manual SQL:** `scripts/postgres/02_demo_seed_data.sql`

Edit JSON → restart backend. New rows use `ON CONFLICT DO NOTHING`.

### 9.2 Create tender (UI)

1. Login → **Create Tender** (sidebar) or `/tenders/new`
2. Fill form → **Save tender**
3. Data stored in `tms.tender`; business ID auto-generated (`TND-YYYY-NNNN`)

---



## 10. Troubleshooting


| Issue                                | Fix                                                                       |
| ------------------------------------ | ------------------------------------------------------------------------- |
| UI shows old text / no Create Tender | Rebuild frontend; copy to `target/classes/static/`; or use :5173; Ctrl+F5 |
| `node` not found                     | Add Node to PATH (see §4)                                                 |
| Flyway checksum / schema errors      | See `POSTGRES_SETUP.md` reset schema steps                                |
| Maven 403 on dependencies            | Maven Central repos in `pom.xml`; retry build                             |
| API errors on :5173                  | Ensure backend running on :8080                                           |
| Tests fail                           | PostgreSQL up; demo data present; `mvn test -DskipFrontend=true`          |
| Port 8080 in use                     | Stop old Java process or change `server.port`                             |


**Check health:**

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Expected: `status: UP`, `seedIntegrated: true`, `tendersLoaded` ≥ 3.

---



## 11. Related documents


| Document              | Path                                             |
| --------------------- | ------------------------------------------------ |
| PostgreSQL setup      | `../POSTGRES_SETUP.md`                           |
| Frontend readme       | `../frontend/README.md`                          |
| Phase 0 notes         | `../PHASE0_LOCAL_SETUP.md` (if present)          |
| Business requirements | Desktop `TMS_v9.2.2_*.docx` (authoritative spec) |


---



## 12. Version and change log (summary)


| Date    | Change                                             |
| ------- | -------------------------------------------------- |
| Phase 0 | Spring Boot API, PostgreSQL, Flyway, React screens |
|         | Login, Dashboard, Tender List                      |
|         | Static seed from `demo-data.json`                  |
|         | Maven bundles React into JAR                       |
|         | Create Tender screen + `POST /api/tenders`         |


**Artifact version:** `0.1.0-SNAPSHOT` (`backend/pom.xml`)

---

*Document generated for local TMS tender-platform workspace. Update this file when stack, ports, or deployment process changes.*