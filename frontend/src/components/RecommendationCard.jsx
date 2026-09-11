import { Link } from 'react-router-dom'

export default function RecommendationCard({ rec }) {
  const score = Number(rec.score)
  const ring = score >= 80 ? 'text-teal' : score >= 60 ? 'text-gold' : 'text-muted'

  return (
    <div className="rounded-2xl border border-line bg-white p-5">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="font-display text-lg font-semibold text-ink">{rec.collegeName}</p>
          {rec.college && (
            <p className="text-sm text-muted">{rec.college.city}, {rec.college.state}</p>
          )}
        </div>
        <div className={`flex h-14 w-14 shrink-0 items-center justify-center rounded-full border-2 border-current ${ring}`}>
          <span className="text-sm font-semibold">{score}%</span>
        </div>
      </div>

      <p className="mt-3 text-sm text-muted">{rec.reason}</p>

      {rec.matchedCriteria && (
        <p className="mt-3 text-xs text-teal">✓ {rec.matchedCriteria}</p>
      )}
      {rec.unmatchedCriteria && (
        <p className="mt-1 text-xs text-danger">△ {rec.unmatchedCriteria}</p>
      )}

      <Link
        to={`/college/${rec.collegeId}`}
        className="mt-4 inline-block text-sm font-medium text-ink underline decoration-gold decoration-2 underline-offset-4 hover:text-gold"
      >
        View college
      </Link>
    </div>
  )
}
