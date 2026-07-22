import { useEffect, useState } from 'react';
import { getStories } from '../services/storyService';
import StoryCard from '../components/StoryCard';
import SearchBar from '../components/SearchBar';
import Pagination from '../components/Pagination';

const STATUS_OPTIONS = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'ONGOING', label: 'Đang ra' },
  { value: 'COMPLETED', label: 'Hoàn thành' },
  { value: 'HIATUS', label: 'Tạm ngưng' },
];

export default function StoryListPage() {
  const [data, setData] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    getStories({ keyword, status, page })
      .then((res) => { if (!cancelled) { setData(res); setError(null); } })
      .catch((err) => { if (!cancelled) setError(err.response?.data?.message || err.message); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [keyword, status, page]);

  return (
    <div>
      <div className="list-header">
        <h1>Danh sách truyện</h1>
        <div className="list-tools">
          <select
            className="status-filter"
            value={status}
            onChange={(e) => { setStatus(e.target.value); setPage(0); }}
          >
            {STATUS_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>{o.label}</option>
            ))}
          </select>
          <SearchBar initial={keyword} onSearch={(kw) => { setKeyword(kw); setPage(0); }} />
        </div>
      </div>

      {loading && <p className="muted">Đang tải...</p>}
      {error && <p className="error">Lỗi: {error}</p>}

      {data && !loading && (
        data.items.length === 0
          ? <p className="muted">Không tìm thấy truyện nào{keyword ? ` cho "${keyword}"` : ''}.</p>
          : (
            <>
              <div className="grid">
                {data.items.map((s) => <StoryCard key={s.id} story={s} />)}
              </div>
              <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />
            </>
          )
      )}
    </div>
  );
}
