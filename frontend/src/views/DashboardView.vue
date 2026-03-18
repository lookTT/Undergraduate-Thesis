<template>
  <section class="page dashboard-page">
    <div class="dashboard-hero">
      <div>
        <p class="eyebrow">Dashboard</p>
        <h2>欢迎回来，{{ auth.user?.realName || '管理员' }}</h2>
        <p class="hero-text">
          当前系统用于管理志愿者档案、活动发布、报名审核与服务时长统计。这里先展示联调结果和关键指标。
        </p>
      </div>

      <div class="profile-card">
        <div class="profile-name">{{ auth.user?.realName || '未登录用户' }}</div>
        <div class="profile-meta">@{{ auth.user?.username || 'unknown' }}</div>
        <div class="profile-tags">
          <el-tag v-for="role in auth.user?.roles || []" :key="role" type="info" effect="plain">
            {{ role }}
          </el-tag>
        </div>
      </div>
    </div>

    <el-alert v-if="!auth.hasRole('ADMIN')" type="info" show-icon :closable="false" title="当前账号不是管理员，统计数据仅对管理员开放。" />

    <div v-if="auth.hasRole('ADMIN')" class="stats-grid">
      <el-card v-for="item in summaryCards" :key="item.label" class="stat-card" shadow="never">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-help">{{ item.help }}</div>
      </el-card>
    </div>

    <div class="dashboard-panel">
      <el-card shadow="never">
        <template #header>
          <div class="panel-header">
            <span>联调状态</span>
            <el-tag type="success" effect="plain">已接通后端</el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="后端地址">http://127.0.0.1:8080</el-descriptions-item>
          <el-descriptions-item label="前端代理">/api → backend</el-descriptions-item>
          <el-descriptions-item label="当前账号">{{ auth.user?.username || '未登录' }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ (auth.user?.roles || []).join(' / ') || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div class="panel-header">
            <span>功能范围</span>
          </div>
        </template>
        <ul class="feature-list">
          <li>志愿者档案管理</li>
          <li>活动发布与查询</li>
          <li>报名审核与签到</li>
          <li>服务时长统计</li>
        </ul>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getStatisticSummary, type StatisticSummary } from '@/api/statistics'

const auth = useAuthStore()
const summary = ref<StatisticSummary | null>(null)

const summaryCards = computed(() => [
  { label: '志愿者人数', value: summary.value?.volunteerCount ?? '-', help: '已录入的志愿者档案数量' },
  { label: '活动总数', value: summary.value?.activityCount ?? '-', help: '系统中创建的活动数量' },
  { label: '报名总数', value: summary.value?.signupCount ?? '-', help: '志愿者报名记录数量' },
  { label: '累计时长', value: summary.value?.serviceHours ?? '-', help: '服务记录统计的总时长' }
])

onMounted(async () => {
  if (!auth.hasRole('ADMIN')) return
  try {
    const { data } = await getStatisticSummary()
    if (data.code !== 0 || !data.data) {
      throw new Error(data.message || '加载统计失败')
    }
    summary.value = data.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载统计失败')
  }
})
</script>
