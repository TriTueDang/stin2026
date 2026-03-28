<template>
  <div class="login-container modern-theme">
    <div class="login-box card">
      <h2>Přihlášení</h2>
      <p class="subtitle">Currency Analyzer</p>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label>Uživatelské jméno</label>
          <input type="text" v-model="username" class="modern-input" required autofocus />
        </div>
        <div class="form-group">
          <label>Heslo</label>
          <input type="password" v-model="password" class="modern-input" required />
        </div>

        <button type="submit" class="main-btn w-full mt-4">Přihlásit se</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/services/api';

const router = useRouter();
const username = ref('admin');
const password = ref('admin');
const errorMsg = ref('');

const handleLogin = async () => {
  const token = btoa(`${username.value}:${password.value}`);
  try {
    // Validates login via backend
    await apiClient.post(`/api/rates/current`, {
      base: 'EUR',
      watched: ['USD']
    }, {
      headers: { Authorization: `Basic ${token}` }
    });
    localStorage.setItem('isAuthenticated', 'true');
    localStorage.setItem('user', username.value);
    localStorage.setItem('basicAuthToken', token);
    router.push('/');
  } catch(e) {
    if (e.response && e.response.status === 401) {
      errorMsg.value = 'Nesprávné uživatelské jméno nebo heslo.';
    } else {
      errorMsg.value = 'Chyba připojení k serveru.';
    }
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #111827;
  color: #f3f4f6;
  font-family: 'Inter', sans-serif;
}
.login-box {
  background: #1f2937;
  padding: 40px;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
  box-shadow: 0 10px 25px rgba(0,0,0,0.5);
  border: 1px solid #374151;
}
.login-box h2 { margin: 0; font-size: 1.8rem; color: #f9fafb; text-align: center; }
.subtitle { text-align: center; color: #9ca3af; margin-bottom: 24px; margin-top: 8px; font-weight: 500;}
.form-group { display: flex; flex-direction: column; gap: 8px; margin-bottom: 16px; }
.form-group label { color: #9ca3af; font-size: 0.9rem; }
.modern-input {
  width: 100%; padding: 12px; border-radius: 8px; border: 1px solid #4b5563;
  background-color: #374151; color: #f3f4f6; font-size: 1rem; outline: none; box-sizing: border-box;
}
.modern-input:focus { border-color: #3b82f6; }
.main-btn {
  background: #2563eb; color: white; border: none; padding: 12px; border-radius: 8px;
  font-weight: 600; cursor: pointer; transition: 0.2s; font-size: 1rem;
}
.main-btn:hover { background: #1d4ed8; }
.w-full { width: 100%; box-sizing: border-box;}
.mt-4 { margin-top: 24px; }
.error-msg { background: rgba(239,68,68,0.1); color: #ef4444; border: 1px solid #ef4444; padding: 12px; border-radius: 8px; margin-bottom: 16px; text-align: center;}
</style>
