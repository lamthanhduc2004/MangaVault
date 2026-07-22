import { useEffect, useState } from 'react';
import { getStories } from '../services/storyService';
import StoryCard from '../components/StoryCard';
import SearchBar from '../components/SearchBar';
import Pagination from '../components/Pagination';

export default function StoryListPage() {
  const [data, setData] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    getStories({ keyword, page })
      .then((res) => { if (!cancelled) { setData(res); setError(null); } })
      .catch((err) => { if (!cancelled) setError(err.response?.data?.message || err.message); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [keyword, page]);

  return (
    <div>
      <div className="list-header">
        <h1>Danh sách truyện</h1>
        <SearchBar initial={keyword} onSearch={(kw) => { setKeyword(kw); setPage(0); }} />
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
