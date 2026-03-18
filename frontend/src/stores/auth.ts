import { defineStore } from 'pinia'

const TOKEN_KEY = 'volunteer_token'
const USER_KEY = 'volunteer_user'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null') as null | {
      userId: number
      username: string
      realName: string
      roles: string[]
    }
  }),
  actions: {
    setSession(token: string, user: { userId: number; username: string; realName: string; roles: string[] }) {
      this.token = token
      this.user = user
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    },
    clearSession() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})
