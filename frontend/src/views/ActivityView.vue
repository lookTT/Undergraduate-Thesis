<template>
  <section class="page">
    <div class="page-head">
      <div>
        <p class="eyebrow">Activity</p>
        <h2>活动管理</h2>
      </div>
      <div class="page-actions">
        <el-button v-if="auth.hasRole('ADMIN')" type="primary" @click="openCreate">新建活动</el-button>
        <el-tag type="info" effect="plain">可供管理员和志愿者查看</el-tag>
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
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="auth.hasRole('ADMIN')" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="auth.hasRole('ADMIN') && row.status === 0" link type="success" @click="publish(row.id)">
            发布
          </el-button>
          <el-button v-if="auth.hasRole('ADMIN')" link type="danger" @click="removeRow(row.id)">删除</el-button>
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
              <el-input-number v-model="formModel.recruitCount" :min="1" :max="9999" controls-position="right" class="w-full" />
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
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  createActivity,
  deleteActivity,
  getActivities,
  publishActivity,
  updateActivity,
  type ActivitySaveRequest,
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
const formModel = reactive<ActivitySaveRequest>({
  title: '',
  content: '',
  location: '',
  startTime: '',
  endTime: '',
  recruitCount: 1
})

const dialogTitle = computed(() => (editingId.value ? '编辑活动' : '新建活动'))

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

onMounted(loadData)
</script>
