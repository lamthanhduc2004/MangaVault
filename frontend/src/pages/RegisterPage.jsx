import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../services/authService';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const [form, setForm] = useState({ username: '', email: '', password: '', confirm: '' });
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password !== form.confirm) {
      setError('Mật khẩu nhập lại không khớp');
      return;
    }
    setSaving(true);
    setError(null);
    try {
      await register({ username: form.username, email: form.email, password: form.password });
      // Log the new account in right away for a smoother flow.
      await login({ username: form.username, password: form.password });
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      setSaving(false);
    }
  };

  return (
    <div className="form-page auth-page">
      <h1>Đăng ký</h1>
      {error && <p className="error">Lỗi: {error}</p>}
      <form onSubmit={handleSubmit} className="form">
        <label>
          Tên đăng nhập
          <input value={form.username} onChange={set('username')} autoComplete="username" />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={set('email')} autoComplete="email" />
        </label>
        <label>
          Mật khẩu
          <input type="password" value={form.password} onChange={set('password')} autoComplete="new-password" />
        </label>
        <label>
          Nhập lại mật khẩu
          <input type="password" value={form.confirm} onChange={set('confirm')} autoComplete="new-password" />
        </label>
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Đang đăng ký...' : 'Đăng ký'}
        </button>
      </form>
      <p className="muted" style={{ marginTop: '1rem' }}>
        Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
      </p>
    </div>
  );
}
