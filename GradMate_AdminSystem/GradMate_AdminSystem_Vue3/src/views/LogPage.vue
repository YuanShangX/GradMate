<template>
  <section>
    <div class="page-heading">
      <div>
        <h2>系统日志</h2>
        <p>记录管理员登录、退出，以及新增、编辑、删除等关键操作。</p>
      </div>
      <div class="heading-actions">
        <el-button icon="Refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <section class="panel">
      <el-form class="query-bar log-query" :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="账号、模块、IP、结果信息" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="动作">
          <el-select v-model="query.actionType" clearable placeholder="动作类型">
            <el-option v-for="item in actionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源">
          <el-select v-model="query.resourceKey" clearable filterable placeholder="资源类型">
            <el-option v-for="item in resourceOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员">
          <el-input v-model="query.adminUsername" clearable placeholder="管理员账号" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="records" border stripe height="calc(100vh - 330px)">
        <el-table-column prop="id" label="ID" width="86" fixed />
        <el-table-column prop="adminUsername" label="管理员" min-width="120" show-overflow-tooltip />
        <el-table-column prop="moduleName" label="模块" min-width="130" show-overflow-tooltip />
        <el-table-column prop="actionType" label="动作" width="110">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.actionType)">{{ actionLabel(row.actionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceKey" label="资源" width="130" show-overflow-tooltip />
        <el-table-column prop="targetId" label="目标ID" width="110" show-overflow-tooltip />
        <el-table-column prop="resultStatus" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.resultStatus === 'SUCCESS' ? 'success' : 'danger'">
              {{ row.resultStatus === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP" min-width="130" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" min-width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="openDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="760px">
      <el-descriptions v-if="detail" :column="2" border class="log-detail">
        <el-descriptions-item label="日志ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="管理员">{{ detail.adminUsername || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ detail.moduleName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="动作">{{ actionLabel(detail.actionType) }}</el-descriptions-item>
        <el-descriptions-item label="资源">{{ detail.resourceKey || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标ID">{{ detail.targetId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ detail.requestMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="执行结果">
          {{ detail.resultStatus === 'SUCCESS' ? '成功' : '失败' }}
        </el-descriptions-item>
        <el-descriptions-item label="请求地址" :span="2">{{ detail.requestUri || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结果信息" :span="2">{{ detail.resultMessage || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detail.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="User-Agent" :span="2">{{ detail.userAgent || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import { flatMenus } from '../config/resources'

const loading = ref(false)
const detailVisible = ref(false)
const records = ref([])
const total = ref(0)
const detail = ref(null)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  actionType: '',
  resourceKey: '',
  adminUsername: ''
})

const actionOptions = [
  { label: '登录', value: 'LOGIN' },
  { label: '退出', value: 'LOGOUT' },
  { label: '新增', value: 'CREATE' },
  { label: '编辑', value: 'UPDATE' },
  { label: '删除数据', value: 'DELETE' },
  { label: '删除日志', value: 'DELETE_LOG' }
]

const resourceOptions = [
  { label: '登录认证', value: 'auth' },
  ...flatMenus
    .filter((item) => item.resource !== 'logs')
    .map((item) => ({ label: item.title, value: item.resource })),
  { label: '系统日志', value: 'logs' }
]

load()

async function load() {
  loading.value = true
  try {
    const params = {}
    Object.entries(query).forEach(([key, value]) => {
      if (value !== '' && value !== null && value !== undefined) params[key] = value
    })
    const data = await request.get('/logs', { params })
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
  Object.assign(query, {
    pageNum: 1,
    pageSize: 10,
    keyword: '',
    actionType: '',
    resourceKey: '',
    adminUsername: ''
  })
  load()
}

async function openDetail(row) {
  detail.value = await request.get(`/logs/${row.id}`)
  detailVisible.value = true
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除日志 ID ${row.id}？删除后会保留一条删除日志记录。`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await request.delete(`/logs/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

function actionLabel(value) {
  return actionOptions.find((item) => item.value === value)?.label || value || '-'
}

function actionTagType(value) {
  if (value === 'LOGIN' || value === 'LOGOUT') return 'info'
  if (value === 'CREATE') return 'success'
  if (value === 'UPDATE') return 'warning'
  return 'danger'
}
</script>
