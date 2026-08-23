-- Demo seed data (mirror of backend/src/main/resources/db/seed/demo-data.json)
-- Run after Flyway V1 schema migration:
--   psql -h localhost -U tms -d tms -f 02_demo_seed_data.sql

INSERT INTO tms.tenant (id, code, name, status, timezone, database_mode)
VALUES (
    'a0000000-0000-4000-8000-000000000001',
    'demo',
    'Acme System Integrators (demo)',
    'ACTIVE',
    'Asia/Kolkata',
    'SHARED_DB'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tms.app_user (tenant_id, email, display_name, active)
VALUES (
    'a0000000-0000-4000-8000-000000000001',
    'demo@acme.com',
    'Demo User',
    TRUE
)
ON CONFLICT (tenant_id, email) DO NOTHING;

INSERT INTO tms.customer (tenant_id, customer_code, name, customer_type, contact_email, active)
VALUES
    ('a0000000-0000-4000-8000-000000000001', 'CUS-0001', 'National Highways Authority', 'GOVERNMENT', 'procurement@nhai.gov', TRUE),
    ('a0000000-0000-4000-8000-000000000001', 'CUS-0002', 'NTPC Limited', 'PSU', 'tenders@ntpc.co.in', TRUE),
    ('a0000000-0000-4000-8000-000000000001', 'CUS-0003', 'State Electricity Board', 'GOVERNMENT', 'tenders@seb.gov.in', TRUE)
ON CONFLICT (tenant_id, customer_code) DO NOTHING;

INSERT INTO tms.business_sequence (tenant_id, sequence_name, sequence_year, current_value)
VALUES ('a0000000-0000-4000-8000-000000000001', 'TENDER_ID', 2026, 12)
ON CONFLICT (tenant_id, sequence_name, sequence_year) DO NOTHING;

INSERT INTO tms.tender (
    tenant_id, tender_id, name, customer_id, tender_reference, tender_type,
    estimated_value, currency, closing_at, submission_mode, tender_owner_name,
    status, priority, approval_status, health, completion_percent, latest_status_comment
)
VALUES
    (
        'a0000000-0000-4000-8000-000000000001',
        'TND-2026-0012',
        'Highway Package XII',
        (SELECT id FROM tms.customer WHERE customer_code = 'CUS-0001' AND tenant_id = 'a0000000-0000-4000-8000-000000000001'),
        'NHAI/EPC/2026/4412',
        'OPEN',
        45000000.0000,
        'INR',
        TIMESTAMPTZ '2026-08-05 17:00:00+05:30',
        'PORTAL',
        'A. Sharma',
        'IN_PREPARATION',
        'CRITICAL',
        'NOT_STARTED',
        'RED',
        60,
        'Awaiting OEM authorization document'
    ),
    (
        'a0000000-0000-4000-8000-000000000001',
        'TND-2026-0011',
        'Solar EPC Lot 3',
        (SELECT id FROM tms.customer WHERE customer_code = 'CUS-0002' AND tenant_id = 'a0000000-0000-4000-8000-000000000001'),
        'NTPC/SOLAR/2026/089',
        'OPEN',
        32000000.0000,
        'INR',
        TIMESTAMPTZ '2026-08-12 15:00:00+05:30',
        'PORTAL',
        'P. Mehta',
        'PENDING_APPROVAL',
        'HIGH',
        'PENDING',
        'AMBER',
        80,
        'Pending final approval'
    ),
    (
        'a0000000-0000-4000-8000-000000000001',
        'TND-2026-0010',
        'Substation Upgrade',
        (SELECT id FROM tms.customer WHERE customer_code = 'CUS-0003' AND tenant_id = 'a0000000-0000-4000-8000-000000000001'),
        'SEB/SUB/2026/012',
        'LIMITED',
        18000000.0000,
        'INR',
        TIMESTAMPTZ '2026-08-01 17:00:00+05:30',
        'PORTAL',
        'R. Kumar',
        'SUBMITTED',
        'MEDIUM',
        'APPROVED',
        'GREEN',
        100,
        'Submitted successfully'
    )
ON CONFLICT (tenant_id, tender_id) DO NOTHING;

INSERT INTO tms.audit_event (tenant_id, event_type, entity_type, entity_id, actor, detail)
VALUES
    ('a0000000-0000-4000-8000-000000000001', 'SCHEMA_INITIALIZED', 'DATABASE', 'tms', 'seed-loader', 'Demo data loaded from scripts/postgres/02_demo_seed_data.sql'),
    ('a0000000-0000-4000-8000-000000000001', 'TENDER_CREATED', 'TENDER', 'TND-2026-0012', 'A. Sharma', 'Demo tender registered');
