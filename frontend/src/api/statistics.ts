import { http } from './http'
import type { ApiResponse } from './common'

export interface StatisticSummary {
  volunteerCount: number
  activityCount: number
  signupCount: number
  serviceHours: number
}

export function getStatisticSummary() {
  return http.get<ApiResponse<StatisticSummary>>('/statistics/summary')
}
