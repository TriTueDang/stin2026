import { createRouter, createWebHistory } from 'vue-router'

import ExchangeRate from '@/components/ExchangeRate.vue'
import Login from '@/components/Login.vue'

const routes = [
  {
    path: '/login',
    component: Login
  },
  {
    path: '/',
    component: ExchangeRate,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && isAuthenticated) {
    next('/');
  } else {
    next();
  }
});

export default router
