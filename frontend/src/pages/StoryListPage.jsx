import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getStories } from '../services/storyService';
import { getGenres } from '../services/genreService';
import StoryCard from '../components/StoryCard';
import SearchBar from '../components/SearchBar';
import Pagination from '../components/Pagination';
import SortSelect from '../components/SortSelect';
import GenreFilter from '../components/GenreFilter';

const STATUS_OPTIONS = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'ONGOING', label: 'Đang ra' },
  { value: 'COMPLETED', label: 'Hoàn thành' },
  { value: 'HIATUS', label: 'Tạm ngưng' },
];

export default function StoryListPage() {
  // Filters live in the URL so results can be linked and survive a refresh —
  // the home page links straight into a pre-filtered list.
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get('keyword') ?? '';
  const status = searchParams.get('status') ?? '';
  const genre = searchParams.get('genre') ?? '';
  const sort = searchParams.get('sort') ?? 'latest';
  const page = Number(searchParams.get('page') ?? 0);

  const [data, setData] = useState(null);
  const [genres, setGenres] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const updateParams = (changes) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(changes).forEach(([key, value]) => {
      if (value === '' || value == null) next.delete(key);
      else next.set(key, value);
    });
    // Any filter change invalidates the current page number.
    if (!('page' in changes)) next.delete('page');
    setSearchParams(next);
  };

  useEffect(() => {
    document.title = 'Danh sách truyện — MangaVault';
    getGenres().then(setGenres).catch(() => setGenres([]));
  }, []);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    getStories({ keyword, status, genre, sort, page })
      .then((res) => { if (!cancelled) { setData(res); setError(null); } })
      .catch((err) => { if (!cancelled) setError(err.response?.data?.message || err.message); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [keyword, status, genre, sort, page]);

  return (
    <div>
      <div className="list-header">
        <h1>Danh sách truyện</h1>
        <div className="list-tools">
          <GenreFilter genres={genres} value={genre} onChange={(g) => updateParams({ genre: g })} />
          <select
            className="status-filter"
            value={status}
            onChange={(e) => updateParams({ status: e.target.value })}
            aria-label="Lọc theo trạng thái"
          >
            {STATUS_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>{o.label}</option>
            ))}
          </select>
          <SortSelect value={sort} onChange={(s) => updateParams({ sort: s })} />
          <SearchBar initial={keyword} onSearch={(kw) => updateParams({ keyword: kw })} />
        </div>
      </div>

      {loading && <p className="muted">Đang tải...</p>}
      {error && <p className="error">Lỗi: {error}</p>}

      {data && !loading && (
        data.items.length === 0
          ? <p className="muted">Không tìm thấy truyện nào{keyword ? ` cho "${keyword}"` : ''}.</p>
          : (
            <>
              <p className="muted small">{data.totalElements} truyện</p>
              <div className="grid">
                {data.items.map((s) => <StoryCard key={s.id} story={s} />)}
              </div>
              <Pagination
                page={data.page}
                totalPages={data.totalPages}
                onChange={(p) => updateParams({ page: p })}
              />
            </>
          )
      )}
    </div>
  );
}
