import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  getAdminComments,
  dismissReports,
  deleteCommentAsAdmin,
  setCommentHidden,
} from '../../services/commentService';
import { setUserStatus } from '../../services/userService';
import Pagination from '../../components/Pagination';
import SearchBar from '../../components/SearchBar';
import { formatDateTime } from '../../utils/format';

export default function AdminReportedCommentPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [reportedOnly, setReportedOnly] = useState(true);
  const [visibility, setVisibility] = useState('');
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getAdminComments({
        keyword,
        reportedOnly,
        hidden: visibility,
        page,
      }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [keyword, reportedOnly, visibility, page]);

  useEffect(() => {
    document.title = 'Quản trị bình luận — MangaVault';
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

  const handleVisibility = async (row) => {
    const hidden = !row.hidden;
    if (!window.confirm(`${hidden ? 'Ẩn' : 'Khôi phục'} bình luận này?`)) return;
    setError(null);
    try {
      await setCommentHidden(row.id, hidden);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  // Lets the moderator ban a repeat offender directly from the queue instead
  // of hunting them down in the user management page.
  const handleBanAuthor = async (row) => {
    if (!window.confirm(`Khóa tài khoản "${row.authorUsername}"? Người này sẽ không đăng nhập được nữa.`)) return;
    setError(null);
    try {
      await setUserStatus(row.authorId, 'BANNED');
      window.alert(`Đã khóa tài khoản "${row.authorUsername}".`);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <p className="small"><Link to="/admin">← Quản trị</Link></p>
      <div className="list-header">
        <h1>Quản trị bình luận</h1>
        <div className="list-tools">
          <SearchBar
            initial={keyword}
            placeholder="Nội dung, tài khoản hoặc truyện..."
            onSearch={(value) => { setKeyword(value); setPage(0); }}
          />
          <select
            className="status-filter"
            value={visibility}
            onChange={(e) => { setVisibility(e.target.value); setPage(0); }}
          >
            <option value="">Tất cả trạng thái</option>
            <option value="false">Đang hiển thị</option>
            <option value="true">Đã ẩn</option>
          </select>
          <label className="checkbox-chip">
            <input
              type="checkbox"
              checked={reportedOnly}
              onChange={(e) => { setReportedOnly(e.target.checked); setPage(0); }}
            />
            Chỉ bị báo cáo
          </label>
        </div>
      </div>
      {error && <p className="error">Lỗi: {error}</p>}
      {!data && !error && <p className="muted">Đang tải...</p>}

      {data && data.items.length === 0 && (
        <p className="muted">Không tìm thấy bình luận phù hợp.</p>
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
              <div className="card-meta">
                {row.reportCount > 0 && <span className="badge badge-hiatus">{row.reportCount} báo cáo</span>}
                {row.hidden && <span className="badge">Đã ẩn</span>}
              </div>
            </div>
            <div className="library-actions">
              {row.reportCount > 0 && <button className="btn-small" onClick={() => handleDismiss(row)}>Bỏ qua báo cáo</button>}
              <button className="btn-small" onClick={() => handleVisibility(row)}>
                {row.hidden ? 'Khôi phục' : 'Ẩn'}
              </button>
              <button className="btn-small btn-danger" onClick={() => handleDelete(row)}>Xóa bình luận</button>
              <button className="btn-small btn-danger" onClick={() => handleBanAuthor(row)}>Khóa tài khoản</button>
            </div>
          </li>
        ))}
      </ul>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
