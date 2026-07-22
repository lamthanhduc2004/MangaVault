import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function MainLayout() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="app">
      <header className="header">
        <Link to="/" className="brand">📚 MangaVault</Link>
        <nav>
          <Link to="/" className="nav-link">Trang chủ</Link>
          <Link to="/stories" className="nav-link">Danh sách truyện</Link>
          {isAdmin && <Link to="/admin" className="nav-link">Quản trị</Link>}
          {user ? (
            <>
              <span className="nav-user">👤 {user.username}</span>
              <button onClick={handleLogout} className="btn-small">Đăng xuất</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">Đăng nhập</Link>
              <Link to="/register" className="nav-link btn-primary">Đăng ký</Link>
            </>
          )}
        </nav>
      </header>
      <main className="content">
        <Outlet />
      </main>
      <footer className="footer">MangaVault — website đọc truyện (demo thực tập)</footer>
    </div>
  );
}
