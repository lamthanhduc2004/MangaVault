import { Link, Outlet } from 'react-router-dom';

export default function MainLayout() {
  return (
    <div className="app">
      <header className="header">
        <Link to="/" className="brand">📚 MangaVault</Link>
        <nav>
          <Link to="/" className="nav-link">Danh sách truyện</Link>
          <Link to="/stories/new" className="nav-link btn-primary">+ Thêm truyện</Link>
        </nav>
      </header>
      <main className="content">
        <Outlet />
      </main>
      <footer className="footer">MangaVault — website đọc truyện (demo thực tập)</footer>
    </div>
  );
}
