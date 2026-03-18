<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-copy">
        <h1>社区志愿者服务管理系统</h1>
        <p>用于管理志愿者信息、活动发布、报名审核与服务统计。</p>
      </div>

      <el-form :model="form" label-position="top" class="login-form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" class="login-button" :loading="loading" @click="onLogin">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})

async function onLogin() {
  loading.value = true
  try {
    const { data } = await login(form)
    if (data.code !== 0 || !data.data) {
      throw new Error(data.message || '登录失败')
    }
    auth.setSession(data.data.token, {
      userId: data.data.userId,
      username: data.data.username,
      realName: data.data.realName,
      roles: data.data.roles
    })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
