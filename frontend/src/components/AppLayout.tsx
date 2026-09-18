import { Outlet, useNavigate } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export function AppLayout() {
  const navigate = useNavigate();

  function logout() {
    sessionStorage.removeItem('tms-demo-user');
    navigate('/login');
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <span className="brand">TMS v9.2.2</span>
        <span className="user-bar">
          Demo User ·{' '}
          <button type="button" className="link-btn" onClick={logout}>Logout</button>
        </span>
      </header>
      <div className="layout">
        <Sidebar />
        <main className="content">
          <div className="phase-banner">
            React + Vite · Spring Boot API · PostgreSQL (demo seed data)
          </div>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
