import { useState, useRef, useEffect } from 'react'
import { MessageCircle, X, Send } from 'lucide-react'
import { aiService } from '../services/aiService'

export default function Chatbot() {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([
    { role: 'assistant', text: "Hi! Ask me things like \"colleges in Chennai under 2 lakh fees\" or \"which colleges have hostels?\"" },
  ])
  const [input, setInput] = useState('')
  const [sending, setSending] = useState(false)
  const bottomRef = useRef(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, open])

  const send = async () => {
    const text = input.trim()
    if (!text || sending) return
    setMessages((m) => [...m, { role: 'user', text }])
    setInput('')
    setSending(true)
    try {
      const res = await aiService.chat(text)
      setMessages((m) => [...m, { role: 'assistant', text: res.reply }])
    } catch {
      setMessages((m) => [...m, { role: 'assistant', text: 'Something went wrong reaching the assistant. Please try again.' }])
    } finally {
      setSending(false)
    }
  }

  if (!open) {
    return (
      <button
        onClick={() => setOpen(true)}
        className="fixed bottom-6 right-6 z-50 flex h-14 w-14 items-center justify-center rounded-full bg-ink text-paper shadow-lg transition hover:bg-ink-2"
        aria-label="Open college assistant"
      >
        <MessageCircle className="h-6 w-6" />
      </button>
    )
  }

  return (
    <div className="fixed bottom-6 right-6 z-50 flex h-[28rem] w-[22rem] flex-col overflow-hidden rounded-2xl border border-line bg-white shadow-2xl">
      <div className="flex items-center justify-between border-b border-line bg-ink px-4 py-3 text-paper">
        <p className="text-sm font-semibold">College Assistant</p>
        <button onClick={() => setOpen(false)} aria-label="Close chat">
          <X className="h-4 w-4" />
        </button>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto px-4 py-3">
        {messages.map((m, i) => (
          <div
            key={i}
            className={`max-w-[85%] whitespace-pre-wrap rounded-xl px-3 py-2 text-sm ${
              m.role === 'user' ? 'ml-auto bg-gold/20 text-ink' : 'bg-paper-2 text-ink'
            }`}
          >
            {m.text}
          </div>
        ))}
        {sending && <div className="text-xs text-muted">Thinking…</div>}
        <div ref={bottomRef} />
      </div>

      <div className="flex items-center gap-2 border-t border-line p-3">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && send()}
          placeholder="Ask about colleges…"
          className="flex-1 rounded-full border border-line px-3 py-2 text-sm outline-none focus:border-gold"
        />
        <button
          onClick={send}
          disabled={sending}
          className="flex h-9 w-9 items-center justify-center rounded-full bg-ink text-paper disabled:opacity-50"
          aria-label="Send message"
        >
          <Send className="h-4 w-4" />
        </button>
      </div>
    </div>
  )
}
