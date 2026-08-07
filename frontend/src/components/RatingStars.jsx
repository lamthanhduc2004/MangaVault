import { useState } from 'react';

const STARS = [1, 2, 3, 4, 5];

/**
 * Star widget. In readOnly mode it renders the average; otherwise clicking a star
 * submits that score and hovering previews it.
 */
export default function RatingStars({ value = 0, readOnly = false, onRate }) {
  const [hover, setHover] = useState(0);
  const shown = hover || value;

  return (
    <span className={`stars${readOnly ? ' stars-readonly' : ''}`}>
      {STARS.map((star) => (
        <button
          key={star}
          type="button"
          className="star"
          disabled={readOnly}
          aria-label={`${star} sao`}
          onMouseEnter={() => !readOnly && setHover(star)}
          onMouseLeave={() => !readOnly && setHover(0)}
          onClick={() => !readOnly && onRate?.(star)}
        >
          {star <= Math.round(shown) ? '★' : '☆'}
        </button>
      ))}
    </span>
  );
}
