import { createRouter, createWebHistory } from 'vue-router'

import HelloWorld from '@/components/HelloWorld.vue'
import ExchangeRate from '@/components/ExchangeRate.vue'

const routes = [
  {
    path: '/',
    component: ExchangeRate
    // component: HelloWorld
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
// import { createRouter, createWebHistory } from 'vue-router'

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes: [],
// })

// export default router
