import { useEffect, useMemo, useState } from "react";
import { NavLink } from "react-router-dom";
import { createContract, fetchBuyerContracts, openContract } from "../api/contracts.js";
import { fetchContractSnapshot, fetchContractExplanation } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { EmptyState, InfoRow, StatCard, Tag } from "../components/UI.jsx";

const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

export default function BuyerDashboard() {
  const { token, user } = useAuth();
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
          <p className="eyebrow">Buyer workspace</p>
          <h2>Welcome, {user?.name || "Buyer"}</h2>
          <p>
            Draft contracts, open them for farmers, and use AI price explanation
            before you lock anything in.
          </p>
        </div>
        <div className="stat-grid">
          <StatCard label="Total contracts" value={contractStats.total} />
          <StatCard label="Open for acceptance" value={contractStats.open} />
          <StatCard label="Locked" value={contractStats.locked} />
        </div>
      </section>

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>Create a contract</h3>
            <Tag tone="gold">AI guided</Tag>
          </div>
          <form className="grid-form" onSubmit={handleCreate}>
            <label>
              Crop name
              <input name="cropName" value={form.cropName} onChange={updateForm} />
            </label>
            <label>
              State
              <input name="state" value={form.state} onChange={updateForm} />
            </label>
            <label>
              Variety
              <input name="cropVariety" value={form.cropVariety} onChange={updateForm} />
            </label>
            <label>
              Required quantity
              <input
                name="requiredQuantity"
                type="number"
                value={form.requiredQuantity}
                onChange={updateForm}
              />
            </label>
            <label>
              Price type
              <select name="priceType" value={form.priceType} onChange={updateForm}>
                <option value="RANGE">Range</option>
                <option value="FIXED">Fixed</option>
              </select>
            </label>
            <label>
              Offered min price
              <input
                name="offeredPriceMin"
                type="number"
                value={form.offeredPriceMin}
                onChange={updateForm}
              />
            </label>
            {form.priceType === "RANGE" && (
              <label>
                Offered max price
                <input
                  name="offeredPriceMax"
                  type="number"
                  value={form.offeredPriceMax}
                  onChange={updateForm}
                />
              </label>
            )}
            <label>
              Harvest start month
              <select
                name="harvestStartMonth"
                value={form.harvestStartMonth}
                onChange={updateForm}
              >
                <option value="">Select</option>
                {monthOptions.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Harvest end month
              <select
                name="harvestEndMonth"
                value={form.harvestEndMonth}
                onChange={updateForm}
              >
                <option value="">Select</option>
                {monthOptions.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Harvest date (optional)
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
              Require location match
            </label>
            {form.locationRequired && (
              <>
                <label>
                  Latitude
                  <input
                    name="locationLat"
                    type="number"
                    value={form.locationLat}
                    onChange={updateForm}
                  />
                </label>
                <label>
                  Longitude
                  <input
                    name="locationLng"
                    type="number"
                    value={form.locationLng}
                    onChange={updateForm}
                  />
                </label>
                <label>
                  Radius (km)
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
              Allow multiple farmers (cluster)
            </label>
            {actionError && <div className="alert full">{actionError}</div>}
            <div className="form-actions full">
              <button type="button" className="btn ghost" onClick={handleSnapshot}>
                Preview AI price
              </button>
              <button type="button" className="btn ghost" onClick={handleExplain} disabled={explainLoading}>
                {explainLoading && <span className="loader" aria-hidden="true" />}
                {explainLoading ? "Explaining..." : "Explain price"}
              </button>
              <button className="btn" disabled={busy}>
                {busy ? "Creating..." : "Create contract"}
              </button>
            </div>
          </form>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>AI price insight</h3>
            <Tag tone="mint">Explain</Tag>
          </div>
          <div className="info-stack">
            {snapshot ? (
              <>
                <InfoRow title="Fair min" value={`${snapshot.fairMinPrice || 0} ${snapshot.unit || ""}`} />
                <InfoRow title="Fair max" value={`${snapshot.fairMaxPrice || 0} ${snapshot.unit || ""}`} />
                <InfoRow title="Confidence" value={snapshot.confidence || 0} />
                <div className="tag-list">
                  {(snapshot.reasonCodes || []).map((code) => (
                    <Tag key={code} tone="earth">
                      {code}
                    </Tag>
                  ))}
                </div>
              </>
            ) : (
              <p className="muted">Use "Preview AI price" to pull live pricing bands.</p>
            )}
            {explain && (
              <div className="explain-box">
                <h4>Explanation</h4>
                <p>{explain.explanation}</p>
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="card">
        <div className="card-header">
          <h3>Your contracts</h3>
          <Tag tone="earth">Track</Tag>
        </div>
        {loading ? (
          <p className="muted">Loading contracts...</p>
        ) : contracts.length === 0 ? (
          <EmptyState
            title="No contracts yet"
            body="Create your first contract and open it for farmer commitments."
            action={<NavLink to="/buyer" className="btn ghost">Create now</NavLink>}
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
                  <InfoRow title="Required" value={contract.requiredQuantity} />
                  <InfoRow title="Filled" value={contract.filledQuantity} />
                  <InfoRow title="Price type" value={contract.priceType} />
                </div>
                {contract.priceSnapshot && (
                  <div className="snapshot-mini">
                    AI {contract.priceSnapshot.fairMinPrice} - {contract.priceSnapshot.fairMaxPrice}
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

