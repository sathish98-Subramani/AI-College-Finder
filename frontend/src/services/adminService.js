import api from './api'

export const adminService = {
  async dashboard() {
    const { data } = await api.get('/admin/dashboard')
    return data
  },
  async importColleges(file) {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await api.post('/admin/colleges/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data
  },
  async students() {
    const { data } = await api.get('/admin/students')
    return data
  },
  async createCollege(payload) {
    const { data } = await api.post('/colleges', payload)
    return data
  },
  async updateCollege(id, payload) {
    const { data } = await api.put(`/colleges/${id}`, payload)
    return data
  },
  async deleteCollege(id) {
    await api.delete(`/colleges/${id}`)
  },
}
