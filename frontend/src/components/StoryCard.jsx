import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import { formatCount, formatRating } from '../utils/format';

export default function StoryCard({ story }) {
  return (
    <Link to={`/stories/${story.id}`} className="card">
      <div className="card-cover">
        {story.coverUrl
          ? (
            <img
              src={story.coverUrl}
              alt={story.title}
              loading="lazy"
              // A broken cover URL would otherwise render as a broken-image icon.
              onError={(e) => { e.currentTarget.style.display = 'none'; }}
            />
          )
          : <span className="cover-placeholder">📖</span>}
      </div>
      <div className="card-body">
        <h3 className="card-title">{story.title}</h3>
        {story.author && <p className="muted small">{story.author}</p>}
        <div className="card-meta">
          <StatusBadge status={story.status} />
          <span className="muted small" title="Lượt xem">👁 {formatCount(story.viewCount)}</span>
          {story.ratingCount > 0 && (
            <span className="muted small" title={`${story.ratingCount} lượt đánh giá`}>
              ⭐ {formatRating(story.ratingAvg)}
            </span>
          )}
        </div>
        {story.description && <p className="card-desc">{story.description}</p>}
      </div>
    </Link>
  );
}
