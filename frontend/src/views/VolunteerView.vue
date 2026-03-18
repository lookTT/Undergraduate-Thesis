<template>
  <section class="page">
    <div class="page-head">
      <div>
        <p class="eyebrow">Volunteer</p>
        <h2>志愿者管理</h2>
      </div>
      <el-tag type="info" effect="plain">联调分页列表</el-tag>
    </div>

    <el-alert
      v-if="!auth.hasRole('ADMIN')"
      type="warning"
      show-icon
      :closable="false"
      title="当前账号没有志愿者管理权限。"
    />

    <el-table v-loading="loading" :data="records" border stripe>
      <el-table-column prop="studentOrMemberNo" label="学号/工号" min-width="140" />
      <el-table-column prop="gender" label="性别" width="90" />
      <el-table-column prop="age" label="年龄" width="90" />
      <el-table-column prop="communityName" label="所属社区" min-width="160" />
      <el-table-column prop="skillTag" label="技能标签" min-width="160" />
      <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
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
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getVolunteers, type VolunteerVO } from '@/api/volunteer'

const auth = useAuthStore()
const loading = ref(false)
const records = ref<VolunteerVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

async function loadData() {
  if (!auth.hasRole('ADMIN')) return
  loading.value = true
  try {
    const { data } = await getVolunteers(pageNum.value, pageSize.value)
    if (data.code !== 0 || !data.data) {
      throw new Error(data.message || '加载志愿者列表失败')
    }
    records.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载志愿者列表失败')
  } finally {
    loading.value = false
  }
}

function handleSizeChange(size: number) {
  pageSize.value = size
  pageNum.value = 1
  loadData()
}

onMounted(loadData)
</script>
