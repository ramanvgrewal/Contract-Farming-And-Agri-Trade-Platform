import { useEffect, useMemo, useState } from "react";
import { NavLink } from "react-router-dom";
import { createContract, fetchBuyerContracts, openContract } from "../api/contracts.js";
import { fetchContractSnapshot, fetchContractExplanation } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";
import { EmptyState, InfoRow, StatCard, Tag } from "../components/UI.jsx";
import { formatUnit, getReasonLabel } from "../utils/format.js";

const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

export default function BuyerDashboard() {
  const { token, user } = useAuth();
  const { t, lang } = useLanguage();
  const [contracts, setContracts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState("");
  const [busy, setBusy] = useState(false);
  const [snapshot, setSnapshot] = useState(null);
  const [explain, setExplain] = useState(null);
  const [explainLoading, setExplainLoading] = useState(false);

  const [form, setForm] = useState({
    cropName: "",
    state: "",
    cropVariety: "",
    requiredQuantity: "",
    priceType: "RANGE",
    offeredPriceMin: "",
    offeredPriceMax: "",
    harvestStartMonth: "",
    harvestEndMonth: "",
    harvestDate: "",
    locationRequired: false,
    locationLat: "",
    locationLng: "",
    locationRadiusKm: "",
    clusterEnabled: false
  });

  const contractStats = useMemo(() => {
    const total = contracts.length;
    const open = contracts.filter((c) => c.status === "OPEN_FOR_ACCEPTANCE").length;
    const locked = contracts.filter((c) => c.status === "LOCKED").length;
    return { total, open, locked };
  }, [contracts]);

  useEffect(() => {
    async function loadContracts() {
      setLoading(true);
      try {
        const data = await fetchBuyerContracts(token);
        setContracts(data || []);
      } catch (err) {
        setActionError(err.message || "Unable to load contracts");
      } finally {
        setLoading(false);
      }
    }
    loadContracts();
  }, [token]);

  function updateForm(event) {
    const { name, value, type, checked } = event.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  }

  async function handleCreate(event) {
    event.preventDefault();
    setActionError("");
    setBusy(true);
    try {
      const payload = {
        ...form,
        requiredQuantity: Number(form.requiredQuantity),
        harvestStartMonth: Number(form.harvestStartMonth),
        harvestEndMonth: Number(form.harvestEndMonth),
        offeredPriceMin: form.offeredPriceMin ? Number(form.offeredPriceMin) : null,
        offeredPriceMax: form.offeredPriceMax ? Number(form.offeredPriceMax) : null,
        locationLat: form.locationLat ? Number(form.locationLat) : null,
        locationLng: form.locationLng ? Number(form.locationLng) : null,
        locationRadiusKm: form.locationRadiusKm ? Number(form.locationRadiusKm) : null
      };

      if (payload.priceType === "FIXED" && payload.offeredPriceMin && !payload.offeredPriceMax) {
        payload.offeredPriceMax = payload.offeredPriceMin;
      }

      const response = await createContract(token, payload);
      setContracts((prev) => [response, ...prev]);
    } catch (err) {
      setActionError(err.message || "Unable to create contract");
    } finally {
      setBusy(false);
    }
  }

  async function handleOpen(contractId) {
    setActionError("");
    try {
      const response = await openContract(token, contractId);
      setContracts((prev) => prev.map((c) => (c.id === response.id ? response : c)));
    } catch (err) {
      setActionError(err.message || "Unable to open contract");
    }
  }

  async function handleSnapshot() {
    setActionError("");
    setSnapshot(null);
    try {
      const response = await fetchContractSnapshot(token, {
        crop: form.cropName,
        state: form.state,
        harvestStartMonth: form.harvestStartMonth,
        harvestEndMonth: form.harvestEndMonth
      });
      setSnapshot(response);
    } catch (err) {
      setActionError(err.message || "Unable to fetch snapshot");
    }
  }

  async function handleExplain() {
    setActionError("");
    setExplain(null);
    setExplainLoading(true);
    try {
      const response = await fetchContractExplanation(token, {
        crop: form.cropName,
        state: form.state,
        harvestStartMonth: form.harvestStartMonth,
        harvestEndMonth: form.harvestEndMonth
      });
      setExplain(response);
    } catch (err) {
      setActionError(err.message || "Unable to fetch explanation");
    } finally {
      setExplainLoading(false);
    }
  }

  return (
    <div className="dashboard buyer">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">{t("buyer.eyebrow")}</p>
          <h2>{t("buyer.title")}, {user?.name || "Buyer"}</h2>
          <p>
            {t("buyer.subtitle")}
          </p>
        </div>
        <div className="stat-grid">
          <StatCard label={t("buyer.stats.total")} value={contractStats.total} />
          <StatCard label={t("buyer.stats.open")} value={contractStats.open} />
          <StatCard label={t("buyer.stats.locked")} value={contractStats.locked} />
        </div>
      </section>

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>{t("buyer.card.create")}</h3>
            <Tag tone="gold">AI guided</Tag>
          </div>
          <form className="grid-form" onSubmit={handleCreate}>
            <label>
              {t("label.cropName")}
              <input name="cropName" value={form.cropName} onChange={updateForm} />
            </label>
            <label>
              {t("label.state")}
              <input name="state" value={form.state} onChange={updateForm} />
            </label>
            <label>
              {t("label.variety")}
              <input name="cropVariety" value={form.cropVariety} onChange={updateForm} />
            </label>
            <label>
              {t("label.requiredQty")}
              <input
                name="requiredQuantity"
                type="number"
                value={form.requiredQuantity}
                onChange={updateForm}
              />
            </label>
            <label>
              {t("label.priceType")}
              <select name="priceType" value={form.priceType} onChange={updateForm}>
                <option value="RANGE">{t("option.range")}</option>
                <option value="FIXED">{t("option.fixed")}</option>
              </select>
            </label>
            <label>
              {t("label.priceMin")}
              <input
                name="offeredPriceMin"
                type="number"
                value={form.offeredPriceMin}
                onChange={updateForm}
              />
            </label>
            {form.priceType === "RANGE" && (
              <label>
                {t("label.priceMax")}
                <input
                  name="offeredPriceMax"
                  type="number"
                  value={form.offeredPriceMax}
                  onChange={updateForm}
                />
              </label>
            )}
            <label>
              {t("label.harvestStart")}
              <select
                name="harvestStartMonth"
                value={form.harvestStartMonth}
                onChange={updateForm}
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
                value={form.harvestEndMonth}
                onChange={updateForm}
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
              {t("label.harvestDate")}
              <input
                name="harvestDate"
                type="date"
                value={form.harvestDate}
                onChange={updateForm}
              />
            </label>
            <label className="checkbox">
              <input
                type="checkbox"
                name="locationRequired"
                checked={form.locationRequired}
                onChange={updateForm}
              />
              {t("label.locationRequired")}
            </label>
            {form.locationRequired && (
              <>
                <label>
                  {t("label.latitude")}
                  <input
                    name="locationLat"
                    type="number"
                    value={form.locationLat}
                    onChange={updateForm}
                  />
                </label>
                <label>
                  {t("label.longitude")}
                  <input
                    name="locationLng"
                    type="number"
                    value={form.locationLng}
                    onChange={updateForm}
                  />
                </label>
                <label>
                  {t("label.radius")}
                  <input
                    name="locationRadiusKm"
                    type="number"
                    value={form.locationRadiusKm}
                    onChange={updateForm}
                  />
                </label>
              </>
            )}
            <label className="checkbox">
              <input
                type="checkbox"
                name="clusterEnabled"
                checked={form.clusterEnabled}
                onChange={updateForm}
              />
              {t("label.cluster")}
            </label>
            {actionError && <div className="alert full">{actionError}</div>}
            <div className="form-actions full">
              <button type="button" className="btn ghost" onClick={handleSnapshot}>
                {t("buyer.button.preview")}
              </button>
              <button type="button" className="btn ghost" onClick={handleExplain} disabled={explainLoading}>
                {explainLoading && <span className="loader" aria-hidden="true" />}
                {explainLoading ? t("buyer.button.explaining") : t("buyer.button.explain")}
              </button>
              <button className="btn" disabled={busy}>
                {busy ? t("buyer.button.creating") : t("buyer.button.create")}
              </button>
            </div>
          </form>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>{t("buyer.card.insight")}</h3>
            <Tag tone="mint">Explain</Tag>
          </div>
          <div className="info-stack">
            {snapshot ? (
              <>
                <InfoRow
                  title={t("label.fairMin")}
                  value={`${snapshot.fairMinPrice || 0} ${formatUnit(snapshot.unit, lang)}`}
                />
                <InfoRow
                  title={t("label.fairMax")}
                  value={`${snapshot.fairMaxPrice || 0} ${formatUnit(snapshot.unit, lang)}`}
                />
                <InfoRow title={t("label.confidence")} value={snapshot.confidence || 0} />
                <div className="tag-list">
                  {(snapshot.reasonCodes || []).map((code) => (
                    <Tag key={code} tone="earth">
                      {getReasonLabel(code, lang)}
                    </Tag>
                  ))}
                </div>
              </>
            ) : (
              <p className="muted">{t("text.previewHint")}</p>
            )}
            {explain && (
              <div className="explain-box">
                <h4>{t("label.explanation")}</h4>
                <p>{explain.explanation}</p>
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="card">
        <div className="card-header">
          <h3>{t("buyer.card.contracts")}</h3>
          <Tag tone="earth">Track</Tag>
        </div>
        {loading ? (
          <p className="muted">{t("text.loadingContracts")}</p>
        ) : contracts.length === 0 ? (
          <EmptyState
            title={t("buyer.empty.title")}
            body={t("buyer.empty.body")}
            action={<NavLink to="/buyer" className="btn ghost">{t("buyer.empty.action")}</NavLink>}
          />
        ) : (
          <div className="contract-grid">
            {contracts.map((contract) => (
              <div key={contract.id} className="contract-card">
                <div className="contract-head">
                  <div>
                    <h4>{contract.cropName}</h4>
                    <span>{contract.state}</span>
                  </div>
                  <Tag tone={contract.status === "LOCKED" ? "mint" : "gold"}>
                    {contract.status}
                  </Tag>
                </div>
                <div className="contract-meta">
                  <InfoRow title={t("label.requiredQty")} value={contract.requiredQuantity} />
                  <InfoRow title={t("label.filledQty")} value={contract.filledQuantity} />
                  <InfoRow title={t("label.priceType")} value={contract.priceType} />
                </div>
                {contract.priceSnapshot && (
                  <div className="snapshot-mini">
                    AI {contract.priceSnapshot.fairMinPrice} - {contract.priceSnapshot.fairMaxPrice}{" "}
                    {formatUnit(contract.priceSnapshot.unit, lang)}
                  </div>
                )}
                <div className="contract-actions">
                  <NavLink to={`/contracts/${contract.id}`} className="btn ghost">
                    View
                  </NavLink>
                  {contract.status === "CREATED" && (
                    <button className="btn" onClick={() => handleOpen(contract.id)}>
                      Open for farmers
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

