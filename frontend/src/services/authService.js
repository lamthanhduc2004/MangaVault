import api from './api';

export async function register(payload) {
  const res = await api.post('/auth/register', payload);
  return res.data.result; // UserResponse
}

export async function login(payload) {
  const res = await api.post('/auth/login', payload);
  return res.data.result; // { token, username, role }
}
