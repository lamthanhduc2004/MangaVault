import api from './api';

// Admin calls live under /api/admin/** — Spring Security restricts that prefix
// to ROLE_ADMIN. The admin listing is separate from the public one because the
// public catalogue hides PRIVATE stories.

export async function getAdminStories({ keyword = '', status = '', visibility = '', sort = 'latest', page = 0, size = 10 } = {}) {
  const res = await api.get('/admin/stories', {
    params: {
      keyword: keyword || undefined,
      status: status || undefined,
      visibility: visibility || undefined,
      sort,
      page,
      size,
    },
  });
  return res.data.result;
}

export async function getAdminStoryById(id) {
  const res = await api.get(`/admin/stories/${id}`);
  return res.data.result;
}

export async function getAdminChapters(storyId) {
  const res = await api.get(`/admin/stories/${storyId}/chapters`);
  return res.data.result;
}

// Separate from the public reader endpoint: returns hidden chapters and does not
// count a view when the admin opens one for editing.
export async function getAdminChapterById(id) {
  const res = await api.get(`/admin/chapters/${id}`);
  return res.data.result;
}

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

export async function setChapterPublished(id, published) {
  const res = await api.patch(`/admin/chapters/${id}/publish`, { published });
  return res.data.result;
}

// Swaps chapterNumber with the neighbor above/below (F17 reorder).
export async function moveChapter(id, direction) {
  const res = await api.patch(`/admin/chapters/${id}/move`, { direction });
  return res.data;
}

export async function deleteChapter(id) {
  const res = await api.delete(`/admin/chapters/${id}`);
  return res.data;
}
