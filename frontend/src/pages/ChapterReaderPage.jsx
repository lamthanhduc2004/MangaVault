import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getChapterById } from '../services/storyService';
import { saveProgress } from '../services/libraryService';
import { useAuth } from '../context/AuthContext';

const FONT_SIZES = [
  { value: 'small', label: 'A', px: '0.95rem' },
  { value: 'medium', label: 'A', px: '1.05rem' },
  { value: 'large', label: 'A', px: '1.2rem' },
];

const LINE_HEIGHTS = [
  { value: 'compact', label: 'Hẹp', lh: '1.5' },
  { value: 'normal', label: 'Vừa', lh: '1.9' },
  { value: 'relaxed', label: 'Rộng', lh: '2.4' },
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
  const { user } = useAuth();
  const [chapter, setChapter] = useState(null);
  const [error, setError] = useState(null);
  // Font size and line height are reader-specific; the light/dark theme is global (ThemeContext).
  const [fontSize, setFontSize] = useState(() => readPrefs().fontSize ?? 'medium');
  const [lineHeight, setLineHeight] = useState(() => readPrefs().lineHeight ?? 'normal');

  useEffect(() => {
    localStorage.setItem('mv_reader_prefs', JSON.stringify({ fontSize, lineHeight }));
  }, [fontSize, lineHeight]);

  useEffect(() => {
    window.scrollTo(0, 0);
    getChapterById(id)
      .then((c) => {
        setChapter(c);
        document.title = `Chương ${c.chapterNumber} — ${c.storyTitle}`;
      })
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, [id]);

  // Bookmark the position for signed-in readers. Failure is swallowed on purpose:
  // losing a bookmark must never interrupt reading.
  useEffect(() => {
    if (!user) return;
    saveProgress(id).catch(() => {});
  }, [id, user]);

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
  const lineHeightValue = LINE_HEIGHTS.find((l) => l.value === lineHeight)?.lh ?? '1.9';

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
        <span className="muted small" style={{ marginLeft: '0.5rem' }}>Giãn dòng:</span>
        {LINE_HEIGHTS.map((l) => (
          <button
            key={l.value}
            className={`font-btn${lineHeight === l.value ? ' active' : ''}`}
            onClick={() => setLineHeight(l.value)}
            title={`Giãn dòng ${l.label}`}
          >
            {l.label}
          </button>
        ))}
      </div>

      {nav}
      <article className="reader-content" style={{ fontSize: fontPx, lineHeight: lineHeightValue }}>
        {chapter.content
          ? chapter.content.split('\n').map((p, i) => p.trim() && <p key={i}>{p}</p>)
          : <p className="muted">Chương này chưa có nội dung.</p>}
      </article>
      {nav}
    </div>
  );
}
