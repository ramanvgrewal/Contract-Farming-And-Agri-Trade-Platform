import { useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";

const emptyForm = {
  name: "",
  email: "",
  password: "",
  role: "FARMER",
  phone: "",
  address: "",
  city: "",
  state: ""
};

export default function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const { t } = useLanguage();
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [busy, setBusy] = useState(false);

  function updateField(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
    setFieldErrors({ ...fieldErrors, [event.target.name]: "" });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setFieldErrors({});
    setBusy(true);
    try {
      await register(form);
      navigate("/app");
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        setError(err.message || "Unable to register");
      }
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card wide">
        <h2>{t("register.title")}</h2>
        <p>{t("register.subtitle")}</p>
        <form onSubmit={handleSubmit} className="grid-form">
          <label>
            Full name
            <input name="name" value={form.name} onChange={updateField} />
            {fieldErrors.name && <div className="field-error">{fieldErrors.name}</div>}
          </label>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={updateField} />
            {fieldErrors.email && <div className="field-error">{fieldErrors.email}</div>}
          </label>
          <label>
            Password
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={updateField}
            />
            {fieldErrors.password && <div className="field-error">{fieldErrors.password}</div>}
          </label>
          <label>
            Role
            <select name="role" value={form.role} onChange={updateField}>
              <option value="FARMER">Farmer</option>
              <option value="BUYER">Buyer</option>
            </select>
            {fieldErrors.role && <div className="field-error">{fieldErrors.role}</div>}
          </label>
          <label>
            Phone
            <input name="phone" value={form.phone} onChange={updateField} />
            {fieldErrors.phone && <div className="field-error">{fieldErrors.phone}</div>}
          </label>
          <label>
            Address
            <input name="address" value={form.address} onChange={updateField} />
            {fieldErrors.address && <div className="field-error">{fieldErrors.address}</div>}
          </label>
          <label>
            City
            <input name="city" value={form.city} onChange={updateField} />
            {fieldErrors.city && <div className="field-error">{fieldErrors.city}</div>}
          </label>
          <label>
            State
            <input name="state" value={form.state} onChange={updateField} />
            {fieldErrors.state && <div className="field-error">{fieldErrors.state}</div>}
          </label>
          {error && <div className="alert full">{error}</div>}
          <button className="btn full" disabled={busy}>
            {busy ? t("register.busy") : t("register.button")}
          </button>
        </form>
        <div className="auth-foot">
          <span>{t("register.footer")}</span>
          <NavLink to="/login">{t("register.footer.link")}</NavLink>
        </div>
      </div>
    </div>
  );
}
