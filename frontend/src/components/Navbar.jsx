import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { GraduationCap, Menu, X, User } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

const navLink = ({ isActive }) =>
  `text-sm font-medium transition-colors ${
    isActive ? 'text-ink' : 'text-muted hover:text-ink'
  }`

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <header className="sticky top-0 z-40 border-b border-line bg-paper/90 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-4">
        <Link to="/" className="flex items-center gap-2 font-display text-lg font-semibold text-ink">
          <GraduationCap className="h-6 w-6 text-gold" strokeWidth={1.75} />
          College Finder
        </Link>

        <nav className="hidden items-center gap-7 md:flex">
          <NavLink to="/colleges" className={navLink}>Explore Colleges</NavLink>
          <NavLink to="/recommendations" className={navLink}>Recommendations</NavLink>
          <NavLink to="/compare" className={navLink}>Compare</NavLink>
          <NavLink to="/chat" className={navLink}>Ask the Assistant</NavLink>
          {user ? (
            <>
              <NavLink to="/dashboard" className={navLink}>Dashboard</NavLink>
              {user.role === 'ADMIN' && <NavLink to="/admin" className={navLink}>Admin</NavLink>}
              <button onClick={handleLogout} className="text-sm font-medium text-muted hover:text-danger">
                Sign out
              </button>
              <Link
                to="/profile"
                className="flex h-9 w-9 items-center justify-center rounded-full bg-ink text-paper"
                title={user.fullName}
              >
                <User className="h-4 w-4" />
              </Link>
            </>
          ) : (
            <>
              <NavLink to="/login" className={navLink}>Sign in</NavLink>
              <Link
                to="/register"
                className="rounded-full bg-ink px-4 py-2 text-sm font-medium text-paper transition hover:bg-ink-2"
              >
                Get started
              </Link>
            </>
          )}
        </nav>

        <button className="md:hidden" onClick={() => setOpen((o) => !o)} aria-label="Toggle menu">
          {open ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
        </button>
      </div>

      {open && (
        <div className="border-t border-line bg-paper px-5 py-4 md:hidden">
          <div className="flex flex-col gap-4">
            <NavLink to="/colleges" onClick={() => setOpen(false)} className={navLink}>Explore Colleges</NavLink>
            <NavLink to="/recommendations" onClick={() => setOpen(false)} className={navLink}>Recommendations</NavLink>
            <NavLink to="/compare" onClick={() => setOpen(false)} className={navLink}>Compare</NavLink>
            <NavLink to="/chat" onClick={() => setOpen(false)} className={navLink}>Ask the Assistant</NavLink>
            {user ? (
              <>
                <NavLink to="/dashboard" onClick={() => setOpen(false)} className={navLink}>Dashboard</NavLink>
                <NavLink to="/profile" onClick={() => setOpen(false)} className={navLink}>Profile</NavLink>
                {user.role === 'ADMIN' && (
                  <NavLink to="/admin" onClick={() => setOpen(false)} className={navLink}>Admin</NavLink>
                )}
                <button onClick={handleLogout} className="text-left text-sm font-medium text-danger">
                  Sign out
                </button>
              </>
            ) : (
              <>
                <NavLink to="/login" onClick={() => setOpen(false)} className={navLink}>Sign in</NavLink>
                <NavLink to="/register" onClick={() => setOpen(false)} className={navLink}>Get started</NavLink>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  )
}
