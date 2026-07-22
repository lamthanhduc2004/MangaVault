import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { login as apiLogin } from '../services/authService';

const AuthContext = createContext(null);

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem('mv_user');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser); // { username, role } | null

  // The api interceptor clears storage on 401 — keep React state in sync.
  useEffect(() => {
    const sync = () => setUser(readStoredUser());
    window.addEventListener('mv-auth-changed', sync);
    return () => window.removeEventListener('mv-auth-changed', sync);
  }, []);

  const login = useCallback(async (credentials) => {
    const { token, username, role } = await apiLogin(credentials);
    localStorage.setItem('mv_token', token);
    localStorage.setItem('mv_user', JSON.stringify({ username, role }));
    setUser({ username, role });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('mv_token');
    localStorage.removeItem('mv_user');
    setUser(null);
  }, []);

  const value = { user, isAdmin: user?.role === 'ADMIN', login, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
