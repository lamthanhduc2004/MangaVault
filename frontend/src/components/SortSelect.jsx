const SORT_OPTIONS = [
  { value: 'latest', label: 'Mới đăng' },
  { value: 'updated', label: 'Mới cập nhật' },
  { value: 'views', label: 'Lượt xem' },
  { value: 'rating', label: 'Đánh giá' },
  { value: 'title', label: 'Tên truyện (A→Z)' },
];

export default function SortSelect({ value, onChange }) {
  return (
    <select
      className="status-filter"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      aria-label="Sắp xếp"
    >
      {SORT_OPTIONS.map((o) => (
        <option key={o.value} value={o.value}>Sắp xếp: {o.label}</option>
      ))}
    </select>
  );
}
