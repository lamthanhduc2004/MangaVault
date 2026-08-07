import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import HomePage from '../pages/HomePage';
import StoryListPage from '../pages/StoryListPage';
import StoryDetailPage from '../pages/StoryDetailPage';
import ChapterReaderPage from '../pages/ChapterReaderPage';
import NotFoundPage from '../pages/NotFoundPage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import ProfilePage from '../pages/ProfilePage';
import FollowingPage from '../pages/FollowingPage';
import HistoryPage from '../pages/HistoryPage';
import RequireAuth from './RequireAuth';
import RequireAdmin from './RequireAdmin';
import AdminStoryListPage from '../pages/admin/AdminStoryListPage';
import AdminStoryFormPage from '../pages/admin/AdminStoryFormPage';
import AdminChapterListPage from '../pages/admin/AdminChapterListPage';
import AdminGenreListPage from '../pages/admin/AdminGenreListPage';
import AdminUserListPage from '../pages/admin/AdminUserListPage';
import AdminReportedCommentPage from '../pages/admin/AdminReportedCommentPage';
import AdminDashboardPage from '../pages/admin/AdminDashboardPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        {/* Public */}
        <Route path="/" element={<HomePage />} />
        <Route path="/stories" element={<StoryListPage />} />
        <Route path="/stories/:id" element={<StoryDetailPage />} />
        <Route path="/chapters/:id" element={<ChapterReaderPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Signed-in readers */}
        <Route element={<RequireAuth />}>
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/following" element={<FollowingPage />} />
          <Route path="/history" element={<HistoryPage />} />
        </Route>

        {/* Admin — the backend enforces the same boundary on /api/admin/** */}
        <Route element={<RequireAdmin />}>
          <Route path="/admin" element={<AdminStoryListPage />} />
          <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
          <Route path="/admin/stories/new" element={<AdminStoryFormPage />} />
          <Route path="/admin/stories/:id/edit" element={<AdminStoryFormPage />} />
          <Route path="/admin/stories/:id/chapters" element={<AdminChapterListPage />} />
          <Route path="/admin/genres" element={<AdminGenreListPage />} />
          <Route path="/admin/users" element={<AdminUserListPage />} />
          <Route path="/admin/comments/reported" element={<AdminReportedCommentPage />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
