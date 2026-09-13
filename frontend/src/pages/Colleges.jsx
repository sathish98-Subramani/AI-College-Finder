import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { Map as MapIcon, LayoutGrid } from 'lucide-react'
import { collegeService } from '../services/collegeService'
import { favoriteService } from '../services/favoriteService'
import { useAuth } from '../context/AuthContext'
import SearchBar from '../components/SearchBar'
import FilterPanel from '../components/FilterPanel'
import CollegeCard from '../components/CollegeCard'
import MapView from '../components/MapView'
import LoadingSpinner from '../components/LoadingSpinner'

export default function Colleges() {
  const { user } = useAuth()

  const [query, setQuery] = useState('')
  const [filters, setFilters] = useState({})
  const [page, setPage] = useState(0)

  const [result, setResult] = useState({
    content: [],
    totalPages: 0,
    totalElements: 0,
  })

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const [view, setView] = useState('grid')
  const [selectedIds, setSelectedIds] = useState([])
  const [favoriteIds, setFavoriteIds] = useState([])

  const load = useCallback(() => {
    setLoading(true)
    setError(false)

    collegeService
      .search({
        q: query || undefined,
        state: filters.state || undefined,
        city: filters.city || undefined,
        course: filters.course || undefined,
        institutionType: filters.institutionType || undefined,
        minRating: filters.minRating || undefined,
        maxFeeLakh: filters.maxFeeLakh || undefined,
        minPlacementPct: filters.minPlacementPct || undefined,
        hostel: filters.hostel || undefined,
        page,
        size: 12,
      })
      .then((res) => {
        setResult({
          content: res?.content || [],
          totalPages: res?.totalPages || 0,
          totalElements: res?.totalElements || 0,
        })

        setError(false)
      })
      .catch(() => {
        setError(true)

        setResult({
          content: [],
          totalPages: 0,
          totalElements: 0,
        })
      })
      .finally(() => {
        setLoading(false)
      })
  }, [query, filters, page])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (!user) {
      setFavoriteIds([])
      return
    }

    favoriteService
      .list()
      .then((favs) => {
        setFavoriteIds(favs.map((f) => f.id))
      })
      .catch(() => {
        setFavoriteIds([])
      })
  }, [user])

  const toggleFavorite = async (collegeId) => {
    if (!user) return

    try {
      if (favoriteIds.includes(collegeId)) {
        await favoriteService.remove(collegeId)

        setFavoriteIds((ids) =>
          ids.filter((id) => id !== collegeId)
        )
      } else {
        await favoriteService.add(collegeId)

        setFavoriteIds((ids) => [...ids, collegeId])
      }
    } catch {
      // Keep current UI state if favorite request fails
    }
  }

  const toggleSelect = (id, checked) => {
    setSelectedIds((ids) =>
      checked
        ? ids.includes(id)
          ? ids
          : [...ids, id]
        : ids.filter((i) => i !== id)
    )
  }

  const handleSearchSubmit = () => {
    setPage(0)
    load()
  }

  const handleRetry = () => {
    load()
  }

  return (
    <div className="mx-auto max-w-6xl px-5 py-12">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="font-display text-3xl font-semibold text-ink">
            Explore colleges
          </h1>

          <p className="mt-1 text-sm text-muted">
            {loading
              ? 'Loading colleges...'
              : `${result.totalElements} colleges match your current filters`}
          </p>
        </div>

        {/* View Toggle */}
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => setView('grid')}
            className={`flex items-center gap-1.5 rounded-full border px-4 py-2 text-sm ${
              view === 'grid'
                ? 'border-ink bg-ink text-paper'
                : 'border-line text-muted'
            }`}
          >
            <LayoutGrid className="h-4 w-4" />
            Grid
          </button>

          <button
            type="button"
            onClick={() => setView('map')}
            className={`flex items-center gap-1.5 rounded-full border px-4 py-2 text-sm ${
              view === 'map'
                ? 'border-ink bg-ink text-paper'
                : 'border-line text-muted'
            }`}
          >
            <MapIcon className="h-4 w-4" />
            Map
          </button>
        </div>
      </div>

      {/* Search */}
      <div className="mt-6">
        <SearchBar
          value={query}
          onChange={setQuery}
          onSubmit={handleSearchSubmit}
        />
      </div>

      {/* Compare Selection */}
      {selectedIds.length > 0 && (
        <div className="mt-4 flex items-center justify-between rounded-xl border border-gold/40 bg-gold/10 px-4 py-3 text-sm">
          <span>
            {selectedIds.length} selected for comparison
          </span>

          <Link
            to={`/compare?ids=${selectedIds.join(',')}`}
            className="rounded-full bg-ink px-4 py-1.5 text-xs font-medium text-paper"
          >
            Compare now
          </Link>
        </div>
      )}

      {/* Main Content */}
      <div className="mt-8 grid gap-8 md:grid-cols-[260px_1fr]">
        {/* Filters */}
        <FilterPanel
          filters={filters}
          onChange={(f) => {
            setFilters(f)
            setPage(0)
          }}
          onReset={() => {
            setFilters({})
            setPage(0)
          }}
        />

        {/* Results */}
        <div>
          {loading ? (
            <div className="py-10">
              <LoadingSpinner />

              <p className="mt-4 text-center text-sm text-muted">
                Loading college data...
              </p>

              <p className="mt-1 text-center text-xs text-muted">
                If the backend is starting up, this may take a few seconds.
              </p>
            </div>
          ) : error ? (
            /* API / Render Error */
            <div className="rounded-2xl border border-line bg-white p-8 text-center">
              <p className="font-display text-lg font-semibold text-ink">
                College data couldn't be loaded
              </p>

              <p className="mt-2 text-sm text-muted">
                The backend may be starting up or temporarily unavailable.
                Please try again.
              </p>

              <button
                type="button"
                onClick={handleRetry}
                className="mt-5 rounded-full bg-ink px-5 py-2.5 text-sm font-medium text-paper transition hover:bg-ink-2"
              >
                Try again
              </button>
            </div>
          ) : view === 'map' ? (
            /* Map View */
            result.content.length > 0 ? (
              <MapView
                colleges={result.content}
                height="560px"
              />
            ) : (
              <div className="rounded-2xl border border-line bg-white p-8 text-center">
                <p className="font-medium text-ink">
                  No colleges found.
                </p>

                <p className="mt-2 text-sm text-muted">
                  Try widening your search or removing some filters.
                </p>
              </div>
            )
          ) : (
            <>
              {/* College Grid */}
              {result.content.length > 0 ? (
                <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-3">
                  {result.content.map((c) => (
                    <CollegeCard
                      key={c.id}
                      college={c}
                      onToggleFavorite={
                        user ? toggleFavorite : undefined
                      }
                      isFavorite={favoriteIds.includes(c.id)}
                      selectable
                      selected={selectedIds.includes(c.id)}
                      onSelect={toggleSelect}
                    />
                  ))}
                </div>
              ) : (
                <div className="rounded-2xl border border-line bg-white p-8 text-center">
                  <p className="font-medium text-ink">
                    No colleges match these filters.
                  </p>

                  <p className="mt-2 text-sm text-muted">
                    Try widening your search or removing some filters.
                  </p>
                </div>
              )}

              {/* Pagination */}
              {result.totalPages > 1 && (
                <div className="mt-10 flex items-center justify-center gap-2">
                  <button
                    type="button"
                    disabled={page === 0}
                    onClick={() =>
                      setPage((p) => p - 1)
                    }
                    className="rounded-full border border-line px-4 py-2 text-sm disabled:opacity-40"
                  >
                    Previous
                  </button>

                  <span className="text-sm text-muted">
                    Page {page + 1} of {result.totalPages}
                  </span>

                  <button
                    type="button"
                    disabled={
                      page >= result.totalPages - 1
                    }
                    onClick={() =>
                      setPage((p) => p + 1)
                    }
                    className="rounded-full border border-line px-4 py-2 text-sm disabled:opacity-40"
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}