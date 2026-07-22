const LABELS = {
  ONGOING: { text: 'Đang ra', className: 'badge badge-ongoing' },
  COMPLETED: { text: 'Hoàn thành', className: 'badge badge-completed' },
  HIATUS: { text: 'Tạm ngưng', className: 'badge badge-hiatus' },
};

export default function StatusBadge({ status }) {
  const s = LABELS[status] || { text: status, className: 'badge' };
  return <span className={s.className}>{s.text}</span>;
}
