import { useState } from 'react';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';

export default function MainLayout() {
  const { user, isAdmin, logout } = useAuth();
  const { dark, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/');
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <div className="app">
      <header className="header">
        <Link to="/" className="brand" onClick={closeMenu}>📚 MangaVault</Link>

        <button
          className="nav-toggle"
          aria-label="Mở menu"
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((open) => !open)}
        >
          ☰
        </button>

        <nav className={menuOpen ? 'nav-open' : ''}>
          <NavLink to="/" className="nav-link" onClick={closeMenu} end>Trang chủ</NavLink>
          <NavLink to="/stories" className="nav-link" onClick={closeMenu}>Danh sách truyện</NavLink>
          {user && (
            <>
              <NavLink to="/following" className="nav-link" onClick={closeMenu}>Theo dõi</NavLink>
              <NavLink to="/history" className="nav-link" onClick={closeMenu}>Lịch sử</NavLink>
            </>
          )}
          {isAdmin && <NavLink to="/admin/dashboard" className="nav-link" onClick={closeMenu}>Quản trị</NavLink>}

          <button
            onClick={toggleTheme}
            className="theme-toggle"
            title={dark ? 'Chuyển nền sáng' : 'Chuyển nền tối'}
          >
            {dark ? '☀️' : '🌙'}
          </button>

          {user ? (
            <>
              <Link to="/profile" className="nav-user" onClick={closeMenu}>
                {user.avatarUrl
                  ? <img src={user.avatarUrl} alt="" className="nav-avatar" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                  : <span aria-hidden="true">👤</span>}
                {user.displayName || user.username}
              </Link>
              <button onClick={handleLogout} className="btn-small">Đăng xuất</button>
            </>
          ) : (
            <>
              <NavLink to="/login" className="nav-link" onClick={closeMenu}>Đăng nhập</NavLink>
              <NavLink to="/register" className="nav-link btn-primary" onClick={closeMenu}>Đăng ký</NavLink>
            </>
          )}
        </nav>
      </header>

      <main className="content">
        <Outlet />
      </main>

      <footer className="footer">MangaVault — website đọc truyện trực tuyến (đồ án thực tập)</footer>
    </div>
  );
}
