import api from './api'

export const favoriteService = {
  async list() {
    const { data } = await api.get('/favorites')
    return data
  },
  async add(collegeId) {
    await api.post(`/favorites/${collegeId}`)
  },
  async remove(collegeId) {
    await api.delete(`/favorites/${collegeId}`)
  },
}
