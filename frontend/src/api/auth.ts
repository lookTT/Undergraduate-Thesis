import { http } from './http'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  realName: string
  roles: string[]
}

export interface UserResponse {
  id: number
  username: string
  realName: string
  phone: string
  status: number
}

export function login(data: LoginRequest) {
  return http.post<{ code: number; message: string; data: LoginResponse }>('/auth/login', data)
}

export function me() {
  return http.get<{ code: number; message: string; data: UserResponse }>('/auth/me')
}

export function logout() {
  return http.post<{ code: number; message: string; data: null }>('/auth/logout')
}
