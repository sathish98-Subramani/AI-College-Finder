import { useEffect, useState } from 'react'
import { favoriteService } from '../services/favoriteService'
import CollegeCard from '../components/CollegeCard'
import LoadingSpinner from '../components/LoadingSpinner'

export default function Favorites() {
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    favoriteService.list().then(setFavorites).catch(() => {}).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const remove = async (id) => {
    await favoriteService.remove(id)
    setFavorites((f) => f.filter((c) => c.id !== id))
  }

  return (
    <div className="mx-auto max-w-6xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Your saved colleges</h1>
      <p className="mt-2 text-sm text-muted">Colleges you've bookmarked for later.</p>

      {loading ? (
        <LoadingSpinner />
      ) : favorites.length === 0 ? (
        <p className="mt-10 text-sm text-muted">You haven't saved any colleges yet. Browse colleges and tap the heart icon to save one.</p>
      ) : (
        <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {favorites.map((c) => (
            <CollegeCard key={c.id} college={c} onToggleFavorite={remove} isFavorite />
          ))}
        </div>
      )}
    </div>
  )
}
