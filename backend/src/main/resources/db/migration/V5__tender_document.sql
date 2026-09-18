CREATE TABLE tms.tender_document (
    id                BIGSERIAL PRIMARY KEY,
    tenant_id         UUID         NOT NULL REFERENCES tms.tenant(id),
    tender_id         BIGINT       NOT NULL REFERENCES tms.tender(id) ON DELETE CASCADE,
    doc_name          VARCHAR(200) NOT NULL,
    file_name         VARCHAR(255) NOT NULL,
    stored_path       VARCHAR(500) NOT NULL,
    file_size_bytes   BIGINT       NOT NULL CHECK (file_size_bytes > 0),
    content_type      VARCHAR(100) NOT NULL,
    approval_status   VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    uploaded_by       VARCHAR(200) NOT NULL DEFAULT 'Demo User',
    uploaded_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    submitted_at      TIMESTAMPTZ,
    version           BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT tender_document_approval_status_chk
        CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_tender_document_tender ON tms.tender_document(tender_id);
CREATE INDEX idx_tender_document_tenant ON tms.tender_document(tenant_id);
