import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import StoryListPage from '../pages/StoryListPage';
import StoryDetailPage from '../pages/StoryDetailPage';
import ChapterReaderPage from '../pages/ChapterReaderPage';
import AdminStoryListPage from '../pages/admin/AdminStoryListPage';
import AdminStoryFormPage from '../pages/admin/AdminStoryFormPage';
import AdminChapterListPage from '../pages/admin/AdminChapterListPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<StoryListPage />} />
        <Route path="/stories/:id" element={<StoryDetailPage />} />
        <Route path="/chapters/:id" element={<ChapterReaderPage />} />
        {/* Admin routes — will be protected by auth in a later phase. */}
        <Route path="/admin" element={<AdminStoryListPage />} />
        <Route path="/admin/stories/new" element={<AdminStoryFormPage />} />
        <Route path="/admin/stories/:id/edit" element={<AdminStoryFormPage />} />
        <Route path="/admin/stories/:id/chapters" element={<AdminChapterListPage />} />
      </Route>
    </Routes>
  );
}
