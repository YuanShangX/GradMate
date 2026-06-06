import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import AdminLayout from '../views/AdminLayout.vue'
import DashboardView from '../views/DashboardView.vue'
import ResourcePage from '../views/ResourcePage.vue'
import LogPage from '../views/LogPage.vue'
import { flatMenus } from '../config/resources'

const routes = [
  { path: '/login', component: LoginView },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: DashboardView, meta: { title: '仪表盘' } },
      ...flatMenus.map((item) => ({
        path: item.path.replace(/^\//, ''),
        component: item.resource === 'logs' ? LogPage : ResourcePage,
        meta: item
      }))
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('gradmate_admin_token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return '/dashboard'
  }
  return true
})

export default router
