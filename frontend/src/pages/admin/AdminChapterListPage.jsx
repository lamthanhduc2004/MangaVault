import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  createChapter,
  updateChapter,
  deleteChapter,
  getAdminStoryById,
  getAdminChapters,
  getAdminChapterById,
  setChapterPublished,
  moveChapter,
} from '../../services/adminService';

const EMPTY = { chapterNumber: '', title: '', content: '' };

const nextNumber = (chapters) =>
  chapters.length ? chapters[chapters.length - 1].chapterNumber + 1 : 1;

export default function AdminChapterListPage() {
  const { id: storyId } = useParams();
  const [story, setStory] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null); // null = add mode
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    const [s, c] = await Promise.all([getAdminStoryById(storyId), getAdminChapters(storyId)]);
    setStory(s);
    setChapters(c);
    return c;
  }, [storyId]);

  useEffect(() => {
    load()
      .then((c) => setForm({ ...EMPTY, chapterNumber: nextNumber(c) }))
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, [load]);

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const startEdit = async (summary) => {
    setError(null);
    try {
      // Summaries never include content — fetch the full chapter.
      const full = await getAdminChapterById(summary.id);
      setEditingId(summary.id);
      setForm({ chapterNumber: full.chapterNumber, title: full.title, content: full.content });
      window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const cancelEdit = (list = chapters) => {
    setEditingId(null);
    setForm({ ...EMPTY, chapterNumber: nextNumber(list) });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    const payload = { ...form, chapterNumber: Number(form.chapterNumber) };
    try {
      if (editingId) {
        await updateChapter(editingId, payload);
      } else {
        await createChapter(storyId, payload);
      }
      cancelEdit(await load()); // reset form with the fresh chapter list
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (chapter) => {
    if (!window.confirm(`Xóa chương ${chapter.chapterNumber}: "${chapter.title}"?`)) return;
    setError(null);
    try {
      await deleteChapter(chapter.id);
      const fresh = await load();
      if (editingId === chapter.id) cancelEdit(fresh);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleTogglePublish = async (chapter) => {
    setError(null);
    try {
      await setChapterPublished(chapter.id, !chapter.published);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const handleMove = async (chapter, direction) => {
    setError(null);
    try {
      await moveChapter(chapter.id, direction);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  if (!story) return error ? <p className="error">Lỗi: {error}</p> : <p className="muted">Đang tải...</p>;

  return (
    <div>
      <p className="small"><Link to="/admin">← Quản trị truyện</Link></p>
      <h1>Chương — {story.title}</h1>
      {error && <p className="error">Lỗi: {error}</p>}

      <table className="table" style={{ marginTop: '1.25rem' }}>
        <thead>
          <tr>
            <th style={{ width: '90px' }}>Chương</th>
            <th>Tiêu đề</th>
            <th style={{ width: '110px' }}>Hiển thị</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {chapters.map((chapter, index) => (
            <tr key={chapter.id}>
              <td>{chapter.chapterNumber}</td>
              <td>
                <Link to={`/chapters/${chapter.id}`}>{chapter.title}</Link>
              </td>
              <td>
                <span className={`badge ${chapter.published ? 'badge-completed' : ''}`}>
                  {chapter.published ? 'Công khai' : 'Đang ẩn'}
                </span>
              </td>
              <td className="table-actions">
                <button
                  onClick={() => handleMove(chapter, 'UP')}
                  className="btn-small"
                  disabled={index === 0}
                  title="Đổi lên trên"
                >
                  ↑
                </button>
                <button
                  onClick={() => handleMove(chapter, 'DOWN')}
                  className="btn-small"
                  disabled={index === chapters.length - 1}
                  title="Đổi xuống dưới"
                >
                  ↓
                </button>
                <button onClick={() => handleTogglePublish(chapter)} className="btn-small">
                  {chapter.published ? 'Ẩn' : 'Hiện'}
                </button>
                <button onClick={() => startEdit(chapter)} className="btn-small">Sửa</button>
                <button onClick={() => handleDelete(chapter)} className="btn-small btn-danger">Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {chapters.length === 0 && <p className="muted">Chưa có chương nào.</p>}

      <div className="form-page" style={{ marginTop: '2.5rem' }}>
        <h2>{editingId ? 'Sửa chương' : 'Thêm chương'}</h2>
        <form onSubmit={handleSubmit} className="form">
          <div className="form-row">
            <label>
              Số chương *
              <input type="number" min={1} value={form.chapterNumber} onChange={set('chapterNumber')} />
            </label>
            <label style={{ flex: 3 }}>
              Tiêu đề *
              <input value={form.title} onChange={set('title')} placeholder="Tiêu đề chương" />
            </label>
          </div>
          <label>
            Nội dung *
            <textarea value={form.content} onChange={set('content')} rows={12} placeholder="Nội dung chương..." />
          </label>
          <div className="form-row">
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? 'Đang lưu...' : editingId ? 'Cập nhật chương' : 'Thêm chương'}
            </button>
            {editingId && (
              <button type="button" className="btn-small" onClick={cancelEdit}>Hủy sửa</button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
