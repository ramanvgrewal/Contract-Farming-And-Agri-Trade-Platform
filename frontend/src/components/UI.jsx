export function StatCard({ label, value, helper }) {
  return (
    <div className="card stat-card">
      <div className="stat-label">{label}</div>
      <div className="stat-value">{value}</div>
      {helper && <div className="stat-helper">{helper}</div>}
    </div>
  );
}

export function InfoRow({ title, value }) {
  return (
    <div className="info-row">
      <span>{title}</span>
      <strong>{value}</strong>
    </div>
  );
}

export function Tag({ children, tone = "earth" }) {
  return <span className={`tag tag-${tone}`}>{children}</span>;
}

export function EmptyState({ title, body, action }) {
  return (
    <div className="empty-state">
      <div>
        <h3>{title}</h3>
        <p>{body}</p>
      </div>
      {action}
    </div>
  );
}

export function Spinner({ text }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '16px', color: 'var(--muted)' }}>
      <span className="loader"></span>
      {text && <span>{text}</span>}
    </div>
  );
}
