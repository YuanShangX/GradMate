<template>
  <section>
    <div class="page-heading">
      <div>
        <h2>仪表盘</h2>
        <p>查看 GradMate 当前数据库规模与管理入口。</p>
      </div>
      <el-button type="primary" icon="Refresh" @click="load">刷新</el-button>
    </div>

    <div class="stat-grid">
      <article v-for="card in cards" :key="card.key" class="stat-card">
        <div class="stat-icon" :class="card.key">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div>
          <span>{{ card.label }}</span>
          <strong>{{ stats[card.key] ?? 0 }}</strong>
        </div>
      </article>
    </div>

    <section class="panel">
      <div class="panel-title">管理建议</div>
      <el-alert show-icon type="info" :closable="false" title="删除院校和用户时系统会执行关联保护，避免误删影响小程序端数据。" />
      <el-alert show-icon type="warning" :closable="false" title="major 表数据量较大，请使用关键词和筛选条件分页维护。" />
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import request from '../api/request'

const stats = ref({})
const cards = computed(() => [
  { key: 'users', label: '用户数', icon: 'User' },
  { key: 'schools', label: '院校数', icon: 'School' },
  { key: 'majors', label: '专业数', icon: 'Collection' },
  { key: 'infos', label: '资讯数', icon: 'Document' },
  { key: 'studyRecords', label: '学习记录', icon: 'Reading' },
  { key: 'aiSessions', label: 'AI会话', icon: 'ChatDotRound' },
  { key: 'favorites', label: '收藏数', icon: 'Star' },
  { key: 'admins', label: '管理员', icon: 'Setting' }
])

async function load() {
  stats.value = await request.get('/dashboard/stats')
}

onMounted(load)
</script>
