import { useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

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
  const [form, setForm] = useState(emptyForm);
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
      await register(form);
      navigate("/app");
    } catch (err) {
      setError(err.message || "Unable to register");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card wide">
        <h2>Join the SavvyFARM network</h2>
        <p>Choose your role and start building confident crop agreements.</p>
        <form onSubmit={handleSubmit} className="grid-form">
          <label>
            Full name
            <input name="name" value={form.name} onChange={updateField} />
          </label>
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
          <label>
            Role
            <select name="role" value={form.role} onChange={updateField}>
              <option value="FARMER">Farmer</option>
              <option value="BUYER">Buyer</option>
            </select>
          </label>
          <label>
            Phone
            <input name="phone" value={form.phone} onChange={updateField} />
          </label>
          <label>
            Address
            <input name="address" value={form.address} onChange={updateField} />
          </label>
          <label>
            City
            <input name="city" value={form.city} onChange={updateField} />
          </label>
          <label>
            State
            <input name="state" value={form.state} onChange={updateField} />
          </label>
          {error && <div className="alert full">{error}</div>}
          <button className="btn full" disabled={busy}>
            {busy ? "Creating account..." : "Create account"}
          </button>
        </form>
        <div className="auth-foot">
          <span>Already registered?</span>
          <NavLink to="/login">Login</NavLink>
        </div>
      </div>
    </div>
  );
}
