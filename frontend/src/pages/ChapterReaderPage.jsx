import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getChapterById } from '../services/storyService';

const FONT_SIZES = [
  { value: 'small', label: 'A', px: '0.95rem' },
  { value: 'medium', label: 'A', px: '1.05rem' },
  { value: 'large', label: 'A', px: '1.2rem' },
];

const readPrefs = () => {
  try {
    return JSON.parse(localStorage.getItem('mv_reader_prefs')) ?? {};
  } catch {
    return {};
  }
};

export default function ChapterReaderPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [chapter, setChapter] = useState(null);
  const [error, setError] = useState(null);
  const [fontSize, setFontSize] = useState(() => readPrefs().fontSize ?? 'medium');
  const [dark, setDark] = useState(() => readPrefs().dark ?? false);

  useEffect(() => {
    localStorage.setItem('mv_reader_prefs', JSON.stringify({ fontSize, dark }));
  }, [fontSize, dark]);

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
        <Link to="/">← Quay lại trang chủ</Link>
      </div>
    );
  }
  if (!chapter) return <p className="muted">Đang tải...</p>;

  const fontPx = FONT_SIZES.find((f) => f.value === fontSize)?.px ?? '1.05rem';

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

      <div className="reader-options">
        <span className="muted small">Cỡ chữ:</span>
        {FONT_SIZES.map((f) => (
          <button
            key={f.value}
            className={`font-btn font-${f.value}${fontSize === f.value ? ' active' : ''}`}
            onClick={() => setFontSize(f.value)}
            title={`Cỡ chữ ${f.value}`}
          >
            {f.label}
          </button>
        ))}
        <button className={`font-btn${dark ? ' active' : ''}`} onClick={() => setDark(!dark)}>
          {dark ? '☀️ Nền sáng' : '🌙 Nền tối'}
        </button>
      </div>

      {nav}
      <article
        className={`reader-content${dark ? ' reader-dark' : ''}`}
        style={{ fontSize: fontPx }}
      >
        {chapter.content
          ? chapter.content.split('\n').map((p, i) => p.trim() && <p key={i}>{p}</p>)
          : <p className="muted">Chương này chưa có nội dung.</p>}
      </article>
      {nav}
    </div>
  );
}
