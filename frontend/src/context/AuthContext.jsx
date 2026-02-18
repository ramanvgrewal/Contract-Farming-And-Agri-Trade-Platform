import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { login as loginApi, register as registerApi } from "../api/auth.js";

const AuthContext = createContext(null);

function parseStored() {
  const raw = localStorage.getItem("savvyfarm.auth");
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = parseStored();
    if (stored?.token && stored?.user) {
      setToken(stored.token);
      setUser(stored.user);
    }
    setLoading(false);
  }, []);

  const value = useMemo(() => {
    return {
      user,
      token,
      loading,
      async login(payload) {
        const response = await loginApi(payload);
        setToken(response.token);
        setUser(response.user);
        localStorage.setItem("savvyfarm.auth", JSON.stringify(response));
        return response;
      },
      async register(payload) {
        const response = await registerApi(payload);
        setToken(response.token);
        setUser(response.user);
        localStorage.setItem("savvyfarm.auth", JSON.stringify(response));
        return response;
      },
      logout() {
        setToken(null);
        setUser(null);
        localStorage.removeItem("savvyfarm.auth");
      }
    };
  }, [user, token, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
