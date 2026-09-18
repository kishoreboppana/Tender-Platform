import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';
import { CreateTenderPage } from './pages/CreateTenderPage';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { CreateCustomerPage } from './pages/CreateCustomerPage';
import { CustomerListPage } from './pages/CustomerListPage';
import { TenderDetailPage } from './pages/TenderDetailPage';
import { PlaceholderPage } from './pages/PlaceholderPage';
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
        <Route path="/tenders/:tenderId" element={<TenderDetailPage />} />
        <Route path="/customers" element={<CustomerListPage />} />
        <Route path="/customers/new" element={<CreateCustomerPage />} />
        <Route path="/admin/users" element={<PlaceholderPage title="Users" description="Tenant user administration" />} />
        <Route path="/admin/roles" element={<PlaceholderPage title="Roles" description="Role and permission management" />} />
        <Route path="/admin/settings" element={<PlaceholderPage title="Settings" description="Tenant configuration" />} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
