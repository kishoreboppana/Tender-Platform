-- TMS initial schema (PostgreSQL)
-- Schema namespace: tms
-- Database: tms (user tms)

CREATE SCHEMA IF NOT EXISTS tms;

-- ---------------------------------------------------------------------------
-- Tenant (multi-tenant foundation — single demo tenant seeded in V2)
-- ---------------------------------------------------------------------------
CREATE TABLE tms.tenant (
    id              UUID PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    timezone        VARCHAR(64)  NOT NULL DEFAULT 'Asia/Kolkata',
    database_mode   VARCHAR(20)  NOT NULL DEFAULT 'SHARED_DB',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT tenant_code_unique UNIQUE (code)
);

-- ---------------------------------------------------------------------------
-- Customer master
-- ---------------------------------------------------------------------------
CREATE TABLE tms.customer (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       UUID         NOT NULL REFERENCES tms.tenant(id),
    customer_code   VARCHAR(30)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    customer_type   VARCHAR(30)  NOT NULL DEFAULT 'OTHER',
    primary_contact VARCHAR(200),
    contact_email   VARCHAR(320),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    version         BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT customer_tenant_code_unique UNIQUE (tenant_id, customer_code)
);

CREATE INDEX idx_customer_tenant ON tms.customer(tenant_id);

-- ---------------------------------------------------------------------------
-- Tender ID sequence (per tenant, per year)
-- ---------------------------------------------------------------------------
CREATE TABLE tms.business_sequence (
    tenant_id       UUID         NOT NULL REFERENCES tms.tenant(id),
    sequence_name   VARCHAR(50)  NOT NULL,
    sequence_year   INT          NOT NULL,
    current_value   BIGINT       NOT NULL DEFAULT 0 CHECK (current_value >= 0),
    PRIMARY KEY (tenant_id, sequence_name, sequence_year)
);

-- ---------------------------------------------------------------------------
-- Tender register (core entity)
-- ---------------------------------------------------------------------------
CREATE TABLE tms.tender (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           UUID         NOT NULL REFERENCES tms.tenant(id),
    public_id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    tender_id           VARCHAR(30)  NOT NULL,
    name                VARCHAR(300) NOT NULL,
    customer_id         BIGINT       NOT NULL REFERENCES tms.customer(id),
    tender_reference    VARCHAR(150) NOT NULL,
    tender_type         VARCHAR(30)  NOT NULL DEFAULT 'OPEN',
    estimated_value     NUMERIC(19, 4),
    currency            VARCHAR(3)   NOT NULL DEFAULT 'INR',
    closing_at          TIMESTAMPTZ  NOT NULL,
    submission_mode     VARCHAR(30)  NOT NULL DEFAULT 'PORTAL',
    portal_reference    TEXT,
    tender_owner_name   VARCHAR(200) NOT NULL,
    status              VARCHAR(40)  NOT NULL DEFAULT 'DRAFT',
    priority            VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    go_no_go            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    approval_status     VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED',
    outcome             VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    health              VARCHAR(10)  NOT NULL DEFAULT 'GREEN',
    completion_percent  SMALLINT     NOT NULL DEFAULT 0 CHECK (completion_percent >= 0 AND completion_percent <= 100),
    latest_status_comment TEXT,
    next_action         TEXT,
    next_action_due     TIMESTAMPTZ,
    version             BIGINT       NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT tender_public_id_unique UNIQUE (public_id),
    CONSTRAINT tender_tenant_business_id_unique UNIQUE (tenant_id, tender_id)
);

CREATE INDEX idx_tender_tenant ON tms.tender(tenant_id);
CREATE INDEX idx_tender_status ON tms.tender(status);
CREATE INDEX idx_tender_closing ON tms.tender(closing_at);

-- ---------------------------------------------------------------------------
-- Immutable audit trail
-- ---------------------------------------------------------------------------
CREATE TABLE tms.audit_event (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       UUID         NOT NULL REFERENCES tms.tenant(id),
    event_type      VARCHAR(80)  NOT NULL,
    entity_type     VARCHAR(80),
    entity_id       VARCHAR(80),
    actor           VARCHAR(200),
    detail          TEXT,
    correlation_id  VARCHAR(64),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_tenant ON tms.audit_event(tenant_id);
CREATE INDEX idx_audit_created ON tms.audit_event(created_at);

-- ---------------------------------------------------------------------------
-- Application user (minimal — IAM expanded in Stage 03)
-- ---------------------------------------------------------------------------
CREATE TABLE tms.app_user (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       UUID         NOT NULL REFERENCES tms.tenant(id),
    email           VARCHAR(320) NOT NULL,
    display_name    VARCHAR(200) NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT app_user_tenant_email_unique UNIQUE (tenant_id, email)
);

CREATE INDEX idx_app_user_tenant ON tms.app_user(tenant_id);
