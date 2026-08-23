# TMS v9.2.2 — Screen Catalog for Review

**Source:** `Tender_Management_Platform_v9.2.2_REVISED_FINAL_DEVELOPMENT_FREEZE.docx` (Sections 23, 24, 25, 61)  
**Purpose:** Business/stakeholder review of all Phase 1 screens before development  
**Scope:** Phase 0 Foundation + Phase 1 Tender Management only

---

## 1. Authentication

| # | Screen | Primary actor | Key functions |
|---|--------|---------------|---------------|
| 1.1 | **Login** | All users | Company SSO (Entra ID OIDC); redirect to dashboard on success; access denied for inactive/suspended users |

---

## 2. Operational Screens (Tender Team & Stakeholders)

| # | Screen | Primary actor | Key functions |
|---|--------|---------------|---------------|
| 2.1 | **Dashboard** | Stakeholder, Management | Portfolio KPIs (active tenders, closing today/7 days, pending approvals, missing docs, overdue actions, red risks, pipeline value, win rate); deadline widgets; exception highlights; **last 08:00 digest run status**; tenant/data-scope filtered |
| 2.2 | **Customer List** | Tender Creator, Admin | Search; active filter; list customers |
| 2.3 | **Customer Create / Edit** | Tender Creator, Admin | customerCode, name, type, contact; no physical delete if referenced by tenders |
| 2.4 | **Tender List** | Tender team | Search, filter, sort, pagination; create tender; columns: Tender ID, customer, name, owner, stage, priority, closing, health, completion % |
| 2.5 | **Tender Create** | Tender Creator | Master data form (see Section 2.5.1); creates DRAFT tender with generated TND-YYYY-NNNN ID |
| 2.6 | **Tender Edit** | Owner, Coordinator | Edit permitted master fields only; **status/lifecycle fields read-only** |
| 2.7 | **Tender Detail** | Tender team | Summary header + workflow action buttons + tabbed workspace (see Section 3) |

### 2.5.1 Tender Create/Edit — Form sections (frozen)

| Section | Fields |
|---------|--------|
| Tender Details | Name, Customer, Tender Reference, Tender Type, Published Date, Closing Date/Time, Submission Mode, Portal Reference |
| Commercial | Estimated Value, Currency |
| Ownership | Tender Owner, Bid Manager, Technical Owner, Commercial Owner, Priority |
| Status | **Read-only** — changes only via workflow commands |
| Submission | **Read-only** — entered only through Submit action/modal |
| Outcome | **Read-only** — entered only through Award/Lost/Cancel commands |

### 2.7.1 Tender Detail — Workflow action buttons (frozen)

| Button | Visible when | Backend command |
|--------|--------------|-----------------|
| Start Evaluation | DRAFT | `POST /api/tenders/{id}/start-evaluation` |
| Record GO | UNDER_EVALUATION + authorized | `POST /api/tenders/{id}/go` |
| Record NO-GO | UNDER_EVALUATION + authorized | `POST /api/tenders/{id}/no-go` |
| Start Preparation | GO_APPROVED + authorized | `POST /api/tenders/{id}/start-preparation` |
| Request Approval | IN_PREPARATION + authorized + gates pass | `POST /api/tenders/{id}/request-approval` |
| Approve | Pending approval + approver | `POST /api/tenders/{id}/approvals/{id}/approve` |
| Reject | Pending approval + approver | `POST /api/tenders/{id}/approvals/{id}/reject` |
| Submit Tender | APPROVED_FOR_SUBMISSION + authorized | `POST /api/tenders/{id}/submit` |
| Record Award | SUBMITTED or CLARIFICATION + manager | `POST /api/tenders/{id}/outcome/award` |
| Record Lost | SUBMITTED or CLARIFICATION + manager | `POST /api/tenders/{id}/outcome/lost` |
| Cancel | Authorized nonterminal tender | `POST /api/tenders/{id}/outcome/cancel` |
| Close | Terminal outcome + manager | `POST /api/tenders/{id}/close` |

---

## 3. Tender Detail Tabs (frozen — Section 61)

| # | Tab | Key functions |
|---|-----|---------------|
| 3.1 | **Summary** | Lifecycle stage, health, completion %, GO/NO-GO, approval status, next action, latest status comment |
| 3.2 | **Prerequisites** | Checklist item-by-item: mandatory flag, owner, due date, evidence/reference, status (Complete/N/A); readiness blocks approval if incomplete |
| 3.3 | **Eligibility** | Criterion-by-criterion compliance, evidence, owner, status; PARTIALLY_COMPLIANT blocks approval/submit |
| 3.4 | **Bid Security / Fees** | Tender fee, EMD/bid security: amount, mode, reference, validity, payment/return status, alerts |
| 3.5 | **Pre-Bid & Clarifications** | Pre-bid meeting flag, attendance, minutes; queries/responses, owners, due dates, overdue status |
| 3.6 | **Amendments** | Corrigenda history: amendment no, issued date, description, old/new closing dates, acknowledgement |
| 3.7 | **Documents** | Upload, metadata, version/revision, review/approval status; mandatory document gate |
| 3.8 | **Technical** | Technical readiness status, reviewer, reviewed date, remarks |
| 3.9 | **Commercial / BOQ** | Commercial readiness, BOQ/price schedule status, reviewer, document reference |
| 3.10 | **Actions** | Open/overdue/completed tasks: title, assignee, priority, due date, status, escalation |
| 3.11 | **Risks** | Description, probability, impact, rating, owner, mitigation, review date, status |
| 3.12 | **Approvals** | Request approval; step history; approver, decision, comments, timestamps (immutable) |
| 3.13 | **Submission** | Submission mode, timestamp, reference, proof document; read-only except via Submit modal |
| 3.14 | **Outcome** | Award/Lost/Cancelled display; loss reason, outcome date, handover status if Awarded |
| 3.15 | **Audit** | Read-only immutable timeline of all material actions and transitions |
| 3.16 | **Stakeholders** (tender-specific) | User/email, role, receive daily digest flag |

---

## 4. Tenant Administration Screens (Stage 06)

Accessible to **Tenant Admin** and authorized roles only. Platform admin does **not** automatically see tender content.

| # | Screen | Key functions |
|---|--------|---------------|
| 4.1 | **Users** | List/search users; view employee code, email, designation, org unit, manager, status |
| 4.2 | **User Create / Edit** | Link to directory identity; assign designation, org unit, manager; active/employment status |
| 4.3 | **Organization Units** | Hierarchical tree; code, name, unit type, parent, head user |
| 4.4 | **Designations** | Job title reference data |
| 4.5 | **Locations** | Office/site with timezone |
| 4.6 | **Hierarchy** | Reporting manager assignment; cycle detection |
| 4.7 | **Roles & Permissions** | Role catalogue; permission matrix by module |
| 4.8 | **User Role Assignments** | Effective-dated role grants |
| 4.9 | **Data Scopes** | SELF / TEAM / DEPARTMENT / BUSINESS_UNIT / ALL per user/module |
| 4.10 | **Approval Authority** | Level, value/currency limits, effective dates, subject exclusivity |
| 4.11 | **Approval Delegation** | Delegate, source authority, effective window, reason; no chaining |
| 4.12 | **Stakeholder Groups** | Group definition; **ALL_TENDER_STAKEHOLDERS** seed; TO/CC members |
| 4.13 | **IAM Sync Runs** | Directory sync history, issues, manual trigger |
| 4.14 | **Tenant Configuration** | Timezone, reminder thresholds, partially-compliant rule (read-only BLOCK), app_config visibility |
| 4.15 | **Exchange Rates** | Tenant exchange rates for cross-currency approval/reporting |
| 4.16 | **Daily Digest Admin** | Recipient config; schedule; **preview** (no send); run history; **resend** (audited) |

### 4.16.1 Daily Digest Admin — sub-views

| Sub-view | Functions |
|----------|-----------|
| Digest Run History | `GET /api/admin/digest/runs` — status, business date, tender count, recipient count, retry count |
| Run Detail | Errors, provider message ID, correlation ID |
| Preview | `GET /api/admin/digest/preview` — current snapshot without sending |
| Run Now | `POST /api/admin/digest/run-now` — controlled manual run |
| Resend | `POST /api/admin/digest/{businessDate}/resend` — audited resend attempt |

---

## 5. Platform Administration Screens (Stage 06)

Accessible to **Platform Admin / Platform Support** only. Separate from tenant business data.

| # | Screen | Key functions |
|---|--------|---------------|
| 5.1 | **Tenant Registry** | List tenants; status (PROVISIONING/TRIAL/ACTIVE/SUSPENDED/DISABLED/TERMINATED) |
| 5.2 | **Tenant Provision / Edit** | SHARED_DB vs DEDICATED_DB mode; timezone; datasource routing; entitlements |
| 5.3 | **Tenant Entitlements** | DEDICATED_DATABASE, LDAP, API_ACCESS, ADVANCED_APPROVALS feature flags |
| 5.4 | **Platform Monitoring** | Digest fleet status, failed jobs, health alerts |
| 5.5 | **Support Elevation** | Time-bounded, reason/ticket-bound tenant access for support (fully audited) |

---

## 6. Screen Count Summary

| Area | Screens / tabs |
|------|----------------|
| Authentication | 1 |
| Operational (incl. customers, tender list/create/edit/detail) | 7 screens + 16 detail tabs |
| Tenant Administration | 16 |
| Platform Administration | 5 |
| **Total distinct screens/views** | **~29 screens + 16 tender tabs** |

---

## 7. Role → Screen Access Matrix (summary for review)

| Screen area | Typical roles |
|-------------|---------------|
| Dashboard | All authorized users (data-scoped) |
| Tender List / Detail | TENDER_OWNER, TENDER_MANAGER, TENDER_USER, reviewers, MANAGEMENT_VIEWER (read) |
| Workflow commands | Role + permission + data scope + workflow state |
| GO / NO-GO buttons | GO_DECIDE authority |
| Approve / Reject | TENDER_APPROVER + effective authority + SoD |
| Submit | Authorized submitter + readiness gates |
| Audit tab | AUDIT_VIEWER, admins, authorized support |
| Tenant Admin screens | TENDER_ADMIN, SYSTEM_ADMIN (tenant scope) |
| Platform Admin screens | PLATFORM_ADMIN, PLATFORM_SUPPORT |
| Digest Admin | Tenant Admin or approved operational role |

---

## 8. Review Checklist for Stakeholders

Please confirm or comment on:

- [ ] All tender lifecycle stages are represented as tabs on Tender Detail
- [ ] Workflow buttons match expected business process (no direct status editing)
- [ ] Admin screens cover user/hierarchy/RBAC/authority/delegation/stakeholder needs
- [ ] Daily Digest admin (preview, history, resend) is sufficient for operations
- [ ] Platform vs Tenant admin separation is clear
- [ ] Missing screens for your organization (list any additions as Change Requests)

---

## 9. Interactive Wireframes

Open either file in a browser (double-click or drag into Chrome/Edge):

- **`TMS_v9.2.2_Sample_Screens_for_Review.html`** — wireframe gallery (recommended)
- **`TMS_v9.2.2_Sample_Screens_for_Review.md`** — same content (backup if HTML is blocked)

Use the left sidebar to navigate. **Ctrl+P** prints a PDF pack for stakeholders.

---

*Generated from TMS v9.2.2 FINAL DEVELOPMENT FREEZE specification.*
