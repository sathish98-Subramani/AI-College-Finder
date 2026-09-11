import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ArrowRight, Search, SlidersHorizontal, Sparkles, ScanSearch } from 'lucide-react'
import { collegeService } from '../services/collegeService'
import CollegeCard from '../components/CollegeCard'
import LoadingSpinner from '../components/LoadingSpinner'

const STEPS = [
  { icon: Search, title: 'Search', body: 'Look up colleges by name, city, course or university across the dataset.' },
  { icon: SlidersHorizontal, title: 'Filter & compare', body: 'Narrow by fees, rating, placement rate and hostel availability, then compare finalists side by side.' },
  { icon: Sparkles, title: 'Get matched', body: 'Tell us your budget, course and cutoff score - we rank colleges against your actual profile.' },
]

export default function Home() {
  const [topColleges, setTopColleges] = useState([])
  const [loading, setLoading] = useState(true)
  const [totalColleges, setTotalColleges] = useState(null)

  useEffect(() => {
    collegeService
      .search({ page: 0, size: 3, sortBy: 'nirfRank', sortDir: 'asc' })
      .then((res) => {
        setTopColleges(res.content)
        setTotalColleges(res.totalElements)
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  return (
    <div>
      {/* Hero */}
      <section className="border-b border-line bg-gradient-to-b from-paper-2/60 to-paper">
        <div className="mx-auto grid max-w-6xl items-center gap-10 px-5 py-20 md:grid-cols-[1.1fr_0.9fr] md:py-28">
          <div>
            <p className="mb-4 text-sm font-medium tracking-wide text-gold">
              {totalColleges ? `${totalColleges} real Indian colleges, and counting` : 'Real institutional data'}
            </p>
            <h1 className="font-display text-4xl font-semibold leading-[1.1] text-ink md:text-6xl">
              Find the right college for your future
            </h1>
            <p className="mt-6 max-w-md text-base text-muted md:text-lg">
              Discover, compare and get personalized college recommendations using real college data -
              fees, cutoffs, placements and ratings, not guesswork.
            </p>
            <div className="mt-8 flex flex-wrap gap-4">
              <Link
                to="/recommendations"
                className="flex items-center gap-2 rounded-full bg-ink px-6 py-3 text-sm font-medium text-paper transition hover:bg-ink-2"
              >
                Find My College <ArrowRight className="h-4 w-4" />
              </Link>
              <Link
                to="/colleges"
                className="flex items-center gap-2 rounded-full border border-line px-6 py-3 text-sm font-medium text-ink transition hover:border-gold"
              >
                Explore Colleges
              </Link>
            </div>
          </div>

          <div className="relative hidden md:block">
            <div className="rounded-3xl border border-line bg-white p-6 shadow-[0_20px_60px_-20px_rgba(20,33,61,0.25)]">
              <p className="font-display text-sm font-semibold text-ink">Why students trust the data</p>
              <ul className="mt-4 space-y-3 text-sm text-muted">
                <li className="flex items-center gap-2"><ScanSearch className="h-4 w-4 text-teal" /> No invented colleges or fabricated stats</li>
                <li className="flex items-center gap-2"><ScanSearch className="h-4 w-4 text-teal" /> Fees, cutoffs and placement rates from real records</li>
                <li className="flex items-center gap-2"><ScanSearch className="h-4 w-4 text-teal" /> Transparent, criteria-based recommendation scoring</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="mx-auto max-w-6xl px-5 py-20">
        <h2 className="font-display text-2xl font-semibold text-ink md:text-3xl">How it works</h2>
        <div className="mt-8 grid gap-6 md:grid-cols-3">
          {STEPS.map((step) => (
            <div key={step.title} className="rounded-2xl border border-line bg-white p-6">
              <step.icon className="h-6 w-6 text-gold" strokeWidth={1.75} />
              <p className="mt-4 font-display text-lg font-semibold text-ink">{step.title}</p>
              <p className="mt-2 text-sm text-muted">{step.body}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Top colleges */}
      <section className="border-t border-line bg-paper-2/40">
        <div className="mx-auto max-w-6xl px-5 py-20">
          <div className="flex items-center justify-between">
            <h2 className="font-display text-2xl font-semibold text-ink md:text-3xl">Top ranked colleges</h2>
            <Link to="/colleges" className="text-sm font-medium text-ink underline decoration-gold underline-offset-4">
              See all
            </Link>
          </div>
          {loading ? (
            <LoadingSpinner />
          ) : (
            <div className="mt-8 grid gap-6 md:grid-cols-3">
              {topColleges.map((c) => (
                <CollegeCard key={c.id} college={c} />
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Why choose us */}
      <section className="mx-auto max-w-6xl px-5 py-20">
        <h2 className="font-display text-2xl font-semibold text-ink md:text-3xl">Why choose College Finder</h2>
        <div className="mt-8 grid gap-6 md:grid-cols-2">
          <div className="rounded-2xl border border-line bg-white p-6">
            <p className="font-display text-lg font-semibold text-ink">Grounded, not generated</p>
            <p className="mt-2 text-sm text-muted">
              Every college, fee figure and placement number comes from the underlying dataset.
              Our AI assistant is instructed to only answer from that data, and says so plainly when
              something isn't available.
            </p>
          </div>
          <div className="rounded-2xl border border-line bg-white p-6">
            <p className="font-display text-lg font-semibold text-ink">Built around your profile</p>
            <p className="mt-2 text-sm text-muted">
              Recommendations weigh course fit, location, budget, cutoff compatibility, placement rate,
              rating and hostel needs - so the ranking reflects what actually matters to you.
            </p>
          </div>
        </div>
      </section>
    </div>
  )
}
