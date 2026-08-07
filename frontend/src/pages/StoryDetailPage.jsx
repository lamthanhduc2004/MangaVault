import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getStoryById, getChaptersOfStory } from '../services/storyService';
import { getProgress, getRatingSummary, rateStory } from '../services/libraryService';
import StatusBadge from '../components/StatusBadge';
import GenreChips from '../components/GenreChips';
import FollowButton from '../components/FollowButton';
import RatingStars from '../components/RatingStars';
import CommentSection from '../components/CommentSection';
import { useAuth } from '../context/AuthContext';
import { formatCount, formatRating } from '../utils/format';

export default function StoryDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [story, setStory] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [rating, setRating] = useState(null);
  const [progress, setProgress] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([getStoryById(id), getChaptersOfStory(id)])
      .then(([s, c]) => {
        setStory(s);
        setChapters(c);
        document.title = `${s.title} — MangaVault`;
      })
      .catch((err) => setError(err.response?.data?.message || err.message));

    getRatingSummary(id).then(setRating).catch(() => {});
  }, [id]);

  // Resume position is per-user, so it is fetched separately once signed in.
  useEffect(() => {
    if (!user) {
      setProgress(null);
      return;
    }
    getProgress(id).then(setProgress).catch(() => setProgress(null));
  }, [id, user]);

  const handleRate = async (score) => {
    try {
      setRating(await rateStory(id, score));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  if (error && !story) {
    return (
      <div>
        <p className="error">Lỗi: {error}</p>
        <Link to="/stories">← Quay lại danh sách</Link>
      </div>
    );
  }
  if (!story) return <p className="muted">Đang tải...</p>;

  return (
    <div className="detail">
      <Link to="/stories" className="muted">← Quay lại danh sách</Link>
      <div className="detail-main">
        <div className="detail-cover">
          {story.coverUrl
            ? <img src={story.coverUrl} alt={story.title} onError={(e) => { e.currentTarget.style.display = 'none'; }} />
            : <span className="cover-placeholder large">📖</span>}
        </div>
        <div>
          <h1>{story.title}</h1>
          {story.author && <p className="muted">Tác giả: {story.author}</p>}

          <div className="card-meta">
            <StatusBadge status={story.status} />
            <span className="badge">{story.chapterCount ?? chapters.length} chương</span>
            <span className="muted small">👁 {formatCount(story.viewCount)} lượt xem</span>
            {story.ratingCount > 0 && (
              <span className="muted small">
                ⭐ {formatRating(story.ratingAvg)} ({story.ratingCount} đánh giá)
              </span>
            )}
          </div>

          <GenreChips genres={story.genres} />

          <p>{story.description || 'Chưa có mô tả.'}</p>

          <div className="detail-actions">
            {chapters.length > 0 && (
              <Link to={`/chapters/${chapters[0].id}`} className="btn-small">Đọc từ đầu</Link>
            )}
            {progress && (
              <Link to={`/chapters/${progress.chapterId}`} className="btn-primary">
                Đọc tiếp chương {progress.chapterNumber}
              </Link>
            )}
            <FollowButton storyId={id} />
          </div>

          <div className="rating-row">
            <span className="muted small">{user ? 'Đánh giá của bạn:' : 'Đánh giá:'}</span>
            <RatingStars
              value={user ? (rating?.myScore ?? 0) : (rating?.average ?? 0)}
              readOnly={!user}
              onRate={handleRate}
            />
            {rating?.count > 0 && (
              <span className="muted small">
                Trung bình {formatRating(rating.average)}/5 · {rating.count} lượt
              </span>
            )}
          </div>
          {error && <p className="error">Lỗi: {error}</p>}
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

      <CommentSection storyId={id} />
    </div>
  );
}
