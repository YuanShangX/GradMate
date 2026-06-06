import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api/admin',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('gradmate_admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code === 200) {
      return result.data
    }
    if (result.code === 401) {
      localStorage.removeItem('gradmate_admin_token')
      router.replace('/login')
    }
    ElMessage.error(result.message || '请求失败')
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
