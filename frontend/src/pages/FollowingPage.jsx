import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getFollows, unfollowStory } from '../services/libraryService';
import StatusBadge from '../components/StatusBadge';
import Pagination from '../components/Pagination';
import { formatDateTime } from '../utils/format';

export default function FollowingPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getFollows({ page }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [page]);

  useEffect(() => {
    document.title = 'Truyện đang theo dõi — MangaVault';
    load();
  }, [load]);

  const handleUnfollow = async (item) => {
    if (!window.confirm(`Bỏ theo dõi "${item.title}"?`)) return;
    try {
      await unfollowStory(item.storyId);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <h1>Truyện đang theo dõi</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      {!data && !error && <p className="muted">Đang tải...</p>}

      {data && data.items.length === 0 && (
        <p className="muted">
          Bạn chưa theo dõi truyện nào. <Link to="/stories">Khám phá truyện →</Link>
        </p>
      )}

      <ul className="library-list">
        {data?.items.map((item) => {
          const hasNewChapter = item.latestChapterNumber != null
            && (item.lastReadChapterNumber == null || item.latestChapterNumber > item.lastReadChapterNumber);

          return (
            <li key={item.storyId} className="library-item">
              <Link to={`/stories/${item.storyId}`} className="library-cover">
                {item.coverUrl
                  ? <img src={item.coverUrl} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                  : <span>📖</span>}
              </Link>
              <div className="library-body">
                <Link to={`/stories/${item.storyId}`}><strong>{item.title}</strong></Link>
                {item.author && <p className="muted small">{item.author}</p>}
                <div className="card-meta">
                  <StatusBadge status={item.status} />
                  <span className="muted small">
                    Mới nhất: {item.latestChapterNumber != null ? `Chương ${item.latestChapterNumber}` : 'chưa có'}
                  </span>
                  {hasNewChapter && <span className="badge badge-ongoing">Có chương mới</span>}
                </div>
                <p className="muted small">
                  {item.lastReadChapterId
                    ? <>Đã đọc tới chương {item.lastReadChapterNumber} · </>
                    : <>Chưa bắt đầu đọc · </>}
                  Cập nhật {formatDateTime(item.storyUpdatedAt)}
                </p>
              </div>
              <div className="library-actions">
                {item.lastReadChapterId && (
                  <Link to={`/chapters/${item.lastReadChapterId}`} className="btn-small">Đọc tiếp</Link>
                )}
                <button className="btn-small btn-danger" onClick={() => handleUnfollow(item)}>Bỏ theo dõi</button>
              </div>
            </li>
          );
        })}
      </ul>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
