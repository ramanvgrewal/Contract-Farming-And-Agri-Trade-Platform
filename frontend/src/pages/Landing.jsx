import { NavLink } from "react-router-dom";
import { useLanguage } from "../context/LanguageContext.jsx";

export default function Landing() {
  const { t } = useLanguage();
  return (
    <div className="landing">
      <section className="hero">
        <div className="hero-content">
          <p className="eyebrow">{t("hero.eyebrow")}</p>
          <h1>
            {t("hero.title")}
          </h1>
          <p className="lede">
            {t("hero.lede")}
          </p>
          <div className="hero-actions">
            <NavLink className="btn" to="/register">
              {t("hero.cta.buyer")}
            </NavLink>
            <NavLink className="btn ghost" to="/register">
              {t("hero.cta.farmer")}
            </NavLink>
          </div>
          <div className="hero-stats">
            <div>
              <strong>AI-driven</strong>
              <span>{t("hero.stat.ai")}</span>
            </div>
            <div>
              <strong>Secure</strong>
              <span>{t("hero.stat.secure")}</span>
            </div>
            <div>
              <strong>Seasonal</strong>
              <span>{t("hero.stat.seasonal")}</span>
            </div>
          </div>
        </div>
        <div className="hero-panel">
          <div className="hero-card">
            <h3>{t("hero.workflow.buyer")}</h3>
            <ul>
              <li>{t("hero.workflow.buyer.1")}</li>
              <li>{t("hero.workflow.buyer.2")}</li>
              <li>{t("hero.workflow.buyer.3")}</li>
            </ul>
          </div>
          <div className="hero-card farmer">
            <h3>{t("hero.workflow.farmer")}</h3>
            <ul>
              <li>{t("hero.workflow.farmer.1")}</li>
              <li>{t("hero.workflow.farmer.2")}</li>
              <li>{t("hero.workflow.farmer.3")}</li>
            </ul>
          </div>
        </div>
      </section>

      <section className="feature-grid">
        <div className="feature">
          <h3>{t("feature.explain.title")}</h3>
          <p>
            {t("feature.explain.body")}
          </p>
        </div>
        <div className="feature">
          <h3>{t("feature.market.title")}</h3>
          <p>
            {t("feature.market.body")}
          </p>
        </div>
        <div className="feature">
          <h3>{t("feature.location.title")}</h3>
          <p>
            {t("feature.location.body")}
          </p>
        </div>
      </section>
    </div>
  );
}
