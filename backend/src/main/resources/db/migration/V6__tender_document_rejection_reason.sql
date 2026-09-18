ALTER TABLE tms.tender_document
    ADD COLUMN rejection_reason TEXT,
    ADD COLUMN reviewed_at TIMESTAMPTZ,
    ADD COLUMN reviewed_by VARCHAR(200);
