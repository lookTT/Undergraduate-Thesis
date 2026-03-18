import { http } from './http'
import type { ApiResponse, PageVO } from './common'

export interface VolunteerVO {
  id: number
  userId: number
  studentOrMemberNo: string
  gender: string
  age: number
  communityName: string
  skillTag: string
  remark: string
}

export function getVolunteers(pageNum = 1, pageSize = 10) {
  return http.get<ApiResponse<PageVO<VolunteerVO>>>('/volunteers', {
    params: { pageNum, pageSize }
  })
}
