import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
});

// Attach the JWT (if any) to every request.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('mv_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401 (expired/invalid token) drop the stale session so the UI
// falls back to guest state instead of failing silently.
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem('mv_token')) {
      localStorage.removeItem('mv_token');
      localStorage.removeItem('mv_user');
      window.dispatchEvent(new Event('mv-auth-changed'));
    }
    return Promise.reject(error);
  },
);

export default api;
