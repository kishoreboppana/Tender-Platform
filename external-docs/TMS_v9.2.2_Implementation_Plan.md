# TMS v9.2.2 — Implementation Plan (From Scratch)

**Version:** 9.2.2  
**Status:** Implementation plan aligned to FINAL DEVELOPMENT FREEZE  
**Scope:** Phase 0 Foundation + Phase 1 Tender Management  
**Target workspace:** `C:\Workspace\SRC\Kishore\TMS`

---

## Current State

- Workspace `C:\Workspace\SRC\Kishore\TMS` is **empty** — no code, git repo, or README.
- Authoritative requirements on Desktop:
  - `TMS_v9.2.2_Business_Use_Case_Catalogue.docx` — 30 business use cases (UC-01 to UC-30)
  - `Tender_Management_Platform_v9.2.2_REVISED_FINAL_DEVELOPMENT_FREEZE.docx` — single source of truth

**First setup action:** copy both `.docx` files into `docs/requirements/` in the repo.

---

## What We Are Building

Enterprise **Tender Management System** for a System Integrator — single system of record for the tender lifecycle. **Phase 2 Procurement/Delivery is out of scope** except for one controlled **Award handover** action when a tender is AWARDED.

### Business lifecycle (command-driven)

```
DRAFT → UNDER_EVALUATION → GO_APPROVED → IN_PREPARATION → PENDING_APPROVAL
  → APPROVED_FOR_SUBMISSION → SUBMITTED ↔ CLARIFICATION → AWARDED/LOST/CANCELLED → CLOSED

UNDER_EVALUATION → NO-GO → BID_DECLINED → CLOSED
```

Key rules:
- Lifecycle transitions only via explicit backend commands
- Readiness gates block approval/submission
- `PARTIALLY_COMPLIANT` eligibility **blocks** approval and submission (frozen)
- Segregation of duties: Owner/Creator/Requester cannot approve same tender
- Immutable `audit_event` for all material actions

---

## Frozen Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 3.x, Spring Security, OAuth2 Resource Server |
| Persistence | PostgreSQL 16+, Spring Data JPA, Flyway |
| Frontend | React, TypeScript, Vite |
| Edge | Nginx |
| Identity | Microsoft Entra ID OIDC; LDAP/LDAPS adapter; LOCAL_DEV (dev only) |
| Notifications | Microsoft Graph; dev console adapter |
| Documents | `DocumentStorageService` — local/S3-compatible; **no SharePoint** |
| Scheduling | Spring `@Scheduled` digest orchestrator |
| Deployment | Docker Compose |

**Prohibited in Phase 1:** Kafka, Kubernetes, Redis, Camunda, Elasticsearch, microservices, SharePoint, MongoDB as primary DB.

---

## Multi-Tenant Rules (from Stage 01)

- `TenantContext` is the first authorization boundary
- Every tenant-owned table: `tenant_id UUID NOT NULL`
- `SHARED_DB` and `DEDICATED_DB` from same codebase
- Fail-closed: dedicated tenant never falls back to shared DB
- Digest idempotency: `tenant_id + local_business_date + digest_type`

---

## Repository Structure

```
tender-platform/
  backend/
  frontend/
  nginx/
  docker/
  scripts/backup/ restore/ smoke/
  docs/requirements/ api/ runbooks/ uat/
  docker-compose.yml
  README.md
```

---

## 12-Stage Gated Implementation Roadmap

| Stage | Name | Core deliverables |
|-------|------|-------------------|
| **01** | Repository & Local Platform Bootstrap | Git, Docker Compose, Spring Boot, React/Vite, Nginx, TenantContext, 2 demo tenants |
| **02** | PostgreSQL + Flyway Foundation | V000 platform DDL, tenant-aware schema, Testcontainers |
| **03** | IAM + User Master | Entra OIDC, LOCAL_DEV, app_user, /api/me |
| **04** | Hierarchy + RBAC + Data Scope | Org units, roles, SELF/TEAM/DEPARTMENT/ALL scopes |
| **05** | Approval Authority + Delegation + Stakeholders | Authority, SoD, ALL_TENDER_STAKEHOLDERS |
| **06** | Administration UI & APIs | Platform vs tenant admin separation |
| **07** | Tender Core + Prerequisites + GO/NO-GO + Eligibility | Customer, tender CRUD, TND-YYYY-NNNN ID |
| **08** | Compliance Operations | EMD, clarifications, amendments, documents, actions, risks |
| **09** | Technical + Commercial + Approval + Submission + Outcome | Full lifecycle + award handover |
| **10** | Dashboard + 08:00 Digest + Notifications | KPIs, digest orchestrator, preview/resend |
| **11** | Security, Performance, Backup, Regression | IDOR tests, fleet backup/restore |
| **12** | UAT, Release Packaging & Handover | Runbooks, smoke tests, sign-off |

Each stage ends with tests, completion checklist, and **STOP** until `PROCEED STAGE NN`.

---

## Core APIs (frozen workflow commands)

- `POST /api/tenders` — create (DRAFT)
- `POST /api/tenders/{id}/start-evaluation`
- `POST /api/tenders/{id}/go` / `/no-go`
- `POST /api/tenders/{id}/start-preparation`
- `POST /api/tenders/{id}/request-approval`
- `POST /api/tenders/{id}/approvals/{approvalId}/approve|reject`
- `POST /api/tenders/{id}/submit`
- `POST /api/tenders/{id}/outcome/award|lost|cancel`
- `POST /api/tenders/{id}/close`

---

## Open Decisions (DEV placeholders until UAT)

- GO/NO-GO approver matrix and bid approval limits
- Production digest TO/CC recipients
- Entra/LDAP/Graph production configuration
- Document storage target (S3-compatible vs NAS)
- Backup RPO/RTO, file limits, retention

---

## Definition of Done (Phase 1)

- Flyway migrations reproducible from empty DB (platform + SHARED_DB + DEDICATED_DB)
- All lifecycle transitions via explicit commands
- Role matrix enforced in backend + tests
- Per-tenant 08:00 digest with idempotency
- SHARED_DB and DEDICATED_DB pass identical E2E scenarios
- No SharePoint dependency

---

## First Action

**Execute Stage 01:** scaffold repo, Docker Compose, TenantContext, 2-tenant demo, README — then stop for `PROCEED STAGE 02`.

See `TMS_v9.2.2_Screen_Catalog_for_Review.md` for the full UI screen list.
