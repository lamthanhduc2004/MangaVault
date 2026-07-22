import { Link, Outlet } from 'react-router-dom';

export default function MainLayout() {
  return (
    <div className="app">
      <header className="header">
        <Link to="/" className="brand">📚 MangaVault</Link>
        <nav>
          <Link to="/" className="nav-link">Danh sách truyện</Link>
          <Link to="/admin" className="nav-link">Quản trị</Link>
        </nav>
      </header>
      <main className="content">
        <Outlet />
      </main>
      <footer className="footer">MangaVault — website đọc truyện (demo thực tập)</footer>
    </div>
  );
}
