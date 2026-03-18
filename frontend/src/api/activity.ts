import { http } from './http'
import type { ApiResponse, PageVO } from './common'

export interface ActivityVO {
  id: number
  title: string
  content: string
  location: string
  startTime: string
  endTime: string
  recruitCount: number | null
  status: number
  creatorId: number | null
}

export interface ActivitySaveRequest {
  title: string
  content?: string
  location: string
  startTime: string
  endTime: string
  recruitCount: number
}

export function getActivities(pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<ActivityVO>>>('/activities', {
    params: { pageNum, pageSize }
  })
}

export function getActivityById(id: number) {
  return http.get<ApiResponse<ActivityVO>>(`/activities/${id}`)
}

export function createActivity(data: ActivitySaveRequest) {
  return http.post<ApiResponse<null>>('/activities', data)
}

export function updateActivity(id: number, data: ActivitySaveRequest) {
  return http.put<ApiResponse<null>>(`/activities/${id}`, data)
}

export function deleteActivity(id: number) {
  return http.delete<ApiResponse<null>>(`/activities/${id}`)
}

export function publishActivity(id: number) {
  return http.put<ApiResponse<null>>(`/activities/${id}/publish`)
}

export interface ActivitySignupVO {
  id: number
  activityId: number
  volunteerId: number
  applyTime: string
  auditStatus: number
  auditTime: string | null
  auditRemark: string | null
}

export interface ActivityCheckinVO {
  id: number
  activityId: number
  volunteerId: number
  checkinTime: string
  checkinStatus: number
  checkinNote: string | null
}

export function signupActivity(id: number) {
  return http.post<ApiResponse<null>>(`/activities/${id}/signup`)
}

export function checkinActivity(id: number) {
  return http.post<ApiResponse<null>>(`/activities/${id}/checkin`)
}

export function getActivitySignups(id: number, pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<ActivitySignupVO>>>(`/activities/${id}/signups`, {
    params: { pageNum, pageSize }
  })
}

export function approveSignup(id: number) {
  return http.put<ApiResponse<null>>(`/signups/${id}/approve`)
}

export function rejectSignup(id: number) {
  return http.put<ApiResponse<null>>(`/signups/${id}/reject`)
}

export function getActivityCheckins(id: number, pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<ActivityCheckinVO>>>(`/activities/${id}/checkins`, {
    params: { pageNum, pageSize }
  })
}
