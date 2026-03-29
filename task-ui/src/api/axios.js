import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Automatically adds the JWT token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = token; // already stored as "Bearer <token>"
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;