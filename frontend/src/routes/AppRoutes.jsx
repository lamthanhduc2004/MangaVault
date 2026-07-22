import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import HomePage from '../pages/HomePage';
import StoryListPage from '../pages/StoryListPage';
import StoryDetailPage from '../pages/StoryDetailPage';
import ChapterReaderPage from '../pages/ChapterReaderPage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import RequireAdmin from './RequireAdmin';
import AdminStoryListPage from '../pages/admin/AdminStoryListPage';
import AdminStoryFormPage from '../pages/admin/AdminStoryFormPage';
import AdminChapterListPage from '../pages/admin/AdminChapterListPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/stories" element={<StoryListPage />} />
        <Route path="/stories/:id" element={<StoryDetailPage />} />
        <Route path="/chapters/:id" element={<ChapterReaderPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<RequireAdmin />}>
          <Route path="/admin" element={<AdminStoryListPage />} />
          <Route path="/admin/stories/new" element={<AdminStoryFormPage />} />
          <Route path="/admin/stories/:id/edit" element={<AdminStoryFormPage />} />
          <Route path="/admin/stories/:id/chapters" element={<AdminChapterListPage />} />
        </Route>
      </Route>
    </Routes>
  );
}
