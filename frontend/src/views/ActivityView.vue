<template>
  <section class="page">
    <div class="page-head">
      <div>
        <p class="eyebrow">Activity</p>
        <h2>活动管理</h2>
      </div>
      <el-tag type="info" effect="plain">可供管理员和志愿者查看</el-tag>
    </div>

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="title" label="活动标题" min-width="180" />
      <el-table-column prop="location" label="地点" min-width="160" />
      <el-table-column label="开始时间" min-width="180">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" min-width="180">
        <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="recruitCount" label="招募人数" width="110" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusMeta[row.status]?.type || 'info'" effect="plain">
            {{ statusMeta[row.status]?.label || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="auth.hasRole('ADMIN') && row.status === 0" link type="primary" @click="publish(row.id)">
            发布
          </el-button>
          <span v-else class="muted">-</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="handleSizeChange"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getActivities, type ActivityVO } from '@/api/activity'
import { http } from '@/api/http'

const auth = useAuthStore()
const loading = ref(false)
const records = ref<ActivityVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusMeta: Record<number, { label: string; type: 'info' | 'success' | 'warning' }> = reactive({
  0: { label: '草稿', type: 'info' },
  1: { label: '已发布', type: 'success' },
  2: { label: '已结束', type: 'warning' }
})

function formatDateTime(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function loadData() {
  loading.value = true
  try {
    const { data } = await getActivities(pageNum.value, pageSize.value)
    if (data.code !== 0 || !data.data) {
      throw new Error(data.message || '加载活动列表失败')
    }
    records.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载活动列表失败')
  } finally {
    loading.value = false
  }
}

function handleSizeChange(size: number) {
  pageSize.value = size
  pageNum.value = 1
  loadData()
}

async function publish(id: number) {
  try {
    const { data } = await http.put(`/activities/${id}/publish`)
    if (data.code !== 0) {
      throw new Error(data.message || '发布失败')
    }
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '发布失败')
  }
}

onMounted(loadData)
</script>
