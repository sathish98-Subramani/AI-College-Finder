import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="mx-auto flex max-w-lg flex-col items-center px-5 py-32 text-center">
      <p className="font-display text-6xl font-semibold text-ink">404</p>
      <p className="mt-3 text-lg text-muted">This page doesn't exist, or the link is out of date.</p>
      <Link to="/" className="mt-6 rounded-full bg-ink px-6 py-2.5 text-sm text-paper">
        Back to home
      </Link>
    </div>
  )
}
