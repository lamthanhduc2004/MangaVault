import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getReportedComments, dismissReports, deleteCommentAsAdmin } from '../../services/commentService';
import Pagination from '../../components/Pagination';
import { formatDateTime } from '../../utils/format';

export default function AdminReportedCommentPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getReportedComments({ page }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [page]);

  useEffect(() => {
    document.title = 'Bình luận bị báo cáo — MangaVault';
    load();
  }, [load]);

  const handleDelete = async (row) => {
    if (!window.confirm('Xóa vĩnh viễn bình luận này?')) return;
    setError(null);
    try {
      await deleteCommentAsAdmin(row.id);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleDismiss = async (row) => {
    setError(null);
    try {
      await dismissReports(row.id);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <p className="small"><Link to="/admin">← Quản trị</Link></p>
      <h1>Bình luận bị báo cáo</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      {!data && !error && <p className="muted">Đang tải...</p>}

      {data && data.items.length === 0 && (
        <p className="muted">Không có bình luận nào bị báo cáo. 🎉</p>
      )}

      <ul className="library-list">
        {data?.items.map((row) => (
          <li key={row.id} className="library-item">
            <div className="library-body">
              <p className="comment-content">{row.content}</p>
              <p className="muted small">
                bởi <strong>{row.authorUsername}</strong> · trong{' '}
                <Link to={`/stories/${row.storyId}`}>{row.storyTitle}</Link> ·{' '}
                {formatDateTime(row.createdAt)}
              </p>
              <span className="badge badge-hiatus">{row.reportCount} báo cáo</span>
            </div>
            <div className="library-actions">
              <button className="btn-small" onClick={() => handleDismiss(row)}>Bỏ qua</button>
              <button className="btn-small btn-danger" onClick={() => handleDelete(row)}>Xóa</button>
            </div>
          </li>
        ))}
      </ul>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
