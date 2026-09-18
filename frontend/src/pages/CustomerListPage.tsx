import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { fetchCustomers, type CustomerOption } from '../api/client';

function typeClass(customerType: string): string {
  if (customerType === 'GOVERNMENT') return 'badge badge-blue';
  if (customerType === 'PSU') return 'badge badge-amber';
  return 'badge';
}

export function CustomerListPage() {
  const location = useLocation();
  const [rows, setRows] = useState<CustomerOption[]>([]);
  const [error, setError] = useState('');
  const createdCode = (location.state as { createdCode?: string } | null)?.createdCode;

  useEffect(() => {
    fetchCustomers()
      .then(setRows)
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [createdCode]);

  return (
    <>
      <div className="page-header-row">
        <div>
          <h2 className="page-title">Customer Register</h2>
          <p className="page-sub">Live data from PostgreSQL</p>
        </div>
        <Link to="/customers/new" className="btn btn-primary">Create customer</Link>
      </div>
      {createdCode && <p className="success">Customer {createdCode} created successfully.</p>}
      {error && <p className="error">{error}</p>}
      <table className="data-table">
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>Type</th>
            <th>Contact email</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((c) => (
            <tr key={c.id}>
              <td>{c.customerCode}</td>
              <td>{c.name}</td>
              <td><span className={typeClass(c.customerType)}>{c.customerType}</span></td>
              <td>{c.contactEmail ?? '—'}</td>
              <td>
                <span className={c.active ? 'badge badge-green' : 'badge badge-red'}>
                  {c.active ? 'Active' : 'Inactive'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
