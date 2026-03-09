import axios from 'axios';

const apiClient = axios.create({
  // Vite automaticky dosadí správnou URL podle režimu
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default apiClient;