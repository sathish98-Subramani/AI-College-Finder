import { Link } from 'react-router-dom'
import { MapPin, Star, Users, Heart } from 'lucide-react'

export default function CollegeCard({ college, onToggleFavorite, isFavorite, selectable, selected, onSelect }) {
  return (
    <div className="group relative flex flex-col rounded-2xl border border-line bg-white p-5 transition hover:border-gold/60 hover:shadow-[0_8px_24px_-12px_rgba(20,33,61,0.25)]">
      {selectable && (
        <label className="absolute right-4 top-4 flex items-center gap-2 text-xs text-muted">
          <input
            type="checkbox"
            checked={selected}
            onChange={(e) => onSelect(college.id, e.target.checked)}
            className="h-4 w-4 accent-gold"
          />
          Compare
        </label>
      )}

      {onToggleFavorite && !selectable && (
        <button
          onClick={() => onToggleFavorite(college.id)}
          className="absolute right-4 top-4 text-muted transition hover:text-danger"
          aria-label="Toggle favorite"
        >
          <Heart className={`h-5 w-5 ${isFavorite ? 'fill-danger text-danger' : ''}`} />
        </button>
      )}

      <p className="pr-8 font-display text-lg font-semibold leading-snug text-ink">
        {college.collegeName}
      </p>

      <p className="mt-1 flex items-center gap-1 text-sm text-muted">
        <MapPin className="h-3.5 w-3.5" />
        {college.city}, {college.state}
        {college.institutionType && <span className="ml-1 text-xs">· {college.institutionType}</span>}
      </p>

      <div className="mt-4 grid grid-cols-2 gap-3 text-sm">
        <div>
          <p className="text-muted">Annual fee</p>
          <p className="font-medium text-ink">
            {college.annualFeeLakh != null ? `₹${college.annualFeeLakh}L` : 'N/A'}
          </p>
        </div>
        <div>
          <p className="text-muted">Placement</p>
          <p className="font-medium text-ink">
            {college.placementRatePct != null ? `${college.placementRatePct}%` : 'N/A'}
          </p>
        </div>
        <div>
          <p className="text-muted">NIRF rank</p>
          <p className="font-medium text-ink">{college.nirfRank ?? 'N/A'}</p>
        </div>
        <div className="flex items-center gap-1">
          <Star className="h-3.5 w-3.5 fill-gold text-gold" />
          <p className="font-medium text-ink">{college.rating ?? 'N/A'} / 5</p>
        </div>
      </div>

      <div className="mt-5 flex items-center justify-between">
        {college.hostel ? (
          <span className="flex items-center gap-1 rounded-full bg-teal/10 px-2.5 py-1 text-xs font-medium text-teal">
            <Users className="h-3 w-3" /> Hostel available
          </span>
        ) : <span />}
        <Link
          to={`/college/${college.id}`}
          className="text-sm font-medium text-ink underline decoration-gold decoration-2 underline-offset-4 hover:text-gold"
        >
          View details
        </Link>
      </div>
    </div>
  )
}
