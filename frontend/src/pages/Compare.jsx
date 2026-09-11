import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { collegeService } from '../services/collegeService'
import SearchBar from '../components/SearchBar'
import ComparisonTable from '../components/ComparisonTable'
import LoadingSpinner from '../components/LoadingSpinner'
import { X } from 'lucide-react'

export default function Compare() {
  const [searchParams] = useSearchParams()
  const [query, setQuery] = useState('')
  const [options, setOptions] = useState([])
  const [selected, setSelected] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const idsParam = searchParams.get('ids')
    if (!idsParam) return
    const ids = idsParam.split(',').map(Number).slice(0, 4)
    collegeService.compare(ids).then(setSelected).catch(() => {})
  }, [searchParams])

  useEffect(() => {
    if (!query) { setOptions([]); return }
    const timeout = setTimeout(() => {
      collegeService.search({ q: query, size: 6 }).then((res) => setOptions(res.content))
    }, 250)
    return () => clearTimeout(timeout)
  }, [query])

  const addCollege = (college) => {
    if (selected.length >= 4 || selected.some((c) => c.id === college.id)) return
    setSelected((s) => [...s, college])
    setQuery('')
    setOptions([])
  }

  const removeCollege = (id) => setSelected((s) => s.filter((c) => c.id !== id))

  return (
    <div className="mx-auto max-w-6xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Compare colleges</h1>
      <p className="mt-2 text-sm text-muted">Add 2 to 4 colleges to see how they stack up side by side.</p>

      <div className="mt-6 max-w-md">
        <SearchBar value={query} onChange={setQuery} placeholder="Search a college to add…" />
        {options.length > 0 && (
          <div className="mt-2 overflow-hidden rounded-xl border border-line bg-white">
            {options.map((c) => (
              <button
                key={c.id}
                onClick={() => addCollege(c)}
                className="block w-full px-4 py-2.5 text-left text-sm hover:bg-paper-2/60"
              >
                {c.collegeName} <span className="text-muted">— {c.city}, {c.state}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      {selected.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-2">
          {selected.map((c) => (
            <span key={c.id} className="flex items-center gap-2 rounded-full bg-ink px-4 py-1.5 text-xs text-paper">
              {c.collegeName}
              <button onClick={() => removeCollege(c.id)} aria-label={`Remove ${c.collegeName}`}>
                <X className="h-3 w-3" />
              </button>
            </span>
          ))}
        </div>
      )}

      <div className="mt-8">
        {loading ? (
          <LoadingSpinner />
        ) : selected.length >= 2 ? (
          <ComparisonTable colleges={selected} />
        ) : (
          <p className="text-sm text-muted">Add at least 2 colleges to compare.</p>
        )}
      </div>
    </div>
  )
}
