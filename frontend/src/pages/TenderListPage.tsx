import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { fetchTenders, type TenderRow } from '../api/client';
function healthClass(health: string): string {
  if (health === 'RED') return 'badge badge-red';
  if (health === 'AMBER') return 'badge badge-amber';
  return 'badge badge-green';
}

export function TenderListPage() {
  const location = useLocation();
  const [rows, setRows] = useState<TenderRow[]>([]);
  const [error, setError] = useState('');
  const createdId = (location.state as { createdId?: string } | null)?.createdId;

  useEffect(() => {
    fetchTenders()
      .then(setRows)
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, [createdId]);

  return (
    <>
      <div className="page-header-row">
        <div>
          <h2 className="page-title">Tender Register</h2>
          <p className="page-sub">Live data from PostgreSQL</p>
        </div>
        <Link to="/tenders/new" className="btn btn-primary">Create tender</Link>
      </div>
      {createdId && <p className="success">Tender {createdId} created successfully.</p>}
      {error && <p className="error">{error}</p>}
      <table className="data-table">
        <thead>
          <tr>
            <th>Tender ID</th>
            <th>Customer</th>
            <th>Name</th>
            <th>Owner</th>
            <th>Stage</th>
            <th>Priority</th>
            <th>Health</th>
            <th>%</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((t) => (
            <tr key={t.tenderId}>
              <td>
                <Link
                className="tender-id"
                to={`/Tenders/${encodeURIComponent(t.tenderId)}`}
              >
                {t.tenderId}
              </Link>
              </td>
              <td>{t.customer}</td>
              <td>{t.name}</td>
              <td>{t.owner}</td>
              <td><span className="badge badge-blue">{t.stage}</span></td>
              <td>{t.priority}</td>
              <td><span className={healthClass(t.health)}>{t.health}</span></td>
              <td>{t.completionPercent}%</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
