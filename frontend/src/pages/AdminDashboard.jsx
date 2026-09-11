import { useEffect, useState } from 'react'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import { adminService } from '../services/adminService'
import LoadingSpinner from '../components/LoadingSpinner'
import { UploadCloud } from 'lucide-react'

const COLORS = ['#c98a2c', '#1f7a5c', '#14213d', '#e0a94e', '#2a9c76', '#b3492f']

export default function AdminDashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [file, setFile] = useState(null)
  const [importResult, setImportResult] = useState(null)
  const [importing, setImporting] = useState(false)

  const load = () => {
    setLoading(true)
    adminService.dashboard().then(setStats).catch(() => {}).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleImport = async (e) => {
    e.preventDefault()
    if (!file) return
    setImporting(true)
    try {
      const result = await adminService.importColleges(file)
      setImportResult(result)
      load()
    } finally {
      setImporting(false)
    }
  }

  if (loading || !stats) return <LoadingSpinner label="Loading admin dashboard" />

  const byState = Object.entries(stats.collegesByState || {}).map(([name, value]) => ({ name, value }))
  const byType = Object.entries(stats.collegesByType || {}).map(([name, value]) => ({ name, value }))

  return (
    <div className="mx-auto max-w-6xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Admin dashboard</h1>

      <div className="mt-8 grid gap-4 sm:grid-cols-3">
        <StatCard label="Total colleges" value={stats.totalColleges} />
        <StatCard label="Total students" value={stats.totalStudents} />
        <StatCard label="Recommendations generated" value={stats.totalRecommendationsGenerated} />
      </div>

      <div className="mt-6 grid gap-4 sm:grid-cols-3">
        <StatCard label="Average annual fee" value={`₹${stats.averageAnnualFeeLakh}L`} />
        <StatCard label="Average rating" value={`${stats.averageRating} / 5`} />
        <StatCard label="Average placement rate" value={`${stats.averagePlacementRatePct}%`} />
      </div>

      <div className="mt-10 grid gap-8 lg:grid-cols-2">
        <div className="rounded-2xl border border-line bg-white p-5">
          <p className="font-display text-base font-semibold text-ink">Colleges by state</p>
          <div className="mt-4 h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={byState}>
                <XAxis dataKey="name" tick={{ fontSize: 10 }} interval={0} angle={-40} textAnchor="end" height={70} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" fill="#c98a2c" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="rounded-2xl border border-line bg-white p-5">
          <p className="font-display text-base font-semibold text-ink">Colleges by institution type</p>
          <div className="mt-4 h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={byType} dataKey="value" nameKey="name" outerRadius={100} label>
                  {byType.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="mt-10 rounded-2xl border border-line bg-white p-6">
        <p className="font-display text-lg font-semibold text-ink">Import / update college dataset</p>
        <p className="mt-1 text-sm text-muted">
          Upload a CSV with the standard columns. Existing colleges (matched by name) are updated; new ones are inserted.
        </p>
        <form onSubmit={handleImport} className="mt-4 flex flex-wrap items-center gap-3">
          <label className="flex cursor-pointer items-center gap-2 rounded-full border border-line px-4 py-2 text-sm">
            <UploadCloud className="h-4 w-4" />
            {file ? file.name : 'Choose CSV file'}
            <input type="file" accept=".csv" className="hidden" onChange={(e) => setFile(e.target.files[0])} />
          </label>
          <button
            type="submit"
            disabled={!file || importing}
            className="rounded-full bg-ink px-5 py-2 text-sm font-medium text-paper disabled:opacity-50"
          >
            {importing ? 'Importing…' : 'Import'}
          </button>
        </form>

        {importResult && (
          <div className="mt-4 grid grid-cols-2 gap-3 text-sm sm:grid-cols-5">
            <ResultChip label="Total" value={importResult.totalRecords} />
            <ResultChip label="Inserted" value={importResult.inserted} />
            <ResultChip label="Updated" value={importResult.updated} />
            <ResultChip label="Duplicates" value={importResult.duplicates} />
            <ResultChip label="Failed" value={importResult.failed} />
          </div>
        )}
      </div>
    </div>
  )
}

function StatCard({ label, value }) {
  return (
    <div className="rounded-2xl border border-line bg-white p-5">
      <p className="text-xs text-muted">{label}</p>
      <p className="mt-1 font-display text-2xl font-semibold text-ink">{value}</p>
    </div>
  )
}

function ResultChip({ label, value }) {
  return (
    <div className="rounded-xl bg-paper-2/60 px-3 py-2 text-center">
      <p className="text-xs text-muted">{label}</p>
      <p className="font-semibold text-ink">{value}</p>
    </div>
  )
}
