import api from './api';

// Admin CRUD calls — backend URLs live under /api/admin/** so Spring Security
// can protect them later without changing the frontend.

export async function createStory(payload) {
  const res = await api.post('/admin/stories', payload);
  return res.data.result;
}

export async function updateStory(id, payload) {
  const res = await api.put(`/admin/stories/${id}`, payload);
  return res.data.result;
}

export async function deleteStory(id) {
  const res = await api.delete(`/admin/stories/${id}`);
  return res.data;
}

export async function createChapter(storyId, payload) {
  const res = await api.post(`/admin/stories/${storyId}/chapters`, payload);
  return res.data.result;
}

export async function updateChapter(id, payload) {
  const res = await api.put(`/admin/chapters/${id}`, payload);
  return res.data.result;
}

export async function deleteChapter(id) {
  const res = await api.delete(`/admin/chapters/${id}`);
  return res.data;
}
