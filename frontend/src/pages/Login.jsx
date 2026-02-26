import { useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { t } = useLanguage();
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
        <h2>{t("login.title")}</h2>
        <p>{t("login.subtitle")}</p>
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
            {busy ? t("login.busy") : t("login.button")}
          </button>
        </form>
        <div className="auth-foot">
          <span>{t("login.footer")}</span>
          <NavLink to="/register">{t("login.footer.link")}</NavLink>
        </div>
      </div>
    </div>
  );
}
