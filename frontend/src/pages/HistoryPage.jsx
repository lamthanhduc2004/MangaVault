import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getHistory, deleteHistoryEntry } from '../services/libraryService';
import Pagination from '../components/Pagination';
import { formatDateTime } from '../utils/format';

export default function HistoryPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getHistory({ page }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [page]);

  useEffect(() => {
    document.title = 'Lịch sử đọc — MangaVault';
    load();
  }, [load]);

  const handleRemove = async (item) => {
    try {
      await deleteHistoryEntry(item.storyId);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <h1>Lịch sử đọc</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      {!data && !error && <p className="muted">Đang tải...</p>}

      {data && data.items.length === 0 && (
        <p className="muted">
          Chưa có lịch sử đọc. <Link to="/stories">Bắt đầu đọc →</Link>
        </p>
      )}

      <ul className="library-list">
        {data?.items.map((item) => (
          <li key={item.storyId} className="library-item">
            <Link to={`/stories/${item.storyId}`} className="library-cover">
              {item.coverUrl
                ? <img src={item.coverUrl} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                : <span>📖</span>}
            </Link>
            <div className="library-body">
              <Link to={`/stories/${item.storyId}`}><strong>{item.title}</strong></Link>
              <p className="muted small">
                Chương {item.chapterNumber}{item.chapterTitle ? `: ${item.chapterTitle}` : ''}
              </p>
              <p className="muted small">Đọc lúc {formatDateTime(item.readAt)}</p>
            </div>
            <div className="library-actions">
              <Link to={`/chapters/${item.chapterId}`} className="btn-primary">Đọc tiếp</Link>
              <button className="btn-small btn-danger" onClick={() => handleRemove(item)}>Xóa</button>
            </div>
          </li>
        ))}
      </ul>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
