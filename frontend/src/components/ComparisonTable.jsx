const ROWS = [
  { label: 'Location', key: (c) => `${c.city}, ${c.state}` },
  { label: 'Institution type', key: (c) => c.institutionType || 'N/A' },
  { label: 'Annual fee', key: (c) => (c.annualFeeLakh != null ? `₹${c.annualFeeLakh}L` : 'N/A'), best: 'min', metric: 'annualFeeLakh' },
  { label: 'Cutoff exam', key: (c) => c.cutoffExam || 'N/A' },
  { label: 'Rating', key: (c) => (c.rating != null ? `${c.rating} / 5` : 'N/A'), best: 'max', metric: 'rating' },
  { label: 'Placement rate', key: (c) => (c.placementRatePct != null ? `${c.placementRatePct}%` : 'N/A'), best: 'max', metric: 'placementRatePct' },
  { label: 'Average package', key: (c) => (c.averagePackageLpa != null ? `₹${c.averagePackageLpa} LPA` : 'N/A'), best: 'max', metric: 'averagePackageLpa' },
  { label: 'Highest package', key: (c) => c.highestPackageApprox || 'N/A' },
  { label: 'Hostel', key: (c) => (c.hostel ? 'Yes' : 'No') },
  { label: 'NIRF rank', key: (c) => c.nirfRank ?? 'N/A', best: 'min', metric: 'nirfRank' },
]

export default function ComparisonTable({ colleges }) {
  const bestIndexFor = (row) => {
    if (!row.metric) return -1
    const values = colleges.map((c) => c[row.metric])
    const valid = values.filter((v) => v != null)
    if (!valid.length) return -1
    const target = row.best === 'min' ? Math.min(...valid) : Math.max(...valid)
    return values.indexOf(target)
  }

  return (
    <div className="overflow-x-auto rounded-2xl border border-line bg-white">
      <table className="w-full min-w-[640px] border-collapse text-sm">
        <thead>
          <tr className="border-b border-line bg-paper-2/50">
            <th className="w-40 p-4 text-left font-medium text-muted">Attribute</th>
            {colleges.map((c) => (
              <th key={c.id} className="p-4 text-left font-display font-semibold text-ink">
                {c.collegeName}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {ROWS.map((row) => {
            const bestIdx = bestIndexFor(row)
            return (
              <tr key={row.label} className="border-b border-line last:border-0">
                <td className="p-4 text-muted">{row.label}</td>
                {colleges.map((c, idx) => (
                  <td
                    key={c.id}
                    className={`p-4 ${idx === bestIdx ? 'font-semibold text-teal' : 'text-ink'}`}
                  >
                    {row.key(c)}
                    {idx === bestIdx && <span className="ml-1 text-xs">★</span>}
                  </td>
                ))}
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
