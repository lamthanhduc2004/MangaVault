import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createStory } from '../services/storyService';

const INITIAL = {
  title: '',
  slug: '',
  author: '',
  description: '',
  coverUrl: '',
  status: 'ONGOING',
  visibility: 'PRIVATE',
};

export default function StoryCreatePage() {
  const [form, setForm] = useState(INITIAL);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const created = await createStory({
        ...form,
        author: form.author || null,
        description: form.description || null,
        coverUrl: form.coverUrl || null,
      });
      navigate(`/stories/${created.id}`);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      setSaving(false);
    }
  };

  return (
    <div className="form-page">
      <h1>Thêm truyện</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      <form onSubmit={handleSubmit} className="form">
        <label>
          Tên truyện *
          <input value={form.title} onChange={set('title')} placeholder="Tên truyện" />
        </label>
        <label>
          Slug *
          <input value={form.slug} onChange={set('slug')} placeholder="ten-truyen" />
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
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Đang lưu...' : 'Lưu truyện'}
        </button>
      </form>
    </div>
  );
}
