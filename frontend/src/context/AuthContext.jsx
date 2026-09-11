import { createContext, useContext, useEffect, useState } from 'react'
import { authService } from '../services/authService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('cf_user')
    return stored ? JSON.parse(stored) : null
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('cf_token')
    if (!token) {
      setLoading(false)
      return
    }
    authService
      .getProfile()
      .then((profile) => {
        setUser(profile)
        localStorage.setItem('cf_user', JSON.stringify(profile))
      })
      .catch(() => {
        localStorage.removeItem('cf_token')
        localStorage.removeItem('cf_user')
        setUser(null)
      })
      .finally(() => setLoading(false))
  }, [])

  const login = async (credentials) => {
    const data = await authService.login(credentials)
    localStorage.setItem('cf_token', data.token)
    localStorage.setItem('cf_user', JSON.stringify(data.user))
    setUser(data.user)
    return data.user
  }

  const register = async (payload) => {
    const data = await authService.register(payload)
    localStorage.setItem('cf_token', data.token)
    localStorage.setItem('cf_user', JSON.stringify(data.user))
    setUser(data.user)
    return data.user
  }

  const logout = () => {
    localStorage.removeItem('cf_token')
    localStorage.removeItem('cf_user')
    setUser(null)
  }

  const refreshProfile = async () => {
    const profile = await authService.getProfile()
    setUser(profile)
    localStorage.setItem('cf_user', JSON.stringify(profile))
    return profile
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, refreshProfile }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
