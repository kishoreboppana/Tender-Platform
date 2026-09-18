import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createCustomer, type CreateCustomerPayload } from '../api/client';

const CUSTOMER_TYPES = ['GOVERNMENT', 'PSU', 'PRIVATE', 'OTHER'];

export function CreateCustomerPage() {
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    customerCode: '',
    name: '',
    customerType: 'GOVERNMENT',
    contactEmail: '',
    active: true,
  });

  function updateField(field: string, value: string | boolean) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setSaving(true);

    const payload: CreateCustomerPayload = {
      customerCode: form.customerCode.trim(),
      name: form.name.trim(),
      customerType: form.customerType,
      active: form.active,
    };

    if (form.contactEmail.trim()) {
      payload.contactEmail = form.contactEmail.trim();
    }

    try {
      const created = await createCustomer(payload);
      navigate('/customers', { state: { createdCode: created.customerCode } });
    } catch (err) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { data?: { error?: string } } };
        setError(axiosErr.response?.data?.error ?? 'Failed to save customer');
      } else {
        setError(err instanceof Error ? err.message : 'Failed to save customer');
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <div className="page-header-row">
        <div>
          <h2 className="page-title">Create Customer</h2>
          <p className="page-sub">Register a new customer — saved to PostgreSQL</p>
        </div>
        <Link to="/customers" className="btn">Back to list</Link>
      </div>

      {error && <p className="error">{error}</p>}

      <form className="form-panel" onSubmit={handleSubmit}>
        <div className="form-grid">
          <label className="form-field">
            <span>Customer code *</span>
            <input
              type="text"
              required
              maxLength={30}
              value={form.customerCode}
              onChange={(e) => updateField('customerCode', e.target.value)}
              placeholder="e.g. CUS-0004"
            />
          </label>

          <label className="form-field">
            <span>Name *</span>
            <input
              type="text"
              required
              maxLength={200}
              value={form.name}
              onChange={(e) => updateField('name', e.target.value)}
            />
          </label>

          <label className="form-field">
            <span>Customer type *</span>
            <select
              value={form.customerType}
              onChange={(e) => updateField('customerType', e.target.value)}
            >
              {CUSTOMER_TYPES.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </label>

          <label className="form-field">
            <span>Contact email</span>
            <input
              type="email"
              maxLength={320}
              value={form.contactEmail}
              onChange={(e) => updateField('contactEmail', e.target.value)}
              placeholder="procurement@example.gov"
            />
          </label>

          <label className="form-field form-field-checkbox">
            <span>
              <input
                type="checkbox"
                checked={form.active}
                onChange={(e) => updateField('active', e.target.checked)}
              />
              Active
            </span>
          </label>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving…' : 'Save customer'}
          </button>
        </div>
      </form>
    </>
  );
}
