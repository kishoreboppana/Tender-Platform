import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  createTender,
  fetchCustomers,
  type CustomerOption,
  type CreateTenderPayload,
} from '../api/client';

const TENDER_TYPES = ['OPEN', 'LIMITED', 'EOI', 'RFQ'];
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

export function CreateTenderPage() {
  const navigate = useNavigate();
  const [customers, setCustomers] = useState<CustomerOption[]>([]);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    name: '',
    customerId: '',
    tenderReference: '',
    tenderType: 'OPEN',
    estimatedValue: '',
    currency: 'INR',
    closingAt: '',
    tenderOwnerName: '',
    priority: 'MEDIUM',
    latestStatusComment: '',
    completionPercent: '0',
  });

  useEffect(() => {
    fetchCustomers()
      .then(setCustomers)
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load customers'));
  }, []);

  function updateField(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setSaving(true);

    const closingAt = new Date(form.closingAt);
    if (Number.isNaN(closingAt.getTime())) {
      setError('Closing date/time is invalid');
      setSaving(false);
      return;
    }

    const payload: CreateTenderPayload = {
      name: form.name.trim(),
      customerId: Number(form.customerId),
      tenderReference: form.tenderReference.trim(),
      tenderType: form.tenderType,
      closingAt: closingAt.toISOString(),
      tenderOwnerName: form.tenderOwnerName.trim(),
      priority: form.priority,
      currency: form.currency.trim() || 'INR',
      completionPercent: Number(form.completionPercent) || 0,
    };

    if (form.estimatedValue.trim()) {
      payload.estimatedValue = Number(form.estimatedValue);
    }
    if (form.latestStatusComment.trim()) {
      payload.latestStatusComment = form.latestStatusComment.trim();
    }

    try {
      const created = await createTender(payload);
      navigate('/tenders', { state: { createdId: created.tenderId } });
    } catch (err) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { data?: { error?: string } } };
        setError(axiosErr.response?.data?.error ?? 'Failed to save tender');
      } else {
        setError(err instanceof Error ? err.message : 'Failed to save tender');
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <div className="page-header-row">
        <div>
          <h2 className="page-title">Create Tender</h2>
          <p className="page-sub">Register a new tender — saved to PostgreSQL</p>
        </div>
        <Link to="/tenders" className="btn">Back to list</Link>
      </div>

      {error && <p className="error">{error}</p>}

      <form className="form-panel" onSubmit={handleSubmit}>
        <div className="form-grid">
          <label className="form-field">
            <span>Tender name *</span>
            <input
              type="text"
              required
              maxLength={300}
              value={form.name}
              onChange={(e) => updateField('name', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Customer *</span>
            <select
              required
              value={form.customerId}
              onChange={(e) => updateField('customerId', e.target.value)}
            >
              <option value="">Select customer</option>
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.customerCode})
                </option>
              ))}
            </select>
          </label>

          <label className="form-field">
            <span>Tender reference *</span>
            <input
              type="text"
              required
              maxLength={150}
              value={form.tenderReference}
              onChange={(e) => updateField('tenderReference', e.target.value)}
              placeholder="e.g. NHAI/EPC/2026/4412"
            />
          </label>

          <label className="form-field">
            <span>Tender type *</span>
            <select
              value={form.tenderType}
              onChange={(e) => updateField('tenderType', e.target.value)}
            >
              {TENDER_TYPES.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </label>

          <label className="form-field">
            <span>Estimated value</span>
            <input
              type="number"
              min="0"
              step="0.01"
              value={form.estimatedValue}
              onChange={(e) => updateField('estimatedValue', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Currency</span>
            <input
              type="text"
              maxLength={3}
              value={form.currency}
              onChange={(e) => updateField('currency', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Closing date & time *</span>
            <input
              type="datetime-local"
              required
              value={form.closingAt}
              onChange={(e) => updateField('closingAt', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Tender owner *</span>
            <input
              type="text"
              required
              maxLength={200}
              value={form.tenderOwnerName}
              onChange={(e) => updateField('tenderOwnerName', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Priority *</span>
            <select
              value={form.priority}
              onChange={(e) => updateField('priority', e.target.value)}
            >
              {PRIORITIES.map((p) => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </label>

          <label className="form-field">
            <span>Completion %</span>
            <input
              type="number"
              min="0"
              max="100"
              value={form.completionPercent}
              onChange={(e) => updateField('completionPercent', e.target.value)}
            />
          </label>

          <label className="form-field form-field-wide">
            <span>Status comment</span>
            <textarea
              rows={3}
              maxLength={2000}
              value={form.latestStatusComment}
              onChange={(e) => updateField('latestStatusComment', e.target.value)}
            />
          </label>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving…' : 'Save tender'}
          </button>
        </div>
      </form>
    </>
  );
}
