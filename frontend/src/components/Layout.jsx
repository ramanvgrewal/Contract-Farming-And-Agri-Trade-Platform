import { Outlet, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <div className="app-shell">
      <header className="top-nav">
        <div className="brand">
          <span className="brand-mark">SF</span>
          <div>
            <div className="brand-title">SavvyFARM</div>
            <div className="brand-tag">Contracts that feel fair</div>
          </div>
        </div>
        <nav>
          <NavLink to="/" end>
            Home
          </NavLink>
          {user && (
            <NavLink to="/app">
              Dashboard
            </NavLink>
          )}
          {user && <NavLink to="/marketplace">Marketplace</NavLink>}
        </nav>
        <div className="nav-actions">
          {user ? (
            <>
              <div className="user-pill">
                {user.name || user.email}
                <span>{user.role}</span>
              </div>
              <button className="btn ghost" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <NavLink className="btn ghost" to="/login">
                Login
              </NavLink>
              <NavLink className="btn" to="/register">
                Join SavvyFARM
              </NavLink>
            </>
          )}
        </div>
      </header>
      <main>
        <Outlet />
      </main>
      <footer className="footer">
        <div>
          Built for farmers and buyers. Transparent prices, confident decisions.
        </div>
        <div className="footer-links">
          <span>Support</span>
          <span>Pricing</span>
          <span>Community</span>
        </div>
      </footer>
    </div>
  );
}
