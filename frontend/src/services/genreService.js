import api from './api';

export async function getGenres() {
  const res = await api.get('/genres');
  return res.data.result;
}

// Admin genre CRUD
export async function createGenre(payload) {
  const res = await api.post('/admin/genres', payload);
  return res.data.result;
}

export async function updateGenre(id, payload) {
  const res = await api.put(`/admin/genres/${id}`, payload);
  return res.data.result;
}

export async function deleteGenre(id) {
  const res = await api.delete(`/admin/genres/${id}`);
  return res.data;
}
