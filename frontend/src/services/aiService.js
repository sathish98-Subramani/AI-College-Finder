import api from './api'

export const aiService = {
  async chat(message) {
    const { data } = await api.post('/ai/chat', { message })
    return data
  },
}
