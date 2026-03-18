import { defineStore } from 'pinia'
import { me } from '@/api/auth'

const TOKEN_KEY = 'volunteer_token'
const USER_KEY = 'volunteer_user'

export interface AuthUser {
  userId: number
  username: string
  realName: string
  phone?: string
  status?: number
  roles: string[]
}

function decodeJwtRole(token: string): string[] {
  try {
    const payload = token.split('.')[1]
    if (!payload) return []
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = JSON.parse(atob(normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=')))
    const role = json.role
    return typeof role === 'string' && role ? [role] : []
  } catch {
    return []
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null') as null | AuthUser
  }),
  actions: {
    setSession(token: string, user: AuthUser) {
      this.token = token
      this.user = user
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    },
    async hydrateSession() {
      if (!this.token) return
      if (this.user) return
      try {
        const { data } = await me()
        if (data.code !== 0 || !data.data) {
          this.clearSession()
          return
        }
        const roles = decodeJwtRole(this.token)
        this.user = {
          userId: data.data.id,
          username: data.data.username,
          realName: data.data.realName,
          phone: data.data.phone,
          status: data.data.status,
          roles
        }
        localStorage.setItem(USER_KEY, JSON.stringify(this.user))
      } catch {
        this.clearSession()
      }
    },
    clearSession() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
    hasRole(role: string) {
      return this.user?.roles?.includes(role) ?? false
    }
  }
})
