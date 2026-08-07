export default function GenreFilter({ genres, value, onChange }) {
  return (
    <select
      className="status-filter"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      aria-label="Lọc theo thể loại"
    >
      <option value="">Tất cả thể loại</option>
      {genres.map((genre) => (
        <option key={genre.id} value={genre.slug}>{genre.name}</option>
      ))}
    </select>
  );
}
