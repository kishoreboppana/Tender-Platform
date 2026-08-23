-- Align demo tender approval_status with db/seed/demo-data.json

UPDATE tms.tender
SET approval_status = 'PENDING'
WHERE tenant_id = 'a0000000-0000-4000-8000-000000000001'
  AND tender_id = 'TND-2026-0011';

UPDATE tms.tender
SET approval_status = 'APPROVED'
WHERE tenant_id = 'a0000000-0000-4000-8000-000000000001'
  AND tender_id = 'TND-2026-0010';
