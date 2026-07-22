import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge';

export default function StoryCard({ story }) {
  return (
    <Link to={`/stories/${story.id}`} className="card">
      <div className="card-cover">
        {story.coverUrl
          ? <img src={story.coverUrl} alt={story.title} />
          : <span className="cover-placeholder">📖</span>}
      </div>
      <div className="card-body">
        <h3 className="card-title">{story.title}</h3>
        {story.author && <p className="muted small">{story.author}</p>}
        <StatusBadge status={story.status} />
        {story.description && <p className="card-desc">{story.description}</p>}
      </div>
    </Link>
  );
}
