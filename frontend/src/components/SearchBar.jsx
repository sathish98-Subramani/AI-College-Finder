import { Search } from 'lucide-react'

export default function SearchBar({ value, onChange, onSubmit, placeholder = 'Search colleges, cities, or universities…' }) {
  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        onSubmit?.()
      }}
      className="flex w-full items-center gap-2 rounded-full border border-line bg-white px-4 py-2.5 shadow-sm focus-within:border-gold"
    >
      <Search className="h-4.5 w-4.5 shrink-0 text-muted" />
      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="w-full bg-transparent text-sm text-ink outline-none placeholder:text-muted"
      />
    </form>
  )
}
