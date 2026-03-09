<script setup>
// import axios from "axios"
import apiClient from "@/services/api"
import { ref, onMounted } from "vue"

const message = ref("")
console.log(apiClient.defaults.baseURL)
onMounted(async () => {
  const response = await apiClient.get("/api/hello")

  message.value = response.data
})
</script>

<template>
  <div>
    {{ message }}
    hello world?
  </div>
</template>