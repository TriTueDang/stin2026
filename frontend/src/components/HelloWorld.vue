<script setup>
import axios from "axios"
import { ref, onMounted } from "vue"

const message = ref("")

onMounted(async () => {
  const response = await axios.get("http://localhost:8080/api/hello")
  message.value = response.data
})
</script>

<template>
  <div>
    {{ message }}
    hello world
  </div>
</template>