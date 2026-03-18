export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageVO<T> {
  pageNum: number
  pageSize: number
  total: number
  records: T[]
}
