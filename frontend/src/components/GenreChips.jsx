import { Link } from 'react-router-dom';

export default function GenreChips({ genres, linkable = true }) {
  if (!genres || genres.length === 0) return null;

  return (
    <div className="genre-chips">
      {genres.map((genre) => (
        linkable
          ? <Link key={genre.id} to={`/stories?genre=${genre.slug}`} className="chip">{genre.name}</Link>
          : <span key={genre.id} className="chip">{genre.name}</span>
      ))}
    </div>
  );
}
