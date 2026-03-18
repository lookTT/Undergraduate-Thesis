import { http } from './http'
import type { ApiResponse, PageVO } from './common'

export interface ActivityVO {
  id: number
  title: string
  content: string
  location: string
  startTime: string
  endTime: string
  recruitCount: number
  status: number
  creatorId: number
}

export function getActivities(pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<ActivityVO>>>('/activities', {
    params: { pageNum, pageSize }
  })
}
