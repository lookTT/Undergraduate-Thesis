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
