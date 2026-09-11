import { useState, useRef, useEffect } from 'react'
import { Send } from 'lucide-react'
import { aiService } from '../services/aiService'
import { Link } from 'react-router-dom'

export default function ChatAssistant() {
  const [messages, setMessages] = useState([
    { role: 'assistant', text: "Ask me anything about the colleges in our database - fees, hostels, placements, cutoffs, comparisons." },
  ])
  const [input, setInput] = useState('')
  const [sending, setSending] = useState(false)
  const [referenced, setReferenced] = useState([])
  const bottomRef = useRef(null)

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages])

  const send = async () => {
    const text = input.trim()
    if (!text || sending) return
    setMessages((m) => [...m, { role: 'user', text }])
    setInput('')
    setSending(true)
    try {
      const res = await aiService.chat(text)
      setMessages((m) => [...m, { role: 'assistant', text: res.reply }])
      setReferenced(res.referencedColleges || [])
    } catch {
      setMessages((m) => [...m, { role: 'assistant', text: 'Something went wrong reaching the assistant. Please try again.' }])
    } finally {
      setSending(false)
    }
  }

  return (
    <div className="mx-auto max-w-4xl px-5 py-12">
      <h1 className="font-display text-3xl font-semibold text-ink">Ask the college assistant</h1>
      <p className="mt-2 text-sm text-muted">
        Answers are grounded in the college database - if something isn't in the dataset, the assistant will say so.
      </p>

      <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_260px]">
        <div className="flex h-[32rem] flex-col rounded-2xl border border-line bg-white">
          <div className="flex-1 space-y-3 overflow-y-auto px-5 py-4">
            {messages.map((m, i) => (
              <div
                key={i}
                className={`max-w-[85%] whitespace-pre-wrap rounded-xl px-4 py-2.5 text-sm ${
                  m.role === 'user' ? 'ml-auto bg-gold/20 text-ink' : 'bg-paper-2 text-ink'
                }`}
              >
                {m.text}
              </div>
            ))}
            {sending && <div className="text-xs text-muted">Thinking…</div>}
            <div ref={bottomRef} />
          </div>
          <div className="flex items-center gap-2 border-t border-line p-4">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && send()}
              placeholder="e.g. Which colleges in Tamil Nadu have hostels under 2 lakh fees?"
              className="flex-1 rounded-full border border-line px-4 py-2.5 text-sm outline-none focus:border-gold"
            />
            <button
              onClick={send}
              disabled={sending}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-ink text-paper disabled:opacity-50"
            >
              <Send className="h-4 w-4" />
            </button>
          </div>
        </div>

        <div>
          <p className="text-xs font-medium uppercase tracking-wide text-muted">Referenced colleges</p>
          <div className="mt-3 space-y-2">
            {referenced.length === 0 ? (
              <p className="text-sm text-muted">Colleges mentioned in the assistant's last answer will show up here.</p>
            ) : (
              referenced.map((c) => (
                <Link key={c.id} to={`/college/${c.id}`} className="block rounded-xl border border-line bg-white px-3 py-2.5 text-sm hover:border-gold/60">
                  {c.collegeName}
                  <span className="block text-xs text-muted">{c.city}, {c.state}</span>
                </Link>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
