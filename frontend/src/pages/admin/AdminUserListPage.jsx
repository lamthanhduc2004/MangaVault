import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getUsers, setUserStatus, setUserRole } from '../../services/userService';
import { useAuth } from '../../context/AuthContext';
import SearchBar from '../../components/SearchBar';
import Pagination from '../../components/Pagination';
import { formatDateTime } from '../../utils/format';

export default function AdminUserListPage() {
  const { user: currentUser } = useAuth();
  const [data, setData] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    try {
      setData(await getUsers({ keyword, page }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [keyword, page]);

  useEffect(() => {
    document.title = 'Quản trị người dùng — MangaVault';
    load();
  }, [load]);

  const handleToggleStatus = async (row) => {
    const next = row.status === 'BANNED' ? 'ACTIVE' : 'BANNED';
    const label = next === 'BANNED' ? 'Khóa' : 'Mở khóa';
    if (!window.confirm(`${label} tài khoản "${row.username}"?`)) return;
    setError(null);
    try {
      await setUserStatus(row.id, next);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleToggleRole = async (row) => {
    const next = row.role === 'ADMIN' ? 'USER' : 'ADMIN';
    if (!window.confirm(`Đổi quyền của "${row.username}" thành ${next}?`)) return;
    setError(null);
    try {
      await setUserRole(row.id, next);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <p className="small"><Link to="/admin">← Quản trị</Link></p>
      <div className="list-header">
        <h1>Quản trị người dùng</h1>
        <div className="list-tools">
          <SearchBar initial={keyword} onSearch={(kw) => { setKeyword(kw); setPage(0); }} />
        </div>
      </div>
      {error && <p className="error">Lỗi: {error}</p>}

      {data && (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>Tài khoản</th>
                <th>Email</th>
                <th>Quyền</th>
                <th>Trạng thái</th>
                <th>Tham gia</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {data.items.map((row) => {
                // The backend rejects self-modification; hide the controls to match.
                const isSelf = currentUser?.id === row.id || currentUser?.username === row.username;
                return (
                  <tr key={row.id}>
                    <td>
                      <strong>{row.username}</strong>
                      {row.displayName && <div className="muted small">{row.displayName}</div>}
                    </td>
                    <td className="muted">{row.email}</td>
                    <td>
                      <span className={`badge ${row.role === 'ADMIN' ? 'badge-ongoing' : ''}`}>{row.role}</span>
                    </td>
                    <td>
                      <span className={`badge ${row.status === 'BANNED' ? 'badge-hiatus' : 'badge-completed'}`}>
                        {row.status === 'BANNED' ? 'Đã khóa' : 'Hoạt động'}
                      </span>
                    </td>
                    <td className="muted small">{formatDateTime(row.createdAt)}</td>
                    <td className="table-actions">
                      {isSelf ? (
                        <span className="muted small">Tài khoản của bạn</span>
                      ) : (
                        <>
                          <button className="btn-small" onClick={() => handleToggleRole(row)}>
                            {row.role === 'ADMIN' ? 'Hạ quyền' : 'Nâng ADMIN'}
                          </button>
                          <button
                            className={`btn-small${row.status === 'BANNED' ? '' : ' btn-danger'}`}
                            onClick={() => handleToggleStatus(row)}
                          >
                            {row.status === 'BANNED' ? 'Mở khóa' : 'Khóa'}
                          </button>
                        </>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {data.items.length === 0 && <p className="muted" style={{ marginTop: '1rem' }}>Không tìm thấy người dùng.</p>}
          <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
