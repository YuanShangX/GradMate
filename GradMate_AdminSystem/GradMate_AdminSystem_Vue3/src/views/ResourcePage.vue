<template>
  <section>
    <div class="page-heading">
      <div>
        <h2>{{ config.title }}</h2>
        <p>支持分页搜索、筛选、新增、编辑和关联保护删除。</p>
      </div>
      <div class="heading-actions">
        <el-button icon="Refresh" @click="load">刷新</el-button>
        <el-button type="primary" icon="Plus" @click="openCreate">新增</el-button>
      </div>
    </div>

    <section class="panel">
      <el-form class="query-bar" :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="搜索关键词" @keyup.enter="search" />
        </el-form-item>
        <el-form-item v-for="filter in config.filters" :key="filter" :label="filterLabel(filter)">
          <el-input v-model="query[filter]" clearable :placeholder="filterLabel(filter)" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="records" border stripe height="calc(100vh - 330px)">
        <el-table-column prop="id" label="ID" width="86" fixed />
        <el-table-column
          v-for="field in visibleFields"
          :key="field.prop"
          :prop="field.prop"
          :label="field.label"
          min-width="150"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <el-tag v-if="field.type === 'switch'" :type="Number(row[field.prop]) ? 'success' : 'info'">
              {{ Number(row[field.prop]) ? '是' : '否' }}
            </el-tag>
            <span v-else>{{ row[field.prop] }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="168" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          @current-change="load"
          @size-change="load"
        />
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="edit-form">
        <el-form-item v-for="field in config.fields" :key="field.prop" :label="field.label" :prop="field.prop">
          <el-switch v-if="field.type === 'switch'" v-model="form[field.prop]" :active-value="1" :inactive-value="0" />
          <el-input-number v-else-if="field.type === 'number'" v-model="form[field.prop]" :min="0" controls-position="right" />
          <el-date-picker v-else-if="field.type === 'date'" v-model="form[field.prop]" value-format="YYYY-MM-DD" type="date" />
          <el-date-picker v-else-if="field.type === 'datetime'" v-model="form[field.prop]" value-format="YYYY-MM-DD HH:mm:ss" type="datetime" />
          <el-input v-else-if="field.type === 'textarea'" v-model="form[field.prop]" type="textarea" :rows="4" />
          <el-input v-else-if="field.type === 'password'" v-model="form[field.prop]" type="password" show-password placeholder="编辑时留空表示不修改" />
          <el-input v-else v-model="form[field.prop]" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'

const route = useRoute()
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const records = ref([])
const total = ref(0)
const form = reactive({})
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const config = computed(() => route.meta)
const visibleFields = computed(() => config.value.fields.slice(0, 8))
const dialogTitle = computed(() => `${editingId.value ? '编辑' : '新增'}${config.value.title}`)
const rules = computed(() => Object.fromEntries(config.value.fields.filter((f) => f.required).map((f) => [f.prop, [{ required: true, message: `请输入${f.label}`, trigger: 'blur' }]])))

watch(() => route.path, () => reset(), { immediate: true })

function filterLabel(filter) {
  const found = config.value.fields.find((f) => toFilterName(f.prop) === filter || f.prop === filter)
  return found?.label || filter
}

function toFilterName(prop) {
  return prop.replace(/_([a-z])/g, (_, c) => c.toUpperCase())
}

async function load() {
  loading.value = true
  try {
    const params = {}
    Object.entries(query).forEach(([key, value]) => {
      if (value !== '' && value !== null && value !== undefined) params[key] = value
    })
    const data = await request.get(config.value.apiPath, { params })
    records.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function search() {
  query.pageNum = 1
  load()
}

function reset() {
  Object.keys(query).forEach((key) => delete query[key])
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '' })
  config.value.filters?.forEach((filter) => (query[filter] = ''))
  load()
}

function openCreate() {
  editingId.value = null
  fillForm({})
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  fillForm(row)
  if (config.value.resource === 'admins') {
    form.password = ''
  }
  dialogVisible.value = true
}

function fillForm(row) {
  Object.keys(form).forEach((key) => delete form[key])
  config.value.fields.forEach((field) => {
    form[field.prop] = row[field.prop] ?? defaultValue(field)
  })
}

function defaultValue(field) {
  if (field.type === 'switch') return 0
  if (field.type === 'number') return null
  return ''
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { ...form }
    Object.keys(payload).forEach((key) => {
      if (payload[key] === '') payload[key] = null
    })
    if (editingId.value) {
      await request.put(`${config.value.apiPath}/${editingId.value}`, payload)
      ElMessage.success('更新成功')
    } else {
      await request.post(config.value.apiPath, payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除 ID ${row.id}？有关联数据时系统会自动阻止。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await request.delete(`${config.value.apiPath}/${row.id}`)
  ElMessage.success('删除成功')
  load()
}
</script>
