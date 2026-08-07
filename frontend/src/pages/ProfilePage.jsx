import { useEffect, useState } from 'react';
import { getMe, updateProfile, changePassword } from '../services/userService';
import { useAuth } from '../context/AuthContext';
import { formatDateTime } from '../utils/format';

export default function ProfilePage() {
  const { refreshUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ displayName: '', avatarUrl: '' });
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', confirm: '' });
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    document.title = 'Hồ sơ cá nhân — MangaVault';
    getMe()
      .then((p) => {
        setProfile(p);
        setForm({ displayName: p.displayName ?? '', avatarUrl: p.avatarUrl ?? '' });
      })
      .catch((err) => setError(err.response?.data?.message || err.message));
  }, []);

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      setProfile(await updateProfile(form));
      await refreshUser();
      setMessage('Đã cập nhật hồ sơ.');
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (passwordForm.newPassword !== passwordForm.confirm) {
      setError('Mật khẩu mới nhập lại không khớp');
      return;
    }
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      await changePassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      });
      setPasswordForm({ currentPassword: '', newPassword: '', confirm: '' });
      setMessage('Đã đổi mật khẩu.');
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setSaving(false);
    }
  };

  if (!profile) {
    return error ? <p className="error">Lỗi: {error}</p> : <p className="muted">Đang tải...</p>;
  }

  return (
    <div className="form-page">
      <h1>Hồ sơ cá nhân</h1>
      {message && <p className="success">{message}</p>}
      {error && <p className="error">Lỗi: {error}</p>}

      <div className="profile-head">
        <div className="profile-avatar">
          {profile.avatarUrl
            ? <img src={profile.avatarUrl} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
            : <span>{profile.username.charAt(0).toUpperCase()}</span>}
        </div>
        <div>
          <strong>{profile.displayName || profile.username}</strong>
          <p className="muted small">{profile.email}</p>
          <p className="muted small">
            Vai trò: {profile.role === 'ADMIN' ? 'Quản trị viên' : 'Thành viên'} ·
            Tham gia {formatDateTime(profile.createdAt)}
          </p>
        </div>
      </div>

      <form onSubmit={handleSaveProfile} className="form">
        <h2>Thông tin hiển thị</h2>
        <label>
          Tên đăng nhập
          <input value={profile.username} disabled />
        </label>
        <label>
          Tên hiển thị
          <input
            value={form.displayName}
            maxLength={100}
            onChange={(e) => setForm({ ...form, displayName: e.target.value })}
            placeholder="Tên bạn muốn hiển thị"
          />
        </label>
        <label>
          Ảnh đại diện (URL)
          <input
            value={form.avatarUrl}
            maxLength={500}
            onChange={(e) => setForm({ ...form, avatarUrl: e.target.value })}
            placeholder="https://..."
          />
        </label>
        <button type="submit" className="btn-primary" disabled={saving}>Lưu thay đổi</button>
      </form>

      <form onSubmit={handleChangePassword} className="form" style={{ marginTop: '2.5rem' }}>
        <h2>Đổi mật khẩu</h2>
        <label>
          Mật khẩu hiện tại
          <input
            type="password"
            required
            autoComplete="current-password"
            value={passwordForm.currentPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
          />
        </label>
        <label>
          Mật khẩu mới
          <input
            type="password"
            required
            minLength={6}
            autoComplete="new-password"
            value={passwordForm.newPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
          />
        </label>
        <label>
          Nhập lại mật khẩu mới
          <input
            type="password"
            required
            minLength={6}
            autoComplete="new-password"
            value={passwordForm.confirm}
            onChange={(e) => setPasswordForm({ ...passwordForm, confirm: e.target.value })}
          />
        </label>
        <button type="submit" className="btn-primary" disabled={saving}>Đổi mật khẩu</button>
      </form>
    </div>
  );
}
