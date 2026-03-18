<template>
  <el-container class="shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">V</div>
        <div>
          <div class="brand-title">志愿者管理</div>
          <div class="brand-subtitle">Community Service</div>
        </div>
      </div>
      <el-menu :default-active="route.path" class="menu" router>
        <el-menu-item index="/">仪表盘</el-menu-item>
        <el-menu-item index="/volunteers">志愿者</el-menu-item>
        <el-menu-item index="/activities">活动</el-menu-item>
      </el-menu>
    </aside>

    <el-container class="main">
      <el-header class="topbar">
        <div class="topbar-title">社区志愿者服务管理系统</div>
        <div class="topbar-user">
          <span>{{ auth.user?.realName || '未登录' }}</span>
          <el-button text :loading="loggingOut" @click="logout">退出</el-button>
        </div>
      </el-header>

      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout as logoutRequest } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loggingOut = ref(false)

async function logout() {
  loggingOut.value = true
  try {
    await logoutRequest()
  } catch {
    // 后端当前没有服务端会话，失败也直接清理本地 token。
  } finally {
    auth.clearSession()
    ElMessage.success('已退出登录')
    router.push('/login')
    loggingOut.value = false
  }
}
</script>
