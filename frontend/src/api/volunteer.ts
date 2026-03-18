import { http } from './http'
import type { ApiResponse, PageVO } from './common'

export interface VolunteerVO {
  id: number
  userId: number | null
  studentOrMemberNo: string
  gender: string
  age: number | null
  communityName: string
  skillTag: string
  remark: string
}

export interface VolunteerSaveRequest {
  userId?: number | null
  studentOrMemberNo: string
  gender?: string
  age?: number | null
  communityName?: string
  skillTag?: string
  remark?: string
}

export function getVolunteers(pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<VolunteerVO>>>('/volunteers', {
    params: { pageNum, pageSize }
  })
}

export function getVolunteerById(id: number) {
  return http.get<ApiResponse<VolunteerVO>>(`/volunteers/${id}`)
}

export function createVolunteer(data: VolunteerSaveRequest) {
  return http.post<ApiResponse<null>>('/volunteers', data)
}

export function updateVolunteer(id: number, data: VolunteerSaveRequest) {
  return http.put<ApiResponse<null>>(`/volunteers/${id}`, data)
}

export function deleteVolunteer(id: number) {
  return http.delete<ApiResponse<null>>(`/volunteers/${id}`)
}
