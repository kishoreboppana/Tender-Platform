import { useNavigate } from 'react-router-dom';

export function LoginPage() {
  const navigate = useNavigate();

  function login() {
    sessionStorage.setItem('tms-demo-user', 'demo');
    navigate('/dashboard');
  }

  return (
    <div className="login-wrap">
      <div className="login-card">
        <h1>Tender Management Platform</h1>
        <p>Local demo · data from PostgreSQL</p>
        <button type="button" className="btn btn-primary btn-block" onClick={login}>
          Continue as Demo User
        </button>
        <p className="hint">Entra ID SSO will be added in Phase 1 (Stage 03)</p>
      </div>
    </div>
  );
}
