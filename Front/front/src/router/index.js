import { createRouter, createWebHashHistory } from 'vue-router'
import FlightInfo from '../components/FlightInfo.vue'
import Login from '../components/Login.vue'

const routes = [
  {  
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
     path: '/',
    redirect: '/login'
  },
  {
    path: '/flight',
    name: 'FlightInfo',
    component: FlightInfo,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查用户是否已登录
    if (!localStorage.getItem('isLoggedIn')) {
      // 未登录则重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 已登录则正常进入
      next()
    }
  } else {
    // 不需要登录的路由正常进入
    next()
  }
})

export default router 