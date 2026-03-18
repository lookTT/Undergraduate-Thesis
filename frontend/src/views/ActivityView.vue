<template>
  <section class="page">
    <div class="page-head">
      <div>
        <p class="eyebrow">Activity</p>
        <h2>活动管理</h2>
      </div>
      <div class="page-actions">
        <el-button v-if="auth.hasRole('ADMIN')" type="primary" @click="openCreate">新建活动</el-button>
        <el-tag type="info" effect="plain">报名、审核、签到在同页完成</el-tag>
      </div>
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
      <el-table-column label="操作" min-width="360">
        <template #default="{ row }">
          <el-button v-if="auth.hasRole('ADMIN')" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="auth.hasRole('ADMIN') && row.status === 0" link type="success" @click="publish(row.id)">
            发布
          </el-button>
          <el-button v-if="auth.hasRole('ADMIN')" link type="warning" @click="openDrawer(row, 'signup')">
            报名审核
          </el-button>
          <el-button v-if="auth.hasRole('ADMIN')" link type="info" @click="openDrawer(row, 'checkin')">
            签到记录
          </el-button>
          <el-button v-if="auth.hasRole('ADMIN')" link type="danger" @click="removeRow(row.id)">删除</el-button>

          <template v-if="auth.hasRole('VOLUNTEER') && row.status === 1">
            <el-button link type="primary" @click="signup(row.id)">报名</el-button>
            <el-button link type="success" @click="checkin(row.id)">签到</el-button>
          </template>

          <span v-if="!auth.hasRole('ADMIN') && (!auth.hasRole('VOLUNTEER') || row.status !== 1)" class="muted">-</span>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="formModel" :rules="rules" label-width="110px">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formModel.title" placeholder="请输入活动标题" />
        </el-form-item>

        <el-form-item label="活动内容" prop="content">
          <el-input v-model="formModel.content" type="textarea" :rows="4" placeholder="可选" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location">
              <el-input v-model="formModel.location" placeholder="请输入地点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招募人数" prop="recruitCount">
              <el-input-number
                v-model="formModel.recruitCount"
                :min="1"
                :max="9999"
                controls-position="right"
                class="w-full"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formModel.startTime"
                type="datetime"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="请选择开始时间"
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formModel.endTime"
                type="datetime"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="请选择结束时间"
                class="w-full"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="70%" destroy-on-close>
      <template #default>
        <div class="drawer-hero">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="标题">{{ activeActivity?.title }}</el-descriptions-item>
            <el-descriptions-item label="地点">{{ activeActivity?.location }}</el-descriptions-item>
            <el-descriptions-item label="时间">
              {{ formatDateTime(activeActivity?.startTime || '') }} - {{ formatDateTime(activeActivity?.endTime || '') }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusMeta[activeActivity?.status || 0]?.type || 'info'" effect="plain">
                {{ statusMeta[activeActivity?.status || 0]?.label || '未知' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-tabs v-model="drawerTab" @tab-change="handleDrawerTabChange">
          <el-tab-pane label="报名审核" name="signup">
            <div class="drawer-actions">
              <el-button type="primary" :loading="drawerLoading" @click="loadSignups">刷新</el-button>
            </div>
            <el-table v-loading="drawerLoading" :data="signupRecords" border>
              <el-table-column prop="id" label="报名ID" width="100" />
              <el-table-column prop="volunteerId" label="志愿者ID" width="120" />
              <el-table-column prop="applyTime" label="申请时间" min-width="180">
                <template #default="{ row }">{{ formatDateTime(row.applyTime) }}</template>
              </el-table-column>
              <el-table-column label="审核状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="signupStatusMeta[row.auditStatus]?.type || 'info'" effect="plain">
                    {{ signupStatusMeta[row.auditStatus]?.label || '未知' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="auditRemark" label="审核备注" min-width="150" show-overflow-tooltip />
              <el-table-column label="操作" width="180">
                <template #default="{ row }">
                  <el-button link type="success" :disabled="row.auditStatus !== 0" @click="approve(row.id)">
                    通过
                  </el-button>
                  <el-button link type="danger" :disabled="row.auditStatus !== 0" @click="reject(row.id)">
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="签到记录" name="checkin">
            <div class="drawer-actions">
              <el-button type="primary" :loading="drawerLoading" @click="loadCheckins">刷新</el-button>
            </div>
            <el-table v-loading="drawerLoading" :data="checkinRecords" border>
              <el-table-column prop="id" label="签到ID" width="100" />
              <el-table-column prop="volunteerId" label="志愿者ID" width="120" />
              <el-table-column prop="checkinTime" label="签到时间" min-width="180">
                <template #default="{ row }">{{ formatDateTime(row.checkinTime) }}</template>
              </el-table-column>
              <el-table-column label="签到状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="checkinStatusMeta[row.checkinStatus]?.type || 'info'" effect="plain">
                    {{ checkinStatusMeta[row.checkinStatus]?.label || '未知' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="checkinNote" label="备注" min-width="180" show-overflow-tooltip />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  approveSignup,
  checkinActivity,
  createActivity,
  deleteActivity,
  getActivityCheckins,
  getActivitySignups,
  getActivities,
  publishActivity,
  rejectSignup,
  signupActivity,
  updateActivity,
  type ActivityCheckinVO,
  type ActivitySaveRequest,
  type ActivitySignupVO,
  type ActivityVO
} from '@/api/activity'

const auth = useAuthStore()
const loading = ref(false)
const saving = ref(false)
const records = ref<ActivityVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const drawerVisible = ref(false)
const drawerLoading = ref(false)
const drawerTab = ref<'signup' | 'checkin'>('signup')
const activeActivity = ref<ActivityVO | null>(null)
const signupRecords = ref<ActivitySignupVO[]>([])
const checkinRecords = ref<ActivityCheckinVO[]>([])

const formModel = reactive<ActivitySaveRequest>({
  title: '',
  content: '',
  location: '',
  startTime: '',
  endTime: '',
  recruitCount: 1
})

const dialogTitle = computed(() => (editingId.value ? '编辑活动' : '新建活动'))
const drawerTitle = computed(() =>
  activeActivity.value ? `活动详情 - ${activeActivity.value.title}` : '活动详情'
)

const rules: FormRules = {
  title: [{ required: true, message: '活动标题不能为空', trigger: 'blur' }],
  location: [{ required: true, message: '活动地点不能为空', trigger: 'blur' }],
  startTime: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
  endTime: [{ required: true, message: '结束时间不能为空', trigger: 'change' }],
  recruitCount: [{ required: true, message: '招募人数不能为空', trigger: 'change' }]
}

const statusMeta: Record<number, { label: string; type: 'info' | 'success' | 'warning' }> = reactive({
  0: { label: '草稿', type: 'info' },
  1: { label: '已发布', type: 'success' },
  2: { label: '已结束', type: 'warning' }
})

const signupStatusMeta: Record<number, { label: string; type: 'info' | 'success' | 'danger' }> = reactive({
  0: { label: '待审核', type: 'info' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' }
})

const checkinStatusMeta: Record<number, { label: string; type: 'info' | 'success' | 'danger' }> = reactive({
  0: { label: '未签到', type: 'info' },
  1: { label: '已签到', type: 'success' }
})

function formatDateTime(value: string) {
  if (!value) return '-'
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

function resetForm() {
  Object.assign(formModel, {
    title: '',
    content: '',
    location: '',
    startTime: '',
    endTime: '',
    recruitCount: 1
  })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: ActivityVO) {
  editingId.value = row.id
  Object.assign(formModel, {
    title: row.title,
    content: row.content || '',
    location: row.location,
    startTime: row.startTime,
    endTime: row.endTime,
    recruitCount: row.recruitCount ?? 1
  })
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    const start = new Date(formModel.startTime)
    const end = new Date(formModel.endTime)
    if (end <= start) {
      ElMessage.error('结束时间必须晚于开始时间')
      return
    }
    saving.value = true
    try {
      const payload = {
        title: formModel.title.trim(),
        content: formModel.content || undefined,
        location: formModel.location.trim(),
        startTime: formModel.startTime,
        endTime: formModel.endTime,
        recruitCount: Number(formModel.recruitCount)
      }
      const request = editingId.value ? updateActivity(editingId.value, payload) : createActivity(payload)
      const { data } = await request
      if (data.code !== 0) {
        throw new Error(data.message || '保存失败')
      }
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      await loadData()
    } catch (error) {
      ElMessage.error(error instanceof Error ? error.message : '保存失败')
    } finally {
      saving.value = false
    }
  })
}

async function publish(id: number) {
  try {
    const { data } = await publishActivity(id)
    if (data.code !== 0) {
      throw new Error(data.message || '发布失败')
    }
    ElMessage.success('发布成功')
    await loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '发布失败')
  }
}

async function removeRow(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该活动吗？', '提示', { type: 'warning' })
    const { data } = await deleteActivity(id)
    if (data.code !== 0) {
      throw new Error(data.message || '删除失败')
    }
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error === 'cancel') return
    ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

async function signup(id: number) {
  try {
    await ElMessageBox.confirm('确认报名该活动吗？', '提示', { type: 'warning' })
    const { data } = await signupActivity(id)
    if (data.code !== 0) {
      throw new Error(data.message || '报名失败')
    }
    ElMessage.success('报名成功')
  } catch (error) {
    if (error === 'cancel') return
    ElMessage.error(error instanceof Error ? error.message : '报名失败')
  }
}

async function checkin(id: number) {
  try {
    await ElMessageBox.confirm('确认签到该活动吗？', '提示', { type: 'warning' })
    const { data } = await checkinActivity(id)
    if (data.code !== 0) {
      throw new Error(data.message || '签到失败')
    }
    ElMessage.success('签到成功')
  } catch (error) {
    if (error === 'cancel') return
    ElMessage.error(error instanceof Error ? error.message : '签到失败')
  }
}

async function openDrawer(row: ActivityVO, tab: 'signup' | 'checkin') {
  activeActivity.value = row
  drawerTab.value = tab
  drawerVisible.value = true
  await loadDrawerData(tab)
}

function handleDrawerTabChange(name: string | number) {
  const tab = name === 'checkin' ? 'checkin' : 'signup'
  loadDrawerData(tab)
}

async function loadDrawerData(tab: 'signup' | 'checkin' = drawerTab.value) {
  if (!activeActivity.value) return
  drawerLoading.value = true
  try {
    if (tab === 'signup') {
      const { data } = await getActivitySignups(activeActivity.value.id)
      if (data.code !== 0 || !data.data) {
        throw new Error(data.message || '加载报名记录失败')
      }
      signupRecords.value = data.data.records
    } else {
      const { data } = await getActivityCheckins(activeActivity.value.id)
      if (data.code !== 0 || !data.data) {
        throw new Error(data.message || '加载签到记录失败')
      }
      checkinRecords.value = data.data.records
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载明细失败')
  } finally {
    drawerLoading.value = false
  }
}

async function loadSignups() {
  drawerTab.value = 'signup'
  await loadDrawerData('signup')
}

async function loadCheckins() {
  drawerTab.value = 'checkin'
  await loadDrawerData('checkin')
}

async function approve(id: number) {
  try {
    const { data } = await approveSignup(id)
    if (data.code !== 0) {
      throw new Error(data.message || '审核失败')
    }
    ElMessage.success('已通过')
    await loadSignups()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审核失败')
  }
}

async function reject(id: number) {
  try {
    const { data } = await rejectSignup(id)
    if (data.code !== 0) {
      throw new Error(data.message || '审核失败')
    }
    ElMessage.success('已拒绝')
    await loadSignups()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审核失败')
  }
}

onMounted(loadData)
</script>
