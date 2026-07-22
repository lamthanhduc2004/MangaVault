import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import StoryListPage from '../pages/StoryListPage';
import StoryDetailPage from '../pages/StoryDetailPage';
import StoryCreatePage from '../pages/StoryCreatePage';
import ChapterReaderPage from '../pages/ChapterReaderPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<StoryListPage />} />
        <Route path="/stories/new" element={<StoryCreatePage />} />
        <Route path="/stories/:id" element={<StoryDetailPage />} />
        <Route path="/chapters/:id" element={<ChapterReaderPage />} />
      </Route>
    </Routes>
  );
}
