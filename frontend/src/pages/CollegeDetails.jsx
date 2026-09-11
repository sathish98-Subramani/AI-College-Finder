import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { MapPin, Globe, Star, Users, Award } from 'lucide-react'
import { collegeService } from '../services/collegeService'
import { favoriteService } from '../services/favoriteService'
import { useAuth } from '../context/AuthContext'
import CollegeCard from '../components/CollegeCard'
import MapView from '../components/MapView'
import LoadingSpinner from '../components/LoadingSpinner'

export default function CollegeDetails() {
  const { id } = useParams()
  const { user } = useAuth()
  const [college, setCollege] = useState(null)
  const [similar, setSimilar] = useState([])
  const [loading, setLoading] = useState(true)
  const [isFavorite, setIsFavorite] = useState(false)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    setLoading(true)
    setNotFound(false)
    Promise.all([
      collegeService.getById(id),
      collegeService.similar(id, 3),
    ])
      .then(([c, sim]) => { setCollege(c); setSimilar(sim) })
      .catch(() => setNotFound(true))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => {
    if (!user) return
    favoriteService.list().then((favs) => setIsFavorite(favs.some((f) => f.id === Number(id)))).catch(() => {})
  }, [user, id])

  const toggleFavorite = async () => {
    if (!user) return
    if (isFavorite) await favoriteService.remove(id)
    else await favoriteService.add(id)
    setIsFavorite(!isFavorite)
  }

  if (loading) return <LoadingSpinner label="Loading college details" />
  if (notFound || !college) {
    return (
      <div className="mx-auto max-w-2xl px-5 py-24 text-center">
        <p className="font-display text-2xl font-semibold text-ink">College not found</p>
        <p className="mt-2 text-sm text-muted">It may have been removed, or the link is incorrect.</p>
        <Link to="/colleges" className="mt-6 inline-block rounded-full bg-ink px-6 py-2.5 text-sm text-paper">
          Back to colleges
        </Link>
      </div>
    )
  }

  const courses = college.courses ? college.courses.split(';').map((c) => c.trim()) : []

  return (
    <div className="mx-auto max-w-5xl px-5 py-12">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div>
          <p className="text-xs font-medium uppercase tracking-wide text-gold">{college.institutionType}</p>
          <h1 className="mt-1 font-display text-3xl font-semibold text-ink md:text-4xl">{college.collegeName}</h1>
          <p className="mt-2 flex items-center gap-1 text-sm text-muted">
            <MapPin className="h-4 w-4" /> {college.city}, {college.state}
          </p>
        </div>
        {user && (
          <button
            onClick={toggleFavorite}
            className={`shrink-0 rounded-full border px-5 py-2.5 text-sm font-medium ${isFavorite ? 'border-danger text-danger' : 'border-line text-ink'}`}
          >
            {isFavorite ? 'Saved to favorites' : 'Save to favorites'}
          </button>
        )}
      </div>

      {college.dataStatus && (
        <p className="mt-4 rounded-lg bg-paper-2/60 px-4 py-2 text-xs text-muted">
          Data note: {college.dataStatus} {college.dataReferenceYear ? `(reference year ${college.dataReferenceYear})` : ''}
        </p>
      )}

      <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Stat label="Annual fee" value={college.annualFeeLakh != null ? `₹${college.annualFeeLakh}L` : 'N/A'} />
        <Stat label="Placement rate" value={college.placementRatePct != null ? `${college.placementRatePct}%` : 'N/A'} />
        <Stat label="Average package" value={college.averagePackageLpa != null ? `₹${college.averagePackageLpa} LPA` : 'N/A'} />
        <Stat label="Highest package" value={college.highestPackageApprox || 'N/A'} />
        <Stat label="NIRF rank" value={college.nirfRank ?? 'N/A'} icon={Award} />
        <Stat label="Rating" value={`${college.rating ?? 'N/A'} / 5`} icon={Star} />
        <Stat label="Hostel" value={college.hostel ? 'Available' : 'Not available'} icon={Users} />
        <Stat label="Cutoff exam" value={college.cutoffExam || 'N/A'} />
      </div>

      {college.cutoffNote && (
        <div className="mt-6 rounded-2xl border border-line bg-white p-5">
          <p className="font-display text-base font-semibold text-ink">Cutoff notes</p>
          <p className="mt-2 text-sm text-muted">{college.cutoffNote}</p>
        </div>
      )}

      {courses.length > 0 && (
        <div className="mt-6">
          <p className="font-display text-base font-semibold text-ink">Courses offered</p>
          <div className="mt-3 flex flex-wrap gap-2">
            {courses.map((c) => (
              <span key={c} className="rounded-full border border-line px-3 py-1.5 text-sm text-ink">{c}</span>
            ))}
          </div>
        </div>
      )}

      {college.website && (
        <a
          href={college.website.startsWith('http') ? college.website : `https://${college.website}`}
          target="_blank"
          rel="noreferrer"
          className="mt-6 inline-flex items-center gap-2 text-sm font-medium text-ink underline decoration-gold underline-offset-4"
        >
          <Globe className="h-4 w-4" /> Visit official website
        </a>
      )}

      <div className="mt-10">
        <p className="font-display text-base font-semibold text-ink">Location</p>
        <div className="mt-3">
          <MapView colleges={[college]} height="320px" />
        </div>
      </div>

      {similar.length > 0 && (
        <div className="mt-12">
          <p className="font-display text-xl font-semibold text-ink">Similar colleges</p>
          <div className="mt-4 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {similar.map((c) => <CollegeCard key={c.id} college={c} />)}
          </div>
        </div>
      )}
    </div>
  )
}

function Stat({ label, value, icon: Icon }) {
  return (
    <div className="rounded-2xl border border-line bg-white p-4">
      <p className="flex items-center gap-1 text-xs text-muted">{Icon && <Icon className="h-3.5 w-3.5" />} {label}</p>
      <p className="mt-1 font-display text-lg font-semibold text-ink">{value}</p>
    </div>
  )
}
