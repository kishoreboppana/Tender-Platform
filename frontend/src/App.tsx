import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';
import { CreateTenderPage } from './pages/CreateTenderPage';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { CustomerListPage } from './pages/CustomerListPage';
import { TenderListPage } from './pages/TenderListPage';

function isLoggedIn(): boolean {
  return sessionStorage.getItem('tms-demo-user') === 'demo';
}

function RequireAuth({ children }: { children: React.ReactNode }) {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <RequireAuth>
            <AppLayout />
          </RequireAuth>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/tenders" element={<TenderListPage />} />
        <Route path="/tenders/new" element={<CreateTenderPage />} />
        <Route path="/customers" element={<CustomerListPage />} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
