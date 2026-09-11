import api from './api'

export const searchHistoryService = {
  async recent(limit = 10) {
    const { data } = await api.get('/search-history', { params: { limit } })
    return data
  },
}
