import { useCallback, useEffect, useState } from 'react';
import { getGenres, createGenre, updateGenre, deleteGenre } from '../../services/genreService';

const EMPTY = { name: '', slug: '', description: '' };

/** Rough slug helper for Vietnamese input — the admin can still edit it by hand. */
const slugify = (value) =>
  value
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');

export default function AdminGenreListPage() {
  const [genres, setGenres] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    try {
      setGenres(await getGenres());
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  }, []);

  useEffect(() => {
    document.title = 'Quản trị thể loại — MangaVault';
    load();
  }, [load]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      if (editingId) {
        await updateGenre(editingId, form);
      } else {
        await createGenre(form);
      }
      setForm(EMPTY);
      setEditingId(null);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (genre) => {
    if (!window.confirm(`Xóa thể loại "${genre.name}"?`)) return;
    setError(null);
    try {
      await deleteGenre(genre.id);
      await load();
    } catch (err) {
      // 4096 = the genre is still assigned to stories.
      setError(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <p className="small"><a href="/admin">← Quản trị</a></p>
      <h1>Quản trị thể loại</h1>
      {error && <p className="error">Lỗi: {error}</p>}

      <table className="table" style={{ marginTop: '1.25rem' }}>
        <thead>
          <tr>
            <th>Tên thể loại</th>
            <th>Slug</th>
            <th>Mô tả</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {genres.map((genre) => (
            <tr key={genre.id}>
              <td>{genre.name}</td>
              <td className="muted">{genre.slug}</td>
              <td className="muted">{genre.description || '—'}</td>
              <td className="table-actions">
                <button
                  className="btn-small"
                  onClick={() => {
                    setEditingId(genre.id);
                    setForm({
                      name: genre.name,
                      slug: genre.slug,
                      description: genre.description || '',
                    });
                  }}
                >
                  Sửa
                </button>
                <button className="btn-small btn-danger" onClick={() => handleDelete(genre)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {genres.length === 0 && <p className="muted" style={{ marginTop: '1rem' }}>Chưa có thể loại nào.</p>}

      <div className="form-page" style={{ marginTop: '2.5rem' }}>
        <h2>{editingId ? 'Sửa thể loại' : 'Thêm thể loại'}</h2>
        <form onSubmit={handleSubmit} className="form">
          <label>
            Tên thể loại *
            <input
              value={form.name}
              required
              maxLength={100}
              onChange={(e) => {
                const name = e.target.value;
                // Auto-fill the slug only while creating, never overwrite a manual edit.
                setForm((f) => ({
                  ...f,
                  name,
                  slug: !editingId && (f.slug === '' || f.slug === slugify(f.name)) ? slugify(name) : f.slug,
                }));
              }}
              placeholder="Tiên hiệp"
            />
          </label>
          <label>
            Mô tả
            <textarea
              rows={3}
              value={form.description}
              maxLength={500}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Mô tả ngắn về thể loại"
            />
          </label>
          <label>
            Slug *
            <input
              value={form.slug}
              required
              maxLength={100}
              onChange={(e) => setForm({ ...form, slug: e.target.value })}
              placeholder="tien-hiep"
            />
          </label>
          <div className="form-row">
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? 'Đang lưu...' : editingId ? 'Cập nhật' : 'Thêm thể loại'}
            </button>
            {editingId && (
              <button type="button" className="btn-small" onClick={() => { setEditingId(null); setForm(EMPTY); }}>
                Hủy
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
