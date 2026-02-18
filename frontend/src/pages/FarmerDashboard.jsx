import { useEffect, useMemo, useState } from "react";
import { NavLink } from "react-router-dom";
import { fetchOpenContracts, joinContract } from "../api/contracts.js";
import { fetchContractExplanation } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { EmptyState, InfoRow, StatCard, Tag } from "../components/UI.jsx";

const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

export default function FarmerDashboard() {
  const { token, user } = useAuth();
  const [contracts, setContracts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState("");
  const [selected, setSelected] = useState(null);
  const [commitQty, setCommitQty] = useState("");
  const [explain, setExplain] = useState(null);
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
      setActionError(err.message || "Unable to join contract");
    }
  }

  async function handleExplain() {
    setActionError("");
    setExplain(null);
    try {
      const response = await fetchContractExplanation(token, explainForm);
      setExplain(response);
    } catch (err) {
      setActionError(err.message || "Unable to fetch explanation");
    }
  }

  function updateExplainField(event) {
    const { name, value } = event.target;
    setExplainForm((prev) => ({ ...prev, [name]: value }));
  }

  return (
    <div className="dashboard farmer">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">Farmer workspace</p>
          <h2>Good day, {user?.name || "Farmer"}</h2>
          <p>
            Browse open contracts, review fair price explanations, and commit
            only the quantity you can deliver.
          </p>
        </div>
        <div className="stat-grid">
          <StatCard label="Open contracts" value={stats.open} />
          <StatCard label="Total available" value={stats.total} />
        </div>
      </section>

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>Open contracts</h3>
            <Tag tone="earth">Pick one</Tag>
          </div>
          {loading ? (
            <p className="muted">Loading open contracts...</p>
          ) : contracts.length === 0 ? (
            <EmptyState
              title="No open contracts yet"
              body="Buyers will post contracts soon. Check back later."
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
            <h3>Commit to contract</h3>
            <Tag tone="mint">Secure</Tag>
          </div>
          {selected ? (
            <div className="info-stack">
              <InfoRow title="Crop" value={selected.cropName} />
              <InfoRow title="State" value={selected.state} />
              <InfoRow title="Required" value={selected.requiredQuantity} />
              <InfoRow title="Filled" value={selected.filledQuantity} />
              <label>
                Commit quantity
                <input
                  type="number"
                  value={commitQty}
                  onChange={(event) => setCommitQty(event.target.value)}
                />
              </label>
              {selected.priceSnapshot && (
                <div className="snapshot-mini">
                  AI price: {selected.priceSnapshot.fairMinPrice} - {selected.priceSnapshot.fairMaxPrice}
                </div>
              )}
              {actionError && <div className="alert">{actionError}</div>}
              <button className="btn" onClick={handleJoin}>
                Commit crop
              </button>
              <NavLink className="btn ghost" to={`/contracts/${selected.id}`}>
                View contract
              </NavLink>
            </div>
          ) : (
            <p className="muted">Select a contract from the list to commit.</p>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <h3>AI price explanation</h3>
            <Tag tone="earth">Transparent</Tag>
          </div>
          <div className="info-stack">
            <label>
              Crop
              <input name="crop" value={explainForm.crop} onChange={updateExplainField} />
            </label>
            <label>
              State
              <input name="state" value={explainForm.state} onChange={updateExplainField} />
            </label>
            <label>
              Harvest start month
              <select
                name="harvestStartMonth"
                value={explainForm.harvestStartMonth}
                onChange={updateExplainField}
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
                value={explainForm.harvestEndMonth}
                onChange={updateExplainField}
              >
                <option value="">Select</option>
                {monthOptions.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </label>
            <button className="btn ghost" onClick={handleExplain}>
              Explain price
            </button>
            {explain && (
              <div className="explain-box">
                <h4>Snapshot</h4>
                <p>
                  Fair range: {explain.snapshot?.fairMinPrice || 0} - {explain.snapshot?.fairMaxPrice || 0} {explain.snapshot?.unit || ""}
                </p>
                <h4>Explanation</h4>
                <p>{explain.explanation}</p>
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
