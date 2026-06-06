<template>
  <main class="login-page">
    <section class="login-panel">
      <div class="brand">
        <div class="brand-mark">G</div>
        <div>
          <h1>GradMate 管理系统</h1>
          <p>数据库信息维护后台</p>
        </div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="login-form" @keyup.enter="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="管理员账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="login-button" @click="submit">
          登录
        </el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await request.post('/auth/login', form)
    localStorage.setItem('gradmate_admin_token', data.token)
    localStorage.setItem('gradmate_admin_profile', JSON.stringify(data.admin))
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>
