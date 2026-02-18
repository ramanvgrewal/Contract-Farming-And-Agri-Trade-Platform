import { useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  function updateField(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setBusy(true);
    try {
      await login(form);
      navigate("/app");
    } catch (err) {
      setError(err.message || "Unable to login");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>Welcome back</h2>
        <p>Log in to manage contracts, bids, and harvest schedules.</p>
        <form onSubmit={handleSubmit}>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={updateField} />
          </label>
          <label>
            Password
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={updateField}
            />
          </label>
          {error && <div className="alert">{error}</div>}
          <button className="btn" disabled={busy}>
            {busy ? "Signing in..." : "Login"}
          </button>
        </form>
        <div className="auth-foot">
          <span>New to SavvyFARM?</span>
          <NavLink to="/register">Create an account</NavLink>
        </div>
      </div>
    </div>
  );
}
