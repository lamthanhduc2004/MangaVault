import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getChapterById } from '../services/storyService';

export default function ChapterReaderPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [chapter, setChapter] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    window.scrollTo(0, 0);
    getChapterById(id)
      .then(setChapter)
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
  if (!chapter) return <p className="muted">Đang tải...</p>;

  const nav = (
    <div className="reader-nav">
      <button
        disabled={!chapter.prevChapterId}
        onClick={() => navigate(`/chapters/${chapter.prevChapterId}`)}
      >
        ← Chương trước
      </button>
      <Link to={`/stories/${chapter.storyId}`} className="muted">Danh sách chương</Link>
      <button
        disabled={!chapter.nextChapterId}
        onClick={() => navigate(`/chapters/${chapter.nextChapterId}`)}
      >
        Chương sau →
      </button>
    </div>
  );

  return (
    <div className="reader">
      <p className="muted small">
        <Link to={`/stories/${chapter.storyId}`} className="muted">{chapter.storyTitle}</Link>
      </p>
      <h1 className="reader-title">
        Chương {chapter.chapterNumber}{chapter.title ? `: ${chapter.title}` : ''}
      </h1>
      {nav}
      <article className="reader-content">
        {chapter.content
          ? chapter.content.split('\n').map((p, i) => p.trim() && <p key={i}>{p}</p>)
          : <p className="muted">Chương này chưa có nội dung.</p>}
      </article>
      {nav}
    </div>
  );
}
