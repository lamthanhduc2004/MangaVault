import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getStories } from '../services/storyService';
import StoryCard from '../components/StoryCard';

export default function HomePage() {
  const [updated, setUpdated] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    getStories({ sort: 'updated', size: 8 })
      .then((res) => setUpdated(res.items))
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, []);

  return (
    <div>
      <section className="hero">
        <h1>📚 MangaVault</h1>
        <p className="muted">Đọc truyện chữ trực tuyến — cập nhật mỗi ngày.</p>
      </section>

      <section>
        <div className="section-header">
          <h2>Truyện mới cập nhật</h2>
          <Link to="/stories" className="muted">Xem tất cả →</Link>
        </div>
        {error && <p className="error">Lỗi: {error}</p>}
        {!updated && !error && <p className="muted">Đang tải...</p>}
        {updated && (
          updated.length === 0
            ? <p className="muted">Chưa có truyện nào.</p>
            : (
              <div className="grid">
                {updated.map((s) => <StoryCard key={s.id} story={s} />)}
              </div>
            )
        )}
      </section>
    </div>
  );
}
