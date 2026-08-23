# PostgreSQL setup for TMS

Local database configuration used by the Spring Boot backend.

## Connection details

| Setting | Value |
|---------|--------|
| Host | `localhost` |
| Port | `5432` |
| Database | `tms` |
| Schema | `tms` |
| User | `tms` |
| Password | `tms` |

JDBC URL: `jdbc:postgresql://localhost:5432/tms`

Configured in [`backend/src/main/resources/application-dev.yml`](backend/src/main/resources/application-dev.yml).

## If you created only a schema (not database)

If your database is `postgres` and you created schema `tms`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
```

Flyway still creates/uses schema `tms`.

## Manual verification (psql)

```powershell
psql -h localhost -U tms -d tms
```

```sql
\dn
\dt tms.*
SELECT tender_id, name, status FROM tms.tender;
```

## Initial tables (Flyway)

| Migration | Purpose |
|-----------|---------|
| `V1__initial_tms_schema.sql` | Schema `tms`, core tables |
| `V2__seed_demo_data.sql` | Legacy inline seed (older databases) |
| `V3__static_seed_reference.sql` | Marker — seed source is `db/seed/demo-data.json` |

Manual SQL scripts (without the app):

| Script | Purpose |
|--------|---------|
| `scripts/postgres/01_create_tables.sql` | Create all `tms.*` tables |
| `scripts/postgres/02_demo_seed_data.sql` | Load demo rows (mirror of JSON seed) |

### Static seed data (edit and reload)

Demo data is defined in:

`backend/src/main/resources/db/seed/demo-data.json`

On startup (dev profile), `DatabaseSeedLoader` reads this file and inserts rows idempotently.

| Setting | File | Default |
|---------|------|---------|
| `tender.seed.enabled` | `application-dev.yml` | `true` |
| `tender.seed.data-location` | `application-dev.yml` | `classpath:db/seed/demo-data.json` |
| `tender.seed.force` | `application-dev.yml` | `false` |

To add tenders or customers: edit `demo-data.json`, then restart the backend. New rows use `ON CONFLICT DO NOTHING` — set `tender.seed.force=true` only when seeding into an empty tenant.

Dashboard KPI defaults (`winRatePercent`, `digestLastRun`, etc.) are also loaded from the `dashboardDefaults` section of the same JSON file.

### Tables created

| Table | Purpose |
|-------|---------|
| `tms.tenant` | Tenant registry (multi-tenant foundation) |
| `tms.customer` | Customer master |
| `tms.business_sequence` | Tender ID sequence per tenant/year |
| `tms.tender` | Tender register |
| `tms.audit_event` | Immutable audit trail |
| `tms.app_user` | Minimal user records (IAM expanded later) |

## First run

1. Ensure PostgreSQL service is running.
2. Ensure database `tms` exists and user `tms` has access:

```sql
CREATE DATABASE tms;
CREATE USER tms WITH PASSWORD 'tms';
GRANT ALL PRIVILEGES ON DATABASE tms TO tms;
```

On PostgreSQL 15+, also grant schema privileges after first Flyway run:

```sql
GRANT ALL ON SCHEMA tms TO tms;
GRANT ALL ON ALL TABLES IN SCHEMA tms TO tms;
```

3. Start the backend — Flyway applies migrations automatically:

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn spring-boot:run
```

4. Check health: http://localhost:8080/api/health  
   Expect: `database: connected`, `schema: tms`

5. Check data: http://localhost:8080/api/tenders  
   Expect: 3 demo tenders

## React UI

Start backend first, then:

```powershell
cd frontend
npm run dev
```

Open http://localhost:5173 — Dashboard and Tender List read from PostgreSQL.

## Automated tests

`mvn test` uses the **PostgreSQL** `tms` database (profile `test` in `application-test.yml`). There is no in-memory database.

Requirements before running tests:

1. PostgreSQL is running.
2. Start the backend once so Flyway applies migrations and seed data (`mvn spring-boot:run`).
3. Demo seed data is present (3 tenders).

Tests do not run Flyway; they connect to the same `tms` database as dev.

## Reset schema (if Flyway or tables are out of sync)

If migrations fail or tables are missing (for example after a partial manual setup):

```sql
DROP SCHEMA IF EXISTS tms CASCADE;
CREATE SCHEMA tms;
GRANT ALL ON SCHEMA tms TO tms;
```

Then start the backend again — Flyway reapplies `V1` and `V2`.

```powershell
cd C:\Workspace\SRC\Kishore\TMS\tender-platform\backend
mvn test
```
