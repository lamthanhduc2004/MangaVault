import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Frontend guard only improves UX — real authorization is enforced by
// Spring Security on /api/admin/** (see backend SecurityConfig).
export default function RequireAdmin() {
  const { user, isAdmin } = useAuth();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  if (!isAdmin) {
    return (
      <div>
        <h1>Không có quyền truy cập</h1>
        <p className="muted">Trang quản trị chỉ dành cho tài khoản ADMIN.</p>
      </div>
    );
  }
  return <Outlet />;
}
