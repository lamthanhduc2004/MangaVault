import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAdminStories, deleteStory } from '../../services/adminService';
import Pagination from '../../components/Pagination';
import SearchBar from '../../components/SearchBar';
import StatusBadge from '../../components/StatusBadge';

export default function AdminStoryListPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getAdminStories({ keyword, page, size: 10 }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [keyword, page]);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async (story) => {
    if (!window.confirm(`Xóa truyện "${story.title}" và toàn bộ chương của nó?`)) return;
    try {
      await deleteStory(story.id);
      load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <div className="list-header">
        <h1>Quản trị truyện</h1>
        <div className="list-tools">
          <SearchBar initial={keyword} onSearch={(kw) => { setKeyword(kw); setPage(0); }} />
          <Link to="/admin/stories/new" className="btn-primary">+ Thêm truyện</Link>
        </div>
      </div>
      {error && <p className="error">Lỗi: {error}</p>}
      {data && (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>Tên truyện</th>
                <th>Slug</th>
                <th>Trạng thái</th>
                <th>Hiển thị</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {data.items.map((story) => (
                <tr key={story.id}>
                  <td>
                    <Link to={`/stories/${story.id}`}>{story.title}</Link>
                  </td>
                  <td className="muted">{story.slug}</td>
                  <td><StatusBadge status={story.status} /></td>
                  <td>{story.visibility === 'PUBLIC' ? 'Công khai' : 'Riêng tư'}</td>
                  <td className="table-actions">
                    <Link to={`/admin/stories/${story.id}/chapters`} className="btn-small">Chương</Link>
                    <Link to={`/admin/stories/${story.id}/edit`} className="btn-small">Sửa</Link>
                    <button onClick={() => handleDelete(story)} className="btn-small btn-danger">Xóa</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {data.items.length === 0 && (
            <p className="muted" style={{ marginTop: '1rem' }}>
              {keyword ? `Không tìm thấy truyện nào cho "${keyword}".` : 'Chưa có truyện nào.'}
            </p>
          )}
          <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
