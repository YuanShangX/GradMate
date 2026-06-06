<template>
  <el-container class="admin-shell">
    <el-aside width="248px" class="sidebar">
      <div class="side-brand">
        <div class="side-logo">G</div>
        <div>
          <strong>GradMate</strong>
          <span>Admin Console</span>
        </div>
      </div>
      <el-menu
        router
        :default-active="$route.path"
        class="side-menu"
        background-color="#172033"
        text-color="#d7deea"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu v-for="group in menuGroups" :key="group.title" :index="group.title">
          <template #title>
            <el-icon><component :is="group.icon" /></el-icon>
            <span>{{ group.title }}</span>
          </template>
          <el-menu-item v-for="item in group.items" :key="item.path" :index="item.path">
            {{ item.title }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="profile">
          <el-icon><UserFilled /></el-icon>
          <span>{{ profile.nickname || profile.username || '管理员' }}</span>
          <el-button text type="primary" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { menuGroups } from '../config/resources'
import request from '../api/request'

const router = useRouter()
const profile = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('gradmate_admin_profile') || '{}')
  } catch {
    return {}
  }
})

async function logout() {
  try {
    await request.post('/auth/logout')
  } finally {
    localStorage.removeItem('gradmate_admin_token')
    localStorage.removeItem('gradmate_admin_profile')
    router.replace('/login')
  }
}
</script>
