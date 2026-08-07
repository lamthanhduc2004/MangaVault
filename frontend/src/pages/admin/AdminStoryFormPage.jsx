import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createStory, updateStory, getAdminStoryById } from '../../services/adminService';
import { getGenres } from '../../services/genreService';

const INITIAL = {
  title: '',
  slug: '',
  author: '',
  description: '',
  coverUrl: '',
  status: 'ONGOING',
  visibility: 'PRIVATE',
  genreIds: [],
};

export default function AdminStoryFormPage() {
  const { id } = useParams(); // undefined = create mode
  const [form, setForm] = useState(INITIAL);
  const [genres, setGenres] = useState([]);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(Boolean(id));
  const navigate = useNavigate();

  useEffect(() => {
    getGenres().then(setGenres).catch(() => setGenres([]));
  }, []);

  useEffect(() => {
    if (!id) return;
    getAdminStoryById(id)
      .then((story) =>
        setForm({
          title: story.title ?? '',
          slug: story.slug ?? '',
          author: story.author ?? '',
          description: story.description ?? '',
          coverUrl: story.coverUrl ?? '',
          status: story.status ?? 'ONGOING',
          visibility: story.visibility ?? 'PRIVATE',
          genreIds: (story.genres ?? []).map((g) => g.id),
        }),
      )
      .catch((err) => setError(err.response?.data?.message || err.message))
      .finally(() => setLoading(false));
  }, [id]);

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const toggleGenre = (genreId) => {
    setForm((f) => ({
      ...f,
      genreIds: f.genreIds.includes(genreId)
        ? f.genreIds.filter((g) => g !== genreId)
        : [...f.genreIds, genreId],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    const payload = {
      ...form,
      author: form.author || null,
      description: form.description || null,
      coverUrl: form.coverUrl || null,
    };
    try {
      if (id) {
        await updateStory(id, payload);
        navigate('/admin');
      } else {
        const created = await createStory(payload);
        navigate(`/admin/stories/${created.id}/chapters`);
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      setSaving(false);
    }
  };

  if (loading) return <p className="muted">Đang tải...</p>;

  return (
    <div className="form-page">
      <h1>{id ? 'Sửa truyện' : 'Thêm truyện'}</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      <form onSubmit={handleSubmit} className="form">
        <label>
          Tên truyện *
          <input value={form.title} onChange={set('title')} required maxLength={255} placeholder="Tên truyện" />
        </label>
        <label>
          Slug *
          <input value={form.slug} onChange={set('slug')} required maxLength={255} placeholder="ten-truyen" />
        </label>
        <label>
          Tác giả
          <input value={form.author} onChange={set('author')} placeholder="Tên tác giả" />
        </label>
        <label>
          Mô tả
          <textarea value={form.description} onChange={set('description')} rows={4} />
        </label>
        <label>
          Ảnh bìa (URL)
          <input value={form.coverUrl} onChange={set('coverUrl')} placeholder="https://..." />
        </label>
        <div className="form-row">
          <label>
            Trạng thái
            <select value={form.status} onChange={set('status')}>
              <option value="ONGOING">Đang ra</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="HIATUS">Tạm ngưng</option>
            </select>
          </label>
          <label>
            Hiển thị
            <select value={form.visibility} onChange={set('visibility')}>
              <option value="PRIVATE">Riêng tư</option>
              <option value="PUBLIC">Công khai</option>
            </select>
          </label>
        </div>

        <fieldset className="genre-picker">
          <legend>Thể loại</legend>
          {genres.length === 0
            ? <p className="muted small">Chưa có thể loại nào — thêm ở mục Quản trị thể loại.</p>
            : genres.map((genre) => (
              <label key={genre.id} className="checkbox-chip">
                <input
                  type="checkbox"
                  checked={form.genreIds.includes(genre.id)}
                  onChange={() => toggleGenre(genre.id)}
                />
                {genre.name}
              </label>
            ))}
        </fieldset>

        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Đang lưu...' : id ? 'Cập nhật' : 'Lưu truyện'}
        </button>
      </form>
    </div>
  );
}
