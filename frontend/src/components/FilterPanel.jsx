const INSTITUTION_TYPES = ['IIT', 'NIT', 'IIIT', 'Deemed', 'Private', 'Autonomous', 'State University', 'Central University']

export default function FilterPanel({ filters, onChange, onReset }) {
  const set = (key, value) => onChange({ ...filters, [key]: value })

  return (
    <div className="rounded-2xl border border-line bg-white p-5">
      <div className="flex items-center justify-between">
        <p className="font-display text-base font-semibold text-ink">Filters</p>
        <button onClick={onReset} className="text-xs font-medium text-muted hover:text-gold">
          Reset
        </button>
      </div>

      <div className="mt-4 space-y-4 text-sm">
        <div>
          <label className="mb-1 block text-xs font-medium text-muted">State</label>
          <input
            value={filters.state || ''}
            onChange={(e) => set('state', e.target.value)}
            placeholder="e.g. Tamil Nadu"
            className="w-full rounded-lg border border-line px-3 py-2 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">City</label>
          <input
            value={filters.city || ''}
            onChange={(e) => set('city', e.target.value)}
            placeholder="e.g. Chennai"
            className="w-full rounded-lg border border-line px-3 py-2 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Course</label>
          <input
            value={filters.course || ''}
            onChange={(e) => set('course', e.target.value)}
            placeholder="e.g. CSE"
            className="w-full rounded-lg border border-line px-3 py-2 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Institution type</label>
          <select
            value={filters.institutionType || ''}
            onChange={(e) => set('institutionType', e.target.value)}
            className="w-full rounded-lg border border-line bg-white px-3 py-2 outline-none focus:border-gold"
          >
            <option value="">Any</option>
            {INSTITUTION_TYPES.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">
            Max annual fee: {filters.maxFeeLakh ? `₹${filters.maxFeeLakh}L` : 'Any'}
          </label>
          <input
            type="range"
            min="0"
            max="10"
            step="0.5"
            value={filters.maxFeeLakh || 10}
            onChange={(e) => set('maxFeeLakh', e.target.value)}
            className="w-full accent-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">
            Minimum rating: {filters.minRating || 'Any'}
          </label>
          <input
            type="range"
            min="0"
            max="5"
            step="0.5"
            value={filters.minRating || 0}
            onChange={(e) => set('minRating', e.target.value)}
            className="w-full accent-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">
            Minimum placement rate: {filters.minPlacementPct ? `${filters.minPlacementPct}%` : 'Any'}
          </label>
          <input
            type="range"
            min="0"
            max="100"
            step="5"
            value={filters.minPlacementPct || 0}
            onChange={(e) => set('minPlacementPct', e.target.value)}
            className="w-full accent-gold"
          />
        </div>

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={!!filters.hostel}
            onChange={(e) => set('hostel', e.target.checked)}
            className="h-4 w-4 accent-gold"
          />
          Hostel available
        </label>
      </div>
    </div>
  )
}
