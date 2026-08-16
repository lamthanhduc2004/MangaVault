import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getStats } from '../../services/commentService';
import { formatCount, formatRating } from '../../utils/format';

const StatCard = ({ label, value, icon }) => (
  <div className="stat-card">
    <span className="stat-icon">{icon}</span>
    <div>
      <div className="stat-value">{value}</div>
      <div className="muted small">{label}</div>
    </div>
  </div>
);

export default function AdminDashboardPage() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    document.title = 'Bảng điều khiển — MangaVault';
    getStats()
      .then(setStats)
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, []);

  return (
    <div>
      <h1>Bảng điều khiển</h1>

      <nav className="admin-nav">
        <Link to="/admin" className="btn-small">Quản trị truyện</Link>
        <Link to="/admin/genres" className="btn-small">Thể loại</Link>
        <Link to="/admin/users" className="btn-small">Người dùng</Link>
        <Link to="/admin/comments/reported" className="btn-small">Quản trị bình luận</Link>
      </nav>

      {error && <p className="error">Lỗi: {error}</p>}
      {!stats && !error && <p className="muted">Đang tải...</p>}

      {stats && (
        <>
          <div className="stat-grid">
            <StatCard icon="👥" label="Người dùng" value={formatCount(stats.totalUsers)} />
            <StatCard icon="📚" label="Truyện" value={formatCount(stats.totalStories)} />
            <StatCard icon="📄" label="Chương" value={formatCount(stats.totalChapters)} />
            <StatCard icon="💬" label="Bình luận" value={formatCount(stats.totalComments)} />
            <StatCard icon="🆕" label="Truyện cập nhật 7 ngày qua" value={formatCount(stats.storiesUpdatedLast7Days)} />
          </div>

          <div className="stat-columns">
            <section>
              <h2>Xem nhiều nhất</h2>
              {stats.topViewedStories.length === 0
                ? <p className="muted">Chưa có dữ liệu.</p>
                : (
                  <ol className="chapter-list">
                    {stats.topViewedStories.map((s) => (
                      <li key={s.id}>
                        <Link to={`/stories/${s.id}`} className="chapter-link">
                          <span>{s.title}</span>
                          <span className="muted small">
                            👁 {formatCount(s.viewCount)}
                            {s.ratingCount > 0 && ` · ⭐ ${formatRating(s.ratingAvg)}`}
                          </span>
                        </Link>
                      </li>
                    ))}
                  </ol>
                )}
            </section>

            <section>
              <h2>Được theo dõi nhiều nhất</h2>
              {stats.topFollowedStories.length === 0
                ? <p className="muted">Chưa có dữ liệu.</p>
                : (
                  <ol className="chapter-list">
                    {stats.topFollowedStories.map((s) => (
                      <li key={s.id}>
                        <Link to={`/stories/${s.id}`} className="chapter-link">
                          <span>{s.title}</span>
                          <span className="muted small">👁 {formatCount(s.viewCount)}</span>
                        </Link>
                      </li>
                    ))}
                  </ol>
                )}
            </section>
          </div>
        </>
      )}
    </div>
  );
}
