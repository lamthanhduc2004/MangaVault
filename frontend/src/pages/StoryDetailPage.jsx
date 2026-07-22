import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getStoryById, getChaptersOfStory } from '../services/storyService';
import StatusBadge from '../components/StatusBadge';

export default function StoryDetailPage() {
  const { id } = useParams();
  const [story, setStory] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([getStoryById(id), getChaptersOfStory(id)])
      .then(([s, c]) => { setStory(s); setChapters(c); })
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, [id]);

  if (error) {
    return (
      <div>
        <p className="error">Lỗi: {error}</p>
        <Link to="/">← Quay lại danh sách</Link>
      </div>
    );
  }
  if (!story) return <p className="muted">Đang tải...</p>;

  return (
    <div className="detail">
      <Link to="/" className="muted">← Quay lại danh sách</Link>
      <div className="detail-main">
        <div className="detail-cover">
          {story.coverUrl
            ? <img src={story.coverUrl} alt={story.title} />
            : <span className="cover-placeholder large">📖</span>}
        </div>
        <div>
          <h1>{story.title}</h1>
          {story.author && <p className="muted">Tác giả: {story.author}</p>}
          <p><StatusBadge status={story.status} /> <span className="badge">{chapters.length} chương</span></p>
          <p>{story.description || 'Chưa có mô tả.'}</p>
          {chapters.length > 0 && (
            <Link to={`/chapters/${chapters[0].id}`} className="btn-primary" style={{ width: 'fit-content' }}>
              Đọc từ đầu
            </Link>
          )}
        </div>
      </div>

      <section className="chapter-section">
        <h2>Danh sách chương</h2>
        {chapters.length === 0
          ? <p className="muted">Chưa có chương nào.</p>
          : (
            <ol className="chapter-list">
              {chapters.map((c) => (
                <li key={c.id}>
                  <Link to={`/chapters/${c.id}`} className="chapter-link">
                    <span>Chương {c.chapterNumber}{c.title ? `: ${c.title}` : ''}</span>
                    <span className="muted small">
                      {new Date(c.createdAt).toLocaleDateString('vi-VN')}
                    </span>
                  </Link>
                </li>
              ))}
            </ol>
          )}
      </section>
    </div>
  );
}
