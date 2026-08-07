import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  getComments,
  createComment,
  updateComment,
  deleteComment,
  reportComment,
} from '../services/commentService';
import { useAuth } from '../context/AuthContext';
import Pagination from './Pagination';
import { formatDateTime } from '../utils/format';

const MAX_LENGTH = 1000;

export default function CommentSection({ storyId }) {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [content, setContent] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editingContent, setEditingContent] = useState('');
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    try {
      setData(await getComments(storyId, { page }));
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, [storyId, page]);

  useEffect(() => { load(); }, [load]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;
    setSaving(true);
    setError(null);
    try {
      await createComment(storyId, content.trim());
      setContent('');
      setPage(0);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleSaveEdit = async (id) => {
    if (!editingContent.trim()) return;
    setError(null);
    try {
      await updateComment(id, editingContent.trim());
      setEditingId(null);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleDelete = async (comment) => {
    if (!window.confirm('Xóa bình luận này?')) return;
    setError(null);
    try {
      await deleteComment(comment.id);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleReport = async (comment) => {
    const reason = window.prompt('Lý do báo cáo (không bắt buộc):', '');
    if (reason === null) return;
    setError(null);
    try {
      await reportComment(comment.id, reason);
      window.alert('Đã gửi báo cáo tới quản trị viên.');
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <section className="chapter-section">
      <h2>Bình luận {data ? `(${data.totalElements})` : ''}</h2>
      {error && <p className="error">Lỗi: {error}</p>}

      {user ? (
        <form onSubmit={handleSubmit} className="form" style={{ marginBottom: '1.5rem' }}>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={3}
            maxLength={MAX_LENGTH}
            placeholder="Viết bình luận của bạn..."
          />
          <div className="comment-form-actions">
            <span className="muted small">{content.length}/{MAX_LENGTH}</span>
            <button type="submit" className="btn-primary" disabled={saving || !content.trim()}>
              {saving ? 'Đang gửi...' : 'Gửi bình luận'}
            </button>
          </div>
        </form>
      ) : (
        <p className="muted">
          <Link to="/login">Đăng nhập</Link> để tham gia bình luận.
        </p>
      )}

      {data && data.items.length === 0 && <p className="muted">Chưa có bình luận nào.</p>}

      <ul className="comment-list">
        {data?.items.map((comment) => (
          <li key={comment.id} className="comment-item">
            <div className="comment-avatar">
              {comment.authorAvatarUrl
                ? <img src={comment.authorAvatarUrl} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                : <span>{comment.authorName.charAt(0).toUpperCase()}</span>}
            </div>
            <div className="comment-body">
              <div className="comment-head">
                <strong>{comment.authorName}</strong>
                <span className="muted small">{formatDateTime(comment.createdAt)}</span>
              </div>

              {editingId === comment.id ? (
                <>
                  <textarea
                    value={editingContent}
                    onChange={(e) => setEditingContent(e.target.value)}
                    rows={3}
                    maxLength={MAX_LENGTH}
                  />
                  <div className="table-actions" style={{ justifyContent: 'flex-start' }}>
                    <button className="btn-small" onClick={() => handleSaveEdit(comment.id)}>Lưu</button>
                    <button className="btn-small" onClick={() => setEditingId(null)}>Hủy</button>
                  </div>
                </>
              ) : (
                <p className="comment-content">{comment.content}</p>
              )}

              {user && editingId !== comment.id && (
                <div className="comment-actions">
                  {comment.mine ? (
                    <>
                      <button
                        className="link-button"
                        onClick={() => { setEditingId(comment.id); setEditingContent(comment.content); }}
                      >
                        Sửa
                      </button>
                      <button className="link-button" onClick={() => handleDelete(comment)}>Xóa</button>
                    </>
                  ) : (
                    <button className="link-button" onClick={() => handleReport(comment)}>Báo cáo</button>
                  )}
                </div>
              )}
            </div>
          </li>
        ))}
      </ul>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </section>
  );
}
