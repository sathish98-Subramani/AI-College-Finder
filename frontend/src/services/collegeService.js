import api from './api'

export const collegeService = {
  async search(params) {
    const { data } = await api.get('/colleges', { params })
    return data
  },
  async getById(id) {
    const { data } = await api.get(`/colleges/${id}`)
    return data
  },
  async similar(id, limit = 4) {
    const { data } = await api.get(`/colleges/${id}/similar`, { params: { limit } })
    return data
  },
  async compare(collegeIds) {
    const { data } = await api.post('/compare', { collegeIds })
    return data
  },
}
