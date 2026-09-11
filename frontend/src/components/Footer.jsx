import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="border-t border-line bg-paper-2/60">
      <div className="mx-auto grid max-w-6xl gap-8 px-5 py-12 md:grid-cols-3">
        <div>
          <p className="font-display text-lg font-semibold text-ink">College Finder</p>
          <p className="mt-2 max-w-xs text-sm text-muted">
            Discover, compare and get personalized recommendations for Indian colleges,
            built on real institutional data.
          </p>
        </div>
        <div>
          <p className="text-sm font-semibold text-ink">Explore</p>
          <ul className="mt-3 space-y-2 text-sm text-muted">
            <li><Link to="/colleges" className="hover:text-ink">Browse colleges</Link></li>
            <li><Link to="/recommendations" className="hover:text-ink">Get recommendations</Link></li>
            <li><Link to="/compare" className="hover:text-ink">Compare colleges</Link></li>
            <li><Link to="/chat" className="hover:text-ink">Ask the assistant</Link></li>
          </ul>
        </div>
        <div>
          <p className="text-sm font-semibold text-ink">Data</p>
          <p className="mt-3 text-sm text-muted">
            College information is sourced from a curated dataset of real Indian institutions.
            Figures marked approximate are self-reported or estimated and should be verified with
            the institution directly.
          </p>
        </div>
      </div>
      <div className="border-t border-line px-5 py-5 text-center text-xs text-muted">
        © {new Date().getFullYear()} College Finder. Built for students, by students.
      </div>
    </footer>
  )
}
