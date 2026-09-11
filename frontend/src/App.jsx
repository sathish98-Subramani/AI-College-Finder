import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Chatbot from './components/Chatbot'
import ProtectedRoute from './components/ProtectedRoute'

import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Colleges from './pages/Colleges'
import CollegeDetails from './pages/CollegeDetails'
import Recommendations from './pages/Recommendations'
import Compare from './pages/Compare'
import Favorites from './pages/Favorites'
import Profile from './pages/Profile'
import ChatAssistant from './pages/ChatAssistant'
import AdminDashboard from './pages/AdminDashboard'
import NotFound from './pages/NotFound'

export default function App() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/colleges" element={<Colleges />} />
          <Route path="/college/:id" element={<CollegeDetails />} />
          <Route path="/recommendations" element={<Recommendations />} />
          <Route path="/compare" element={<Compare />} />
          <Route path="/chat" element={<ChatAssistant />} />

          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/favorites" element={<ProtectedRoute><Favorites /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
          <Route path="/admin" element={<ProtectedRoute adminOnly><AdminDashboard /></ProtectedRoute>} />

          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
      <Footer />
      <Chatbot />
    </div>
  )
}
