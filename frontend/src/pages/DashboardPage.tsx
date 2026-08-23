import { useEffect, useState } from 'react';
import { fetchDashboardSummary, type DashboardSummary } from '../api/client';

export function DashboardPage() {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardSummary()
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load'));
  }, []);

  return (
    <>
      <h2 className="page-title">Portfolio Dashboard</h2>
      {data && (
        <p className="page-sub">
          Tenant: {data.tenantName} · Digest: {data.digestStatus} · Last run {data.digestLastRun}
        </p>
      )}
      {error && <p className="error">{error}</p>}
      {data && (
        <div className="kpi-row">
          <div className="kpi">
            <div className="label">Active Tenders</div>
            <div className="value">{data.activeTenders}</div>
          </div>
          <div className="kpi kpi-red">
            <div className="label">Closing Today</div>
            <div className="value">{data.closingToday}</div>
          </div>
          <div className="kpi">
            <div className="label">Pending Approvals</div>
            <div className="value">{data.pendingApprovals}</div>
          </div>
          <div className="kpi">
            <div className="label">Win Rate</div>
            <div className="value">{data.winRatePercent}%</div>
          </div>
        </div>
      )}
    </>
  );
}
