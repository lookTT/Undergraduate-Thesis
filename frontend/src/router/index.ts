import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'volunteers',
          name: 'volunteers',
          component: () => import('@/views/VolunteerView.vue')
        },
        {
          path: 'activities',
          name: 'activities',
          component: () => import('@/views/ActivityView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  if (to.meta.public) return true
  const auth = useAuthStore()
  if (!auth.token) {
    return '/login'
  }
  if (!auth.user) {
    return auth.hydrateSession().then(() => {
      if (!auth.token) return '/login'
      return true
    })
  }
  return true
})

export default router
