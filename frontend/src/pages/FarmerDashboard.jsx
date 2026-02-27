import { useEffect, useMemo, useState } from "react";
import { NavLink } from "react-router-dom";
import { fetchOpenContracts, joinContract } from "../api/contracts.js";
import { fetchContractExplanation } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";
import { EmptyState, InfoRow, StatCard, Tag, Spinner } from "../components/UI.jsx";
import { formatUnit, getReasonLabel } from "../utils/format.js";

const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

export default function FarmerDashboard() {
  const { token, user } = useAuth();
  const { t, lang } = useLanguage();
  const [contracts, setContracts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [selected, setSelected] = useState(null);
  const [commitQty, setCommitQty] = useState("");
  const [explain, setExplain] = useState(null);
  const [explainLoading, setExplainLoading] = useState(false);
  const [explainForm, setExplainForm] = useState({
    crop: "",
    state: "",
    harvestStartMonth: "",
    harvestEndMonth: ""
  });

  const stats = useMemo(() => {
    const open = contracts.filter((c) => c.status === "OPEN_FOR_ACCEPTANCE").length;
    return { open, total: contracts.length };
  }, [contracts]);

  useEffect(() => {
    async function loadOpen() {
      setLoading(true);
      try {
        const data = await fetchOpenContracts(token);
        setContracts(data || []);
      } catch (err) {
        setActionError(err.message || "Unable to load open contracts");
      } finally {
        setLoading(false);
      }
    }
    loadOpen();
  }, [token]);

  function pickContract(contract) {
    setSelected(contract);
    setCommitQty("");
    setExplain(null);
    setFieldErrors({});
    setExplainForm({
      crop: contract.cropName,
      state: contract.state,
      harvestStartMonth: "",
      harvestEndMonth: ""
    });
  }

  async function handleJoin() {
    if (!selected) return;
    setActionError("");
    try {
      const response = await joinContract(token, selected.id, {
        committedQuantity: Number(commitQty)
      });
      setContracts((prev) => prev.map((c) => (c.id === response.id ? response : c)));
      setSelected(response);
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        setActionError(err.message || "Unable to join contract");
      }
    }
  }

  async function handleExplain() {
    setActionError("");
    setExplain(null);
    setExplainLoading(true);
    try {
      const response = await fetchContractExplanation(token, explainForm);
      setExplain(response);
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        setActionError(err.message || "Unable to fetch explanation");
      }
    } finally {
      setExplainLoading(false);
    }
  }

  function updateExplainField(event) {
    const { name, value } = event.target;
    setExplainForm((prev) => ({ ...prev, [name]: value }));
    setFieldErrors({ ...fieldErrors, [name]: "" });
  }

  return (
    <div className="dashboard farmer">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">{t("farmer.eyebrow")}</p>
          <h2>{t("farmer.title")}, {user?.name || "Farmer"}</h2>
          <p>
            {t("farmer.subtitle")}
          </p>
        </div>
        <div className="stat-grid">
          <StatCard label={t("farmer.stats.open")} value={stats.open} />
          <StatCard label={t("farmer.stats.total")} value={stats.total} />
        </div>
      </section>

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>{t("farmer.card.open")}</h3>
            <Tag tone="earth">Pick one</Tag>
          </div>
          {loading ? (
            <Spinner text={t("text.loadingOpenContracts")} />
          ) : contracts.length === 0 ? (
            <EmptyState
              title={t("farmer.empty.title")}
              body={t("farmer.empty.body")}
            />
          ) : (
            <div className="contract-list">
              {contracts.map((contract) => (
                <button
                  key={contract.id}
                  className={`contract-list-item ${selected?.id === contract.id ? "active" : ""}`}
                  onClick={() => pickContract(contract)}
                >
                  <div>
                    <strong>{contract.cropName}</strong>
                    <span>{contract.state}</span>
                  </div>
                  <Tag tone="gold">{contract.status}</Tag>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <h3>{t("farmer.card.commit")}</h3>
            <Tag tone="mint">Secure</Tag>
          </div>
          {selected ? (
            <div className="info-stack">
              <InfoRow title={t("label.crop")} value={selected.cropName} />
              <InfoRow title={t("label.state")} value={selected.state} />
              <InfoRow title={t("label.requiredQty")} value={selected.requiredQuantity} />
              <InfoRow title={t("label.filledQty")} value={selected.filledQuantity} />
              <label>
                {t("label.commitQty")}
                <input
                  type="number"
                  value={commitQty}
                  onChange={(event) => {
                    setCommitQty(event.target.value);
                    setFieldErrors({ ...fieldErrors, committedQuantity: "" });
                  }}
                />
                {fieldErrors.committedQuantity && <div className="field-error">{fieldErrors.committedQuantity}</div>}
              </label>
              {selected.priceSnapshot && (
                <div className="snapshot-mini">
                  AI price: {selected.priceSnapshot.fairMinPrice} - {selected.priceSnapshot.fairMaxPrice}{" "}
                  {formatUnit(selected.priceSnapshot.unit, lang)}
                </div>
              )}
              {actionError && <div className="alert">{actionError}</div>}
              <button className="btn" onClick={handleJoin}>
                {t("farmer.button.commit")}
              </button>
              <NavLink className="btn ghost" to={`/contracts/${selected.id}`}>
                {t("farmer.button.view")}
              </NavLink>
            </div>
          ) : (
            <p className="muted">{t("text.selectContract")}</p>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <h3>{t("farmer.card.explain")}</h3>
            <Tag tone="earth">Transparent</Tag>
          </div>
          <div className="info-stack">
            <label>
              {t("label.crop")}
              <input name="crop" value={explainForm.crop} onChange={updateExplainField} />
              {fieldErrors.crop && <div className="field-error">{fieldErrors.crop}</div>}
            </label>
            <label>
              {t("label.state")}
              <input name="state" value={explainForm.state} onChange={updateExplainField} />
              {fieldErrors.state && <div className="field-error">{fieldErrors.state}</div>}
            </label>
            <label>
              {t("label.harvestStart")}
              <select
                name="harvestStartMonth"
                value={explainForm.harvestStartMonth}
                onChange={updateExplainField}
              >
                <option value="">{t("option.select")}</option>
                {monthOptions.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </label>
            <label>
              {t("label.harvestEnd")}
              <select
                name="harvestEndMonth"
                value={explainForm.harvestEndMonth}
                onChange={updateExplainField}
              >
                <option value="">{t("option.select")}</option>
                {monthOptions.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
              {fieldErrors.harvestEndMonth && <div className="field-error">{fieldErrors.harvestEndMonth}</div>}
            </label>
            <button className="btn ghost" onClick={handleExplain} disabled={explainLoading}>
              {explainLoading && <span className="loader" aria-hidden="true" />}
              {explainLoading ? t("farmer.button.explaining") : t("farmer.button.explain")}
            </button>
            {explain && (
              <div className="explain-box">
                <h4>{t("label.snapshot")}</h4>
                <p>
                  {t("label.fairRange")}: {explain.snapshot?.fairMinPrice || 0} - {explain.snapshot?.fairMaxPrice || 0}{" "}
                  {formatUnit(explain.snapshot?.unit, lang)}
                </p>
                <div className="tag-list">
                  {(explain.snapshot?.reasonCodes || []).map((code) => (
                    <Tag key={code} tone="earth">
                      {getReasonLabel(code, lang)}
                    </Tag>
                  ))}
                </div>
                <h4>{t("label.explanation")}</h4>
                <p>{explain.explanation}</p>
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
