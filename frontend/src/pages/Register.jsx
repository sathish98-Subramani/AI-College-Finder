import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '', phone: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(form)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create your account')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto flex max-w-md flex-col px-5 py-20">
      <h1 className="font-display text-3xl font-semibold text-ink">Create your account</h1>
      <p className="mt-2 text-sm text-muted">Get personalized college recommendations in a couple of minutes.</p>

      <form onSubmit={handleSubmit} className="mt-8 space-y-4">
        {error && <p className="rounded-lg bg-danger/10 px-3 py-2 text-sm text-danger">{error}</p>}

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Full name</label>
          <input
            required
            value={form.fullName}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })}
            className="w-full rounded-lg border border-line px-3 py-2.5 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Email</label>
          <input
            type="email"
            required
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            className="w-full rounded-lg border border-line px-3 py-2.5 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Phone (optional)</label>
          <input
            value={form.phone}
            onChange={(e) => setForm({ ...form, phone: e.target.value })}
            className="w-full rounded-lg border border-line px-3 py-2.5 outline-none focus:border-gold"
          />
        </div>

        <div>
          <label className="mb-1 block text-xs font-medium text-muted">Password</label>
          <input
            type="password"
            required
            minLength={6}
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            className="w-full rounded-lg border border-line px-3 py-2.5 outline-none focus:border-gold"
          />
          <p className="mt-1 text-xs text-muted">At least 6 characters.</p>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-full bg-ink px-6 py-3 text-sm font-medium text-paper transition hover:bg-ink-2 disabled:opacity-50"
        >
          {loading ? 'Creating account…' : 'Create account'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-muted">
        Already have an account? <Link to="/login" className="font-medium text-ink underline decoration-gold underline-offset-4">Sign in</Link>
      </p>
    </div>
  )
}
