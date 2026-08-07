import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getStories } from '../services/storyService';
import StoryCard from '../components/StoryCard';

const SECTIONS = [
  { key: 'updated', title: 'Truyện mới cập nhật', sort: 'updated' },
  { key: 'rating', title: 'Truyện nổi bật', sort: 'rating' },
  { key: 'views', title: 'Đọc nhiều nhất', sort: 'views' },
];

export default function HomePage() {
  const [sections, setSections] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    document.title = 'MangaVault — Đọc truyện chữ trực tuyến';
    // One round-trip per section, fired together rather than sequentially.
    Promise.all(SECTIONS.map((s) => getStories({ sort: s.sort, size: 8 })))
      .then((results) => setSections(results.map((r) => r.items)))
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, []);

  return (
    <div>
      <section className="hero">
        <h1>📚 MangaVault</h1>
        <p className="muted">Đọc truyện chữ trực tuyến — cập nhật mỗi ngày.</p>
      </section>

      {error && <p className="error">Lỗi: {error}</p>}
      {!sections && !error && <p className="muted">Đang tải...</p>}

      {sections && SECTIONS.map((section, i) => (
        sections[i].length > 0 && (
          <section key={section.key} className="home-section">
            <div className="section-header">
              <h2>{section.title}</h2>
              <Link to={`/stories?sort=${section.sort}`} className="muted">Xem tất cả →</Link>
            </div>
            <div className="grid">
              {sections[i].map((s) => <StoryCard key={s.id} story={s} />)}
            </div>
          </section>
        )
      ))}

      {sections && sections.every((items) => items.length === 0) && (
        <p className="muted">Chưa có truyện nào.</p>
      )}
    </div>
  );
}
