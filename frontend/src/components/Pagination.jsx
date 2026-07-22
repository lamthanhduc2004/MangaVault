export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;
  return (
    <div className="pagination">
      <button disabled={page === 0} onClick={() => onChange(page - 1)}>← Trước</button>
      <span>Trang {page + 1} / {totalPages}</span>
      <button disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>Sau →</button>
    </div>
  );
}
