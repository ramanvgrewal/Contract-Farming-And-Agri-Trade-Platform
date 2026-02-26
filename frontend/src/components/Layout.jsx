import { Outlet, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";

export default function Layout() {
  const { user, logout } = useAuth();
  const { lang, setLang, t } = useLanguage();
  const navigate = useNavigate();
  const roleClass = user?.role ? `${String(user.role).toLowerCase()}-theme` : "";

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <div className={`app-shell ${roleClass}`}>
      <header className="top-nav">
        <div className="brand">
          <span className="brand-mark">SF</span>
          <div>
            <div className="brand-title">SavvyFARM</div>
            <div className="brand-tag">{t("brand.tag")}</div>
          </div>
        </div>
        <nav>
          <NavLink to="/" end>
            {t("nav.home")}
          </NavLink>
          {user && (
            <NavLink to="/app">
              {t("nav.dashboard")}
            </NavLink>
          )}
          {user && <NavLink to="/marketplace">{t("nav.marketplace")}</NavLink>}
        </nav>
        <div className="nav-actions">
          <div className="lang-toggle" role="group" aria-label={t("nav.lang")}>
            <button
              type="button"
              className={`lang-btn ${lang === "en" ? "active" : ""}`}
              onClick={() => setLang("en")}
            >
              EN
            </button>
            <button
              type="button"
              className={`lang-btn ${lang === "hi" ? "active" : ""}`}
              onClick={() => setLang("hi")}
            >
              HI
            </button>
          </div>
          {user ? (
            <>
              <div className="user-pill">
                {user.name || user.email}
                <span>{user.role}</span>
              </div>
              <button className="btn ghost" onClick={handleLogout}>
                {t("nav.logout")}
              </button>
            </>
          ) : (
            <>
              <NavLink className="btn ghost" to="/login">
                {t("nav.login")}
              </NavLink>
              <NavLink className="btn" to="/register">
                {t("nav.join")}
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
          {t("footer.line")}
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
