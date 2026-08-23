# TMS React Frontend

React 18 + TypeScript + Vite UI for Phase 0 sample screens.

## Prerequisites

- **Node.js 20** at `C:\Program Files\nodejs`
- **PATH** must include Node (see below)
- **Backend** running on http://localhost:8080

### Fix PATH (if `node` is not recognized)

Add to system PATH or run in each PowerShell session:

```powershell
$env:PATH = "C:\Program Files\nodejs;" + $env:PATH
node -v   # should show v20.x
```

Or: Settings → System → Environment Variables → Path → add `C:\Program Files\nodejs`.

## Install

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\frontend
npm install
```

## Run (development)

**Terminal 1 — backend:**

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn spring-boot:run
```

**Terminal 2 — frontend:**

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\frontend
npm run dev
```

Open **http://localhost:5173**

| Screen | Route |
|--------|-------|
| Login | `/login` |
| Dashboard | `/dashboard` |
| Tender List | `/tenders` |

Vite proxies `/api` to the Spring Boot backend on port 8080.

## Production build (bundled into backend)

The backend Maven build builds the React app and copies `dist/` into the JAR automatically:

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn clean package -DskipTests
```

Then run only the backend — UI is served at http://localhost:8080:

```powershell
mvn spring-boot:run
```

Skip the frontend step when iterating on backend-only changes:

```powershell
mvn spring-boot:run -DskipFrontend=true
```

Manual frontend build (optional):

```powershell
cd frontend
npm run build
```
