import { NavLink, Outlet, useNavigate } from 'react-router-dom';

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
        <nav className="sidebar">
          <NavLink to="/dashboard" className={({ isActive }) => (isActive ? 'nav-item active' : 'nav-item')}>
            Dashboard
          </NavLink>
          <NavLink to="/tenders" end className={({ isActive }) => (isActive ? 'nav-item active' : 'nav-item')}>
            Tender List
          </NavLink>
          <NavLink to="/customers" className={({ isActive }) => (isActive ? 'nav-item active' : 'nav-item')}>
            Customer List
          </NavLink>
          <NavLink to="/tenders/new" className={({ isActive }) => (isActive ? 'nav-item active' : 'nav-item')}>
            Create Tender
          </NavLink>
        </nav>
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
