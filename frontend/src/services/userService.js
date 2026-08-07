import api from './api';

export async function getMe() {
  const res = await api.get('/me');
  return res.data.result;
}

export async function updateProfile(payload) {
  const res = await api.put('/me', payload);
  return res.data.result;
}

export async function changePassword(payload) {
  const res = await api.put('/me/password', payload);
  return res.data;
}

// Admin user management
export async function getUsers({ keyword = '', role = '', status = '', page = 0, size = 10 } = {}) {
  const res = await api.get('/admin/users', {
    params: {
      keyword: keyword || undefined,
      role: role || undefined,
      status: status || undefined,
      page,
      size,
    },
  });
  return res.data.result;
}

export async function setUserStatus(id, status) {
  const res = await api.patch(`/admin/users/${id}/status`, { status });
  return res.data.result;
}

export async function setUserRole(id, role) {
  const res = await api.patch(`/admin/users/${id}/role`, { role });
  return res.data.result;
}
