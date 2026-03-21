import axios from 'axios';

const apiClient = axios.create({
  // Vite automaticky dosadí správnou URL podle režimu
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('basicAuthToken');
  if (token) {
    config.headers.Authorization = `Basic ${token}`;
  }
  return config;
});

export default apiClient;