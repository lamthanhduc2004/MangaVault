import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="empty-page">
      <p className="empty-icon">🔍</p>
      <h1>Không tìm thấy trang</h1>
      <p className="muted">Đường dẫn bạn truy cập không tồn tại hoặc nội dung đã bị gỡ.</p>
      <div className="empty-actions">
        <Link to="/" className="btn-primary">Về trang chủ</Link>
        <Link to="/stories" className="btn-small">Xem danh sách truyện</Link>
      </div>
    </div>
  );
}
