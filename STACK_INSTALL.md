# Stack install checklist (Windows)

Use this checklist before running Phase 0.

## JDK

```powershell
java -version
```

Expected: version **11** or higher.

If wrong Java version for Maven:

```powershell
# Example — set your actual JDK 11 path
$env:JAVA_HOME = "C:\Program Files\Amazon Corretto\jdk11.0.14.10.1-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

## Maven

```powershell
mvn -version
```

Expected: Maven 3.6+ and **Java version: 11** (or higher) in the output.

## Run Phase 0

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn clean spring-boot:run
```

Browser: http://localhost:8080

## Node.js (skip for Phase 0)

Not required — UI is static HTML served by Spring Boot.

Install Node.js LTS from https://nodejs.org when starting React frontend in Phase 1.

## Docker (skip for Phase 0)

Not required for this setup.

Install Docker Desktop when you need containerized PostgreSQL + Nginx in Phase 1.
