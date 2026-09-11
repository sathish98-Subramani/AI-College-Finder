import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/authService'

export default function Profile() {
  const { user, refreshProfile } = useAuth()
  const [form, setForm] = useState({
    fullName: user?.fullName || '',
    phone: user?.phone || '',
    state: user?.state || '',
    city: user?.city || '',
    academicPercentage: user?.academicPercentage || '',
    entranceExam: user?.entranceExam || '',
    entranceScore: user?.entranceScore || '',
    preferredCourse: user?.preferredCourse || '',
    preferredBranch: user?.preferredBranch || '',
    budgetLakh: user?.budgetLakh || '',
    hostelRequired: user?.hostelRequired || false,
  })
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)

  const set = (key, value) => { setForm((f) => ({ ...f, [key]: value })); setSaved(false) }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await authService.updateProfile({
        ...form,
        academicPercentage: form.academicPercentage ? Number(form.academicPercentage) : null,
        entranceScore: form.entranceScore ? Number(form.entranceScore) : null,
        budgetLakh: form.budgetLakh ? Number(form.budgetLakh) : null,
      })
      await refreshProfile()
      setSaved(true)
    } finally {
      setSaving(false)
    }
  }

  const completion = Object.values(form).filter((v) => v !== '' && v !== null && v !== false).length
  const totalFields = Object.keys(form).length

  return (
    <div className="mx-auto max-w-2xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Your profile</h1>
      <p className="mt-2 text-sm text-muted">
        Profile completion: {Math.round((completion / totalFields) * 100)}%
      </p>
      <div className="mt-2 h-1.5 w-full overflow-hidden rounded-full bg-paper-2">
        <div className="h-full bg-gold" style={{ width: `${Math.round((completion / totalFields) * 100)}%` }} />
      </div>

      <form onSubmit={handleSubmit} className="mt-8 grid gap-4 rounded-2xl border border-line bg-white p-6 sm:grid-cols-2">
        <Field label="Full name"><input value={form.fullName} onChange={(e) => set('fullName', e.target.value)} className="input" /></Field>
        <Field label="Phone"><input value={form.phone} onChange={(e) => set('phone', e.target.value)} className="input" /></Field>
        <Field label="State"><input value={form.state} onChange={(e) => set('state', e.target.value)} className="input" /></Field>
        <Field label="City"><input value={form.city} onChange={(e) => set('city', e.target.value)} className="input" /></Field>
        <Field label="Academic %"><input type="number" step="0.1" value={form.academicPercentage} onChange={(e) => set('academicPercentage', e.target.value)} className="input" /></Field>
        <Field label="Entrance exam"><input value={form.entranceExam} onChange={(e) => set('entranceExam', e.target.value)} className="input" placeholder="e.g. JEE Main" /></Field>
        <Field label="Entrance score"><input type="number" step="0.1" value={form.entranceScore} onChange={(e) => set('entranceScore', e.target.value)} className="input" /></Field>
        <Field label="Preferred course"><input value={form.preferredCourse} onChange={(e) => set('preferredCourse', e.target.value)} className="input" /></Field>
        <Field label="Preferred branch"><input value={form.preferredBranch} onChange={(e) => set('preferredBranch', e.target.value)} className="input" /></Field>
        <Field label="Budget (₹ lakh/yr)"><input type="number" step="0.1" value={form.budgetLakh} onChange={(e) => set('budgetLakh', e.target.value)} className="input" /></Field>

        <label className="flex items-center gap-2 text-sm sm:col-span-2">
          <input type="checkbox" checked={form.hostelRequired} onChange={(e) => set('hostelRequired', e.target.checked)} className="h-4 w-4 accent-gold" />
          I need hostel accommodation
        </label>

        {saved && <p className="text-sm text-teal sm:col-span-2">Profile updated.</p>}

        <button type="submit" disabled={saving} className="rounded-full bg-ink px-6 py-3 text-sm font-medium text-paper transition hover:bg-ink-2 disabled:opacity-50 sm:col-span-2">
          {saving ? 'Saving…' : 'Save changes'}
        </button>
      </form>

      <style>{`.input { border: 1px solid var(--color-line); border-radius: 0.5rem; padding: 0.6rem 0.75rem; font-size: 0.875rem; outline: none; background: white; width: 100%; } .input:focus { border-color: var(--color-gold); }`}</style>
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
