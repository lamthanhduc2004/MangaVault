import api from './api';

export async function getComments(storyId, { page = 0, size = 10 } = {}) {
  const res = await api.get(`/stories/${storyId}/comments`, { params: { page, size } });
  return res.data.result;
}

export async function createComment(storyId, content) {
  const res = await api.post(`/stories/${storyId}/comments`, { content });
  return res.data.result;
}

export async function updateComment(id, content) {
  const res = await api.put(`/comments/${id}`, { content });
  return res.data.result;
}

export async function deleteComment(id) {
  const res = await api.delete(`/comments/${id}`);
  return res.data;
}

export async function reportComment(id, reason) {
  const res = await api.post(`/comments/${id}/report`, { reason });
  return res.data;
}

// Admin moderation (F20)
export async function getReportedComments({ page = 0, size = 10 } = {}) {
  const res = await api.get('/admin/comments/reported', { params: { page, size } });
  return res.data.result;
}

export async function getAdminComments({ keyword = '', reportedOnly = false, hidden = '', page = 0, size = 10 } = {}) {
  const res = await api.get('/admin/comments', {
    params: {
      keyword: keyword || undefined,
      reportedOnly,
      hidden: hidden === '' ? undefined : hidden,
      page,
      size,
    },
  });
  return res.data.result;
}

export async function setCommentHidden(id, hidden) {
  const res = await api.patch(`/admin/comments/${id}/visibility`, { hidden });
  return res.data.result;
}

export async function dismissReports(id) {
  const res = await api.post(`/admin/comments/${id}/dismiss-reports`);
  return res.data;
}

export async function deleteCommentAsAdmin(id) {
  const res = await api.delete(`/admin/comments/${id}`);
  return res.data;
}

export async function getStats() {
  const res = await api.get('/admin/stats');
  return res.data.result;
}
