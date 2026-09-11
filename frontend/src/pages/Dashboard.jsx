import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { favoriteService } from '../services/favoriteService'
import { recommendationService } from '../services/recommendationService'
import { searchHistoryService } from '../services/searchHistoryService'
import LoadingSpinner from '../components/LoadingSpinner'

export default function Dashboard() {
  const { user } = useAuth()
  const [favorites, setFavorites] = useState([])
  const [recommendations, setRecommendations] = useState([])
  const [recentSearches, setRecentSearches] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      favoriteService.list().catch(() => []),
      recommendationService.mine().catch(() => []),
      searchHistoryService.recent(5).catch(() => []),
    ]).then(([favs, recs, history]) => {
      setFavorites(favs)
      setRecommendations(recs)
      setRecentSearches(history)
    }).finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner label="Loading your dashboard" />

  return (
    <div className="mx-auto max-w-6xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Welcome back, {user?.fullName?.split(' ')[0]}</h1>
      <p className="mt-2 text-sm text-muted">Here's where things stand with your college search.</p>

      <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard label="Saved colleges" value={favorites.length} to="/favorites" />
        <SummaryCard label="Recommendations" value={recommendations.length} to="/recommendations" />
        <SummaryCard label="Recent searches" value={recentSearches.length} to="/colleges" />
        <SummaryCard label="Profile" value={user?.preferredCourse ? 'Set up' : 'Incomplete'} to="/profile" />
      </div>

      <div className="mt-10 grid gap-8 lg:grid-cols-2">
        <div>
          <div className="flex items-center justify-between">
            <p className="font-display text-lg font-semibold text-ink">Recent recommendations</p>
            <Link to="/recommendations" className="text-xs font-medium text-ink underline decoration-gold underline-offset-4">View all</Link>
          </div>
          {recommendations.length === 0 ? (
            <p className="mt-3 text-sm text-muted">You haven't generated any recommendations yet.</p>
          ) : (
            <ul className="mt-3 space-y-2">
              {recommendations.slice(0, 5).map((r) => (
                <li key={r.collegeId} className="flex items-center justify-between rounded-xl border border-line bg-white px-4 py-3 text-sm">
                  <span>{r.collegeName}</span>
                  <span className="font-medium text-gold">{Number(r.score)}%</span>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div>
          <div className="flex items-center justify-between">
            <p className="font-display text-lg font-semibold text-ink">Saved colleges</p>
            <Link to="/favorites" className="text-xs font-medium text-ink underline decoration-gold underline-offset-4">View all</Link>
          </div>
          {favorites.length === 0 ? (
            <p className="mt-3 text-sm text-muted">No colleges saved yet.</p>
          ) : (
            <ul className="mt-3 space-y-2">
              {favorites.slice(0, 5).map((c) => (
                <li key={c.id} className="rounded-xl border border-line bg-white px-4 py-3 text-sm">
                  {c.collegeName} <span className="text-muted">— {c.city}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  )
}

function SummaryCard({ label, value, to }) {
  return (
    <Link to={to} className="rounded-2xl border border-line bg-white p-5 transition hover:border-gold/60">
      <p className="text-xs text-muted">{label}</p>
      <p className="mt-1 font-display text-2xl font-semibold text-ink">{value}</p>
    </Link>
  )
}
