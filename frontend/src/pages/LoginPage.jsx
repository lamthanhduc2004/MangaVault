import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await login(form);
      // Return to the page that redirected here, or home.
      navigate(location.state?.from || '/', { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      setSaving(false);
    }
  };

  return (
    <div className="form-page auth-page">
      <h1>Đăng nhập</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      <form onSubmit={handleSubmit} className="form">
        <label>
          Tên đăng nhập
          <input value={form.username} onChange={set('username')} autoComplete="username" />
        </label>
        <label>
          Mật khẩu
          <input type="password" value={form.password} onChange={set('password')} autoComplete="current-password" />
        </label>
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>
      </form>
      <p className="muted" style={{ marginTop: '1rem' }}>
        Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
      </p>
    </div>
  );
}
