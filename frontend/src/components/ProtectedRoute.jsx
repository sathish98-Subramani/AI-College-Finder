import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import LoadingSpinner from './LoadingSpinner'

export default function ProtectedRoute({ children, adminOnly = false }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) return <LoadingSpinner label="Checking your session" />
  if (!user) return <Navigate to="/login" state={{ from: location }} replace />
  if (adminOnly && user.role !== 'ADMIN') return <Navigate to="/dashboard" replace />

  return children
}
