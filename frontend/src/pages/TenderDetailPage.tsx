import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  approveTenderDocument,
  deleteTenderDocument,
  fetchTenderById,
  fetchTenderDocuments,
  getTenderDocumentDownloadUrl,
  rejectTenderDocument,
  uploadTenderDocument,
  type DocumentActionResult,
  type TenderDetail,
  type TenderDocumentRow,
} from '../api/client';

const MAX_FILE_BYTES = 5 * 1024 * 1024;

function healthClass(health: string): string {
  if (health === 'RED') return 'badge badge-red';
  if (health === 'AMBER') return 'badge badge-amber';
  return 'badge badge-green';
}

function approvalClass(status: string): string {
  if (status === 'APPROVED') return 'badge badge-green';
  if (status === 'REJECTED') return 'badge badge-red';
  return 'badge badge-amber';
}

function formatDate(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
}

function formatValue(value: number | null | undefined, currency: string): string {
  if (value == null) {
    return '—';
  }
  return `${currency} ${value.toLocaleString()}`;
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} B`;
  }
  return `${(bytes / 1024).toFixed(1)} KB`;
}

export function TenderDetailPage() {
  const { tenderId } = useParams<{ tenderId: string }>();
  const [tender, setTender] = useState<TenderDetail | null>(null);
  const [documents, setDocuments] = useState<TenderDocumentRow[]>([]);
  const [error, setError] = useState('');
  const [uploadError, setUploadError] = useState('');
  const [uploadSuccess, setUploadSuccess] = useState('');
  const [uploading, setUploading] = useState(false);
  const [docName, setDocName] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [actionError, setActionError] = useState('');
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);
  const [rejectTarget, setRejectTarget] = useState<TenderDocumentRow | null>(null);
  const [rejectReason, setRejectReason] = useState('');
  const [rejectError, setRejectError] = useState('');
  const [deleteTarget, setDeleteTarget] = useState<TenderDocumentRow | null>(null);
  const [deleteError, setDeleteError] = useState('');
  const [commentsTarget, setCommentsTarget] = useState<TenderDocumentRow | null>(null);

  function extractError(err: unknown, fallback: string): string {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { data?: { error?: string } } };
      return axiosErr.response?.data?.error ?? fallback;
    }
    return err instanceof Error ? err.message : fallback;
  }

  function applyHealthUpdate(result: DocumentActionResult) {
    setTender((prev) => (prev ? {
      ...prev,
      health: result.tenderHealth,
      latestStatusComment: result.tenderStatusComment,
    } : prev));
  }

  function loadTenderData(id: string) {
    setError('');
    Promise.all([fetchTenderById(id), fetchTenderDocuments(id)])
      .then(([tenderData, documentRows]) => {
        setTender(tenderData);
        setDocuments(documentRows);
      })
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load tender'));
  }

  useEffect(() => {
    if (!tenderId) {
      setError('Tender ID is missing');
      return;
    }
    loadTenderData(tenderId);
  }, [tenderId]);

  async function handleUpload(e: React.FormEvent) {
    e.preventDefault();
    if (!tenderId) {
      return;
    }

    setUploadError('');
    setUploadSuccess('');

    if (!docName.trim()) {
      setUploadError('Document name is required');
      return;
    }
    if (!file) {
      setUploadError('Please choose a PDF file');
      return;
    }
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      setUploadError('Only PDF files are allowed');
      return;
    }
    if (file.size > MAX_FILE_BYTES) {
      setUploadError('File exceeds maximum size of 5 MB');
      return;
    }

    setUploading(true);
    try {
      const result = await uploadTenderDocument(tenderId, docName.trim(), file);
      setDocuments((prev) => [result.item, ...prev]);
      applyHealthUpdate(result);
      setDocName('');
      setFile(null);
      setUploadSuccess(`"${result.item.docName}" uploaded and sent for approval.`);
    } catch (err) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { data?: { error?: string } } };
        setUploadError(axiosErr.response?.data?.error ?? 'Failed to upload document');
      } else {
        setUploadError(err instanceof Error ? err.message : 'Failed to upload document');
      }
    } finally {
      setUploading(false);
    }
  }

  async function handleApprove(doc: TenderDocumentRow) {
    if (!tenderId) {
      return;
    }
    setActionError('');
    setActionLoadingId(doc.id);
    try {
      const result = await approveTenderDocument(tenderId, doc.id);
      setDocuments((prev) => prev.map((row) => (row.id === doc.id ? result.item! : row)));
      applyHealthUpdate(result);
    } catch (err) {
      setActionError(extractError(err, 'Failed to approve document'));
    } finally {
      setActionLoadingId(null);
    }
  }

  function openRejectModal(doc: TenderDocumentRow) {
    setRejectTarget(doc);
    setRejectReason(doc.rejectionReason ?? '');
    setRejectError('');
  }

  function closeRejectModal() {
    setRejectTarget(null);
    setRejectReason('');
    setRejectError('');
  }

  async function confirmReject() {
    if (!tenderId || !rejectTarget) {
      return;
    }
    if (!rejectReason.trim()) {
      setRejectError('Rejection reason is required');
      return;
    }

    setRejectError('');
    setActionLoadingId(rejectTarget.id);
    try {
      const result = await rejectTenderDocument(tenderId, rejectTarget.id, rejectReason.trim());
      setDocuments((prev) => prev.map((row) => (row.id === rejectTarget.id ? result.item! : row)));
      applyHealthUpdate(result);
      closeRejectModal();
    } catch (err) {
      setRejectError(extractError(err, 'Failed to reject document'));
    } finally {
      setActionLoadingId(null);
    }
  }

  function openCommentsModal(doc: TenderDocumentRow) {
    setCommentsTarget(doc);
  }

  function closeCommentsModal() {
    setCommentsTarget(null);
  }

  function openDeleteModal(doc: TenderDocumentRow) {
    setDeleteTarget(doc);
    setDeleteError('');
  }

  function closeDeleteModal() {
    setDeleteTarget(null);
    setDeleteError('');
  }

  async function confirmDelete() {
    if (!tenderId || !deleteTarget) {
      return;
    }

    setDeleteError('');
    setActionLoadingId(deleteTarget.id);
    try {
      const result = await deleteTenderDocument(tenderId, deleteTarget.id);
      setDocuments((prev) => prev.filter((row) => row.id !== deleteTarget.id));
      applyHealthUpdate(result);
      closeDeleteModal();
    } catch (err) {
      setDeleteError(extractError(err, 'Failed to delete document'));
    } finally {
      setActionLoadingId(null);
    }
  }

  function handleDownload(doc: TenderDocumentRow) {
    if (!tenderId) {
      return;
    }
    window.open(getTenderDocumentDownloadUrl(tenderId, doc.id), '_blank', 'noopener,noreferrer');
  }

  if (error) {
    return (
      <>
        <div className="page-header-row">
          <div>
            <h2 className="page-title">Tender Details</h2>
          </div>
          <Link to="/tenders" className="btn">Back to list</Link>
        </div>
        <p className="error">{error}</p>
      </>
    );
  }

  if (!tender) {
    return <p className="page-sub">Loading tender…</p>;
  }

  return (
    <>
      <div className="page-header-row">
        <div>
          <h2 className="page-title">{tender.tenderId} · {tender.name}</h2>
          <p className="page-sub">
            <span className="badge badge-blue">{tender.stage}</span>
            {' '}
            <span className={healthClass(tender.health)}>{tender.health}</span>
            {' '}
            {tender.completionPercent}% complete · Closes {formatDate(tender.closingAt)}
          </p>
        </div>
        <Link to="/tenders" className="btn">Back to list</Link>
      </div>

      <div className="detail-panel">
        <h3 className="detail-section-title">Summary</h3>
        <dl className="detail-grid">
          <div className="detail-item">
            <dt>Customer</dt>
            <dd>{tender.customer} ({tender.customerCode})</dd>
          </div>
          <div className="detail-item">
            <dt>Tender reference</dt>
            <dd>{tender.tenderReference}</dd>
          </div>
          <div className="detail-item">
            <dt>Tender type</dt>
            <dd>{tender.tenderType}</dd>
          </div>
          <div className="detail-item">
            <dt>Estimated value</dt>
            <dd>{formatValue(tender.estimatedValue, tender.currency)}</dd>
          </div>
          <div className="detail-item">
            <dt>Owner</dt>
            <dd>{tender.owner}</dd>
          </div>
          <div className="detail-item">
            <dt>Priority</dt>
            <dd>{tender.priority}</dd>
          </div>
          <div className="detail-item">
            <dt>Approval status</dt>
            <dd>{tender.approvalStatus}</dd>
          </div>
          <div className="detail-item">
            <dt>Submission mode</dt>
            <dd>{tender.submissionMode}</dd>
          </div>
          <div className="detail-item detail-item-wide">
            <dt>Latest status comment</dt>
            <dd>{tender.latestStatusComment ?? '—'}</dd>
          </div>
        </dl>
      </div>

      <div className="detail-panel doc-panel">
        <h3 className="detail-section-title">Documents</h3>
        <p className="page-sub">
          Upload mandatory PDF documents (max 5 MB). Health is recalculated when documents are missing,
          pending approval, or approved.
        </p>

        {uploadSuccess && <p className="success">{uploadSuccess}</p>}
        {uploadError && <p className="error">{uploadError}</p>}
        {actionError && <p className="error">{actionError}</p>}

        <form className="doc-upload-form" onSubmit={handleUpload}>
          <label className="form-field">
            <span>Document name *</span>
            <input
              type="text"
              maxLength={200}
              value={docName}
              onChange={(e) => setDocName(e.target.value)}
              placeholder="e.g. OEM Authorization Letter"
            />
          </label>
          <label className="form-field">
            <span>PDF file * (max 5 MB)</span>
            <input
              type="file"
              accept=".pdf,application/pdf"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            />
          </label>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={uploading}>
              {uploading ? 'Uploading…' : 'Upload & submit for approval'}
            </button>
          </div>
        </form>

        <table className="data-table">
          <thead>
            <tr>
              <th>Document</th>
              <th>File</th>
              <th>Size</th>
              <th>Approval</th>
              <th>Submitted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {documents.length === 0 && (
              <tr>
                <td colSpan={6}>No documents uploaded yet.</td>
              </tr>
            )}
            {documents.map((doc) => (
              <tr key={doc.id}>
                <td>{doc.docName}</td>
                <td>{doc.fileName}</td>
                <td>{formatFileSize(doc.fileSizeBytes)}</td>
                <td>
                  <span className={approvalClass(doc.approvalStatus)}>{doc.approvalStatus}</span>
                </td>
                <td>{doc.submittedAt ? formatDate(doc.submittedAt) : '—'}</td>
                <td>
                  <div className="icon-actions">
                    <button
                      type="button"
                      className="icon-btn"
                      title="Download"
                      onClick={() => handleDownload(doc)}
                      disabled={actionLoadingId === doc.id}
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3v12m0 0l4-4m-4 4l-4-4M5 21h14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
                    </button>
                    <button
                      type="button"
                      className="icon-btn icon-btn-success"
                      title="Approve"
                      onClick={() => handleApprove(doc)}
                      disabled={actionLoadingId === doc.id}
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 13l4 4L19 7" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
                    </button>
                    <button
                      type="button"
                      className="icon-btn icon-btn-warn"
                      title="Reject"
                      onClick={() => openRejectModal(doc)}
                      disabled={actionLoadingId === doc.id}
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 6l12 12M18 6L6 18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
                    </button>
                    <button
                      type="button"
                      className="icon-btn"
                      title="Comments"
                      onClick={() => openCommentsModal(doc)}
                      disabled={actionLoadingId === doc.id}
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 5h16v11H8l-4 4V5z" fill="none" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" /></svg>
                    </button>
                    <button
                      type="button"
                      className="icon-btn icon-btn-danger"
                      title="Delete"
                      onClick={() => openDeleteModal(doc)}
                      disabled={actionLoadingId === doc.id}
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 7h16M9 7V5h6v2m-7 4v7m4-7v7m4-7v7M7 7l1 12h8l1-12" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {rejectTarget && (
        <div className="modal-overlay" onClick={closeRejectModal}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
            <h3 className="modal-title">Reject document</h3>
            <p className="modal-sub">{rejectTarget.docName}</p>
            {rejectError && <p className="error">{rejectError}</p>}
            <label className="form-field form-field-wide">
              <span>Rejection reason *</span>
              <textarea
                rows={4}
                maxLength={2000}
                value={rejectReason}
                onChange={(e) => setRejectReason(e.target.value)}
                placeholder="Explain why this document is rejected"
              />
            </label>
            <div className="form-actions">
              <button type="button" className="btn" onClick={closeRejectModal}>Cancel</button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={confirmReject}
                disabled={actionLoadingId === rejectTarget.id}
              >
                {actionLoadingId === rejectTarget.id ? 'Rejecting…' : 'Reject document'}
              </button>
            </div>
          </div>
        </div>
      )}

      {commentsTarget && (
        <div className="modal-overlay" onClick={closeCommentsModal}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
            <h3 className="modal-title">Rejection comments</h3>
            <p className="modal-sub">{commentsTarget.docName}</p>
            {commentsTarget.approvalStatus === 'REJECTED' && commentsTarget.rejectionReason ? (
              <div className="modal-comment-box">{commentsTarget.rejectionReason}</div>
            ) : (
              <p className="modal-info">
                Rejection comments are shown here only when a document has been rejected.
              </p>
            )}
            <div className="form-actions">
              <button type="button" className="btn" onClick={closeCommentsModal}>Close</button>
            </div>
          </div>
        </div>
      )}

      {deleteTarget && (
        <div className="modal-overlay" onClick={closeDeleteModal}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
            <h3 className="modal-title">Delete document</h3>
            <p className="modal-sub">
              Delete <strong>{deleteTarget.docName}</strong>? This will permanently remove the file
              and cannot be undone.
            </p>
            {deleteError && <p className="error">{deleteError}</p>}
            <div className="form-actions">
              <button type="button" className="btn" onClick={closeDeleteModal}>Cancel</button>
              <button
                type="button"
                className="btn btn-danger"
                onClick={confirmDelete}
                disabled={actionLoadingId === deleteTarget.id}
              >
                {actionLoadingId === deleteTarget.id ? 'Deleting…' : 'Delete document'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
