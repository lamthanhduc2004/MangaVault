import api from './api';

// Follows (F11)
export async function followStory(storyId) {
  const res = await api.post(`/stories/${storyId}/follow`);
  return res.data;
}

export async function unfollowStory(storyId) {
  const res = await api.delete(`/stories/${storyId}/follow`);
  return res.data;
}

export async function isFollowing(storyId) {
  const res = await api.get(`/stories/${storyId}/follow`);
  return res.data.result.following;
}

export async function getFollows({ page = 0, size = 12 } = {}) {
  const res = await api.get('/me/follows', { params: { page, size } });
  return res.data.result;
}

// Reading history (F12) and resume position (F13)
export async function getHistory({ page = 0, size = 12 } = {}) {
  const res = await api.get('/me/history', { params: { page, size } });
  return res.data.result;
}

export async function saveProgress(chapterId) {
  const res = await api.post('/me/history', { chapterId });
  return res.data;
}

export async function getProgress(storyId) {
  const res = await api.get(`/me/history/${storyId}`);
  return res.data.result; // null when the story has never been opened
}

export async function deleteHistoryEntry(storyId) {
  const res = await api.delete(`/me/history/${storyId}`);
  return res.data;
}

// Ratings (F15)
export async function getRatingSummary(storyId) {
  const res = await api.get(`/stories/${storyId}/rating`);
  return res.data.result;
}

export async function rateStory(storyId, score) {
  const res = await api.put(`/stories/${storyId}/rating`, { score });
  return res.data.result;
}

export async function removeRating(storyId) {
  const res = await api.delete(`/stories/${storyId}/rating`);
  return res.data.result;
}
