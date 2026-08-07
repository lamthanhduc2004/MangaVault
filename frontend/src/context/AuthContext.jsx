import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { login as apiLogin } from '../services/authService';
import { getMe } from '../services/userService';

const AuthContext = createContext(null);

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem('mv_user');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

const storeUser = (user) => {
  localStorage.setItem('mv_user', JSON.stringify(user));
  return user;
};

export function AuthProvider({ children }) {
  // { id, username, displayName, avatarUrl, role } | null
  const [user, setUser] = useState(readStoredUser);

  // The api interceptor clears storage on 401 — keep React state in sync.
  useEffect(() => {
    const sync = () => setUser(readStoredUser());
    window.addEventListener('mv-auth-changed', sync);
    return () => window.removeEventListener('mv-auth-changed', sync);
  }, []);

  const login = useCallback(async (credentials) => {
    const { token, username, role } = await apiLogin(credentials);
    localStorage.setItem('mv_token', token);
    setUser(storeUser({ username, role }));

    // The login response is minimal; the profile call fills in display name and
    // avatar. A failure here must not break an otherwise successful sign-in.
    try {
      const profile = await getMe();
      setUser(storeUser({
        id: profile.id,
        username: profile.username,
        displayName: profile.displayName,
        avatarUrl: profile.avatarUrl,
        role: profile.role,
      }));
    } catch {
      // keep the minimal user
    }
  }, []);

  /** Called after the profile page saves, so the navbar updates immediately. */
  const refreshUser = useCallback(async () => {
    const profile = await getMe();
    setUser(storeUser({
      id: profile.id,
      username: profile.username,
      displayName: profile.displayName,
      avatarUrl: profile.avatarUrl,
      role: profile.role,
    }));
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('mv_token');
    localStorage.removeItem('mv_user');
    setUser(null);
  }, []);

  const value = { user, isAdmin: user?.role === 'ADMIN', login, logout, refreshUser };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
