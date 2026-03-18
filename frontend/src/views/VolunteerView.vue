<template>
  <section class="page">
    <div class="page-head">
      <div>
        <p class="eyebrow">Volunteer</p>
        <h2>志愿者管理</h2>
      </div>
      <div class="page-actions">
        <el-button v-if="auth.hasRole('ADMIN')" type="primary" @click="openCreate">新增志愿者</el-button>
        <el-tag type="info" effect="plain">联调分页列表</el-tag>
      </div>
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
      <el-table-column v-if="auth.hasRole('ADMIN')" label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="removeRow(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="formModel" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input-number v-model="formModel.userId" :min="1" controls-position="right" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号/工号" prop="studentOrMemberNo">
              <el-input v-model="formModel.studentOrMemberNo" placeholder="请输入学号或工号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formModel.gender" placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="formModel.age" :min="1" :max="120" controls-position="right" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属社区" prop="communityName">
              <el-input v-model="formModel.communityName" placeholder="请输入社区名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="技能标签" prop="skillTag">
          <el-input v-model="formModel.skillTag" placeholder="如：急救、摄影、文案" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="formModel.remark" type="textarea" :rows="4" placeholder="可选" />
        </el-form-item>
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
  createVolunteer,
  deleteVolunteer,
  getVolunteers,
  updateVolunteer,
  type VolunteerSaveRequest,
  type VolunteerVO
} from '@/api/volunteer'

const auth = useAuthStore()
const loading = ref(false)
const saving = ref(false)
const records = ref<VolunteerVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const formModel = reactive<VolunteerSaveRequest>({
  userId: null,
  studentOrMemberNo: '',
  gender: '',
  age: null,
  communityName: '',
  skillTag: '',
  remark: ''
})

const dialogTitle = computed(() => (editingId.value ? '编辑志愿者' : '新增志愿者'))

const rules: FormRules = {
  studentOrMemberNo: [{ required: true, message: '学号/工号不能为空', trigger: 'blur' }]
}

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

function resetForm() {
  Object.assign(formModel, {
    userId: null,
    studentOrMemberNo: '',
    gender: '',
    age: null,
    communityName: '',
    skillTag: '',
    remark: ''
  })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: VolunteerVO) {
  editingId.value = row.id
  Object.assign(formModel, {
    userId: row.userId,
    studentOrMemberNo: row.studentOrMemberNo,
    gender: row.gender,
    age: row.age,
    communityName: row.communityName,
    skillTag: row.skillTag,
    remark: row.remark
  })
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const payload = {
        userId: formModel.userId ?? null,
        studentOrMemberNo: formModel.studentOrMemberNo.trim(),
        gender: formModel.gender || undefined,
        age: formModel.age ?? null,
        communityName: formModel.communityName || undefined,
        skillTag: formModel.skillTag || undefined,
        remark: formModel.remark || undefined
      }
      const request = editingId.value ? updateVolunteer(editingId.value, payload) : createVolunteer(payload)
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

async function removeRow(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该志愿者档案吗？', '提示', { type: 'warning' })
    const { data } = await deleteVolunteer(id)
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
