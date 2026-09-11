import api from './api'

export const recommendationService = {
  async recommend(payload) {
    const { data } = await api.post('/recommendations', payload)
    return data
  },
  async mine() {
    const { data } = await api.get('/recommendations')
    return data
  },
}
