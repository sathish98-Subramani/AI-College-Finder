import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { recommendationService } from '../services/recommendationService'
import RecommendationCard from '../components/RecommendationCard'
import LoadingSpinner from '../components/LoadingSpinner'
import { Link } from 'react-router-dom'

const INSTITUTION_TYPES = ['', 'IIT', 'NIT', 'IIIT', 'Deemed', 'Private', 'Autonomous', 'State University', 'Central University']

export default function Recommendations() {
  const { user } = useAuth()
  const [form, setForm] = useState({
    state: '', city: '', course: '', budgetLakh: '', entranceScorePercentile: '',
    minPlacementPct: '', minRating: '', hostelRequired: false, institutionType: '',
  })
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const set = (key, value) => setForm((f) => ({ ...f, [key]: value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = {
        ...form,
        budgetLakh: form.budgetLakh ? Number(form.budgetLakh) : null,
        entranceScorePercentile: form.entranceScorePercentile ? Number(form.entranceScorePercentile) : null,
        minPlacementPct: form.minPlacementPct ? Number(form.minPlacementPct) : null,
        minRating: form.minRating ? Number(form.minRating) : null,
      }
      const data = await recommendationService.recommend(payload)
      setResults(data)
    } catch (err) {
      setError(err.response?.data?.message || 'Could not generate recommendations right now.')
    } finally {
      setLoading(false)
    }
  }

  if (!user) {
    return (
      <div className="mx-auto max-w-xl px-5 py-24 text-center">
        <h1 className="font-display text-2xl font-semibold text-ink">Sign in to get recommendations</h1>
        <p className="mt-2 text-sm text-muted">We personalize rankings to your profile, so you'll need an account first.</p>
        <Link to="/login" className="mt-6 inline-block rounded-full bg-ink px-6 py-2.5 text-sm text-paper">Sign in</Link>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-5xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Find your best-fit colleges</h1>
      <p className="mt-2 max-w-2xl text-sm text-muted">
        Tell us what matters to you. We score every college in the dataset against these
        criteria and explain exactly why each one matched or didn't.
      </p>

      <form onSubmit={handleSubmit} className="mt-8 grid gap-4 rounded-2xl border border-line bg-white p-6 sm:grid-cols-2">
        <Field label="Preferred state">
          <input value={form.state} onChange={(e) => set('state', e.target.value)} className="input" placeholder="e.g. Tamil Nadu" />
        </Field>
        <Field label="Preferred city">
          <input value={form.city} onChange={(e) => set('city', e.target.value)} className="input" placeholder="e.g. Chennai" />
        </Field>
        <Field label="Preferred course">
          <input value={form.course} onChange={(e) => set('course', e.target.value)} className="input" placeholder="e.g. CSE" />
        </Field>
        <Field label="Institution type">
          <select value={form.institutionType} onChange={(e) => set('institutionType', e.target.value)} className="input">
            {INSTITUTION_TYPES.map((t) => <option key={t} value={t}>{t || 'Any'}</option>)}
          </select>
        </Field>
        <Field label="Annual budget (₹ lakh)">
          <input type="number" step="0.1" value={form.budgetLakh} onChange={(e) => set('budgetLakh', e.target.value)} className="input" placeholder="e.g. 2.5" />
        </Field>
        <Field label="Entrance score percentile (0-100)">
          <input type="number" min="0" max="100" value={form.entranceScorePercentile} onChange={(e) => set('entranceScorePercentile', e.target.value)} className="input" placeholder="e.g. 85" />
        </Field>
        <Field label="Minimum placement rate (%)">
          <input type="number" min="0" max="100" value={form.minPlacementPct} onChange={(e) => set('minPlacementPct', e.target.value)} className="input" placeholder="e.g. 80" />
        </Field>
        <Field label="Minimum rating (out of 5)">
          <input type="number" min="0" max="5" step="0.1" value={form.minRating} onChange={(e) => set('minRating', e.target.value)} className="input" placeholder="e.g. 4" />
        </Field>
        <label className="flex items-center gap-2 text-sm text-ink sm:col-span-2">
          <input type="checkbox" checked={form.hostelRequired} onChange={(e) => set('hostelRequired', e.target.checked)} className="h-4 w-4 accent-gold" />
          I need hostel accommodation
        </label>

        {error && <p className="text-sm text-danger sm:col-span-2">{error}</p>}

        <button type="submit" disabled={loading} className="rounded-full bg-ink px-6 py-3 text-sm font-medium text-paper transition hover:bg-ink-2 disabled:opacity-50 sm:col-span-2">
          {loading ? 'Scoring colleges…' : 'Get my recommendations'}
        </button>
      </form>

      {loading && <LoadingSpinner label="Scoring every college against your profile" />}

      {results && !loading && (
        <div className="mt-10">
          <p className="font-display text-xl font-semibold text-ink">Your top matches</p>
          <div className="mt-4 grid gap-6 sm:grid-cols-2">
            {results.map((rec) => <RecommendationCard key={rec.collegeId} rec={rec} />)}
          </div>
          {results.length === 0 && <p className="mt-6 text-sm text-muted">No colleges scored well against these criteria - try loosening a filter.</p>}
        </div>
      )}

      <style>{`.input { border: 1px solid var(--color-line); border-radius: 0.5rem; padding: 0.6rem 0.75rem; font-size: 0.875rem; outline: none; background: white; } .input:focus { border-color: var(--color-gold); }`}</style>
    </div>
  )
}

function Field({ label, children }) {
  return (
    <label className="block text-sm">
      <span className="mb-1 block text-xs font-medium text-muted">{label}</span>
      {children}
    </label>
  )
}
