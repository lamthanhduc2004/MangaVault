import api from './api';

// All story/chapter API calls are centralized here (see AGENTS.md convention).
// Backend envelope: { code, result, message } — callers receive `result`.

export async function getStories({ keyword = '', page = 0, size = 12 } = {}) {
  const res = await api.get('/stories', { params: { keyword: keyword || undefined, page, size } });
  return res.data.result; // PageResponse: { items, page, size, totalElements, totalPages, last }
}

export async function getStoryById(id) {
  const res = await api.get(`/stories/${id}`);
  return res.data.result;
}

export async function getChaptersOfStory(storyId) {
  const res = await api.get(`/stories/${storyId}/chapters`);
  return res.data.result; // ChapterSummary[] — no content
}

export async function getChapterById(id) {
  const res = await api.get(`/chapters/${id}`);
  return res.data.result; // full chapter with content + prev/next ids
}
