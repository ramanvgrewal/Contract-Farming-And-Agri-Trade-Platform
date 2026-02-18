import { useEffect, useState } from "react";
import {
  createListing,
  fetchActiveListings,
  fetchLockedListings,
  placeBid
} from "../api/marketplace.js";
import { fetchMarketplaceSnapshot } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { EmptyState, InfoRow, Tag } from "../components/UI.jsx";

export default function Marketplace() {
  const { token, user } = useAuth();
  const [activeListings, setActiveListings] = useState([]);
  const [lockedListings, setLockedListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [snapshot, setSnapshot] = useState(null);
  const [bidInputs, setBidInputs] = useState({});
  const [form, setForm] = useState({
    crop: "",
    state: "",
    variety: "",
    quantity: "",
    unit: "QUINTAL"
  });

  useEffect(() => {
    async function loadListings() {
      setLoading(true);
      try {
        const [active, locked] = await Promise.all([
          fetchActiveListings(token),
          fetchLockedListings(token)
        ]);
        setActiveListings(active || []);
        setLockedListings(locked || []);
      } catch (err) {
        setError(err.message || "Unable to load listings");
      } finally {
        setLoading(false);
      }
    }
    loadListings();
  }, [token]);

  function updateForm(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSnapshot() {
    setError("");
    setSnapshot(null);
    try {
      const response = await fetchMarketplaceSnapshot(token, {
        crop: form.crop,
        state: form.state
      });
      setSnapshot(response);
    } catch (err) {
      setError(err.message || "Unable to fetch price snapshot");
    }
  }

  async function handleCreate(event) {
    event.preventDefault();
    setError("");
    try {
      const response = await createListing(token, {
        farmerId: user.id,
        crop: form.crop,
        state: form.state,
        variety: form.variety,
        quantity: Number(form.quantity),
        unit: form.unit
      });
      setActiveListings((prev) => [response, ...prev]);
    } catch (err) {
      setError(err.message || "Unable to create listing");
    }
  }

  async function handleBid(listingId) {
    const bidAmount = bidInputs[listingId];
    if (!bidAmount) return;
    setError("");
    try {
      await placeBid(token, listingId, {
        bidderId: user.id,
        bidAmount: Number(bidAmount)
      });
    } catch (err) {
      setError(err.message || "Unable to place bid");
    }
  }

  function updateBid(listingId, value) {
    setBidInputs((prev) => ({ ...prev, [listingId]: value }));
  }

  return (
    <div className="marketplace">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">Marketplace</p>
          <h2>Live crop listings & competitive bids</h2>
          <p>
            Buyers can bid on active listings, farmers can post fresh harvests
            with AI-backed base prices.
          </p>
        </div>
        {user?.role === "FARMER" && (
          <div className="card compact">
            <h4>Post a listing</h4>
            <form className="grid-form" onSubmit={handleCreate}>
              <label>
                Crop
                <input name="crop" value={form.crop} onChange={updateForm} />
              </label>
              <label>
                State
                <input name="state" value={form.state} onChange={updateForm} />
              </label>
              <label>
                Variety
                <input name="variety" value={form.variety} onChange={updateForm} />
              </label>
              <label>
                Quantity
                <input name="quantity" type="number" value={form.quantity} onChange={updateForm} />
              </label>
              <label>
                Unit
                <input name="unit" value={form.unit} onChange={updateForm} />
              </label>
              <div className="form-actions full">
                <button type="button" className="btn ghost" onClick={handleSnapshot}>
                  AI snapshot
                </button>
                <button className="btn">Create listing</button>
              </div>
            </form>
            {snapshot && (
              <div className="snapshot-mini">
                Fair range: {snapshot.fairMinPrice} - {snapshot.fairMaxPrice} {snapshot.unit}
              </div>
            )}
          </div>
        )}
      </section>

      {error && <div className="alert wide">{error}</div>}

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>Active listings</h3>
            <Tag tone="gold">Open</Tag>
          </div>
          {loading ? (
            <p className="muted">Loading listings...</p>
          ) : activeListings.length === 0 ? (
            <EmptyState
              title="No active listings"
              body="Farmers will add listings shortly."
            />
          ) : (
            <div className="listing-grid">
              {activeListings.map((listing) => (
                <div key={listing.id} className="listing-card">
                  <div className="listing-head">
                    <div>
                      <h4>{listing.crop}</h4>
                      <span>{listing.state}</span>
                    </div>
                    <Tag tone="mint">{listing.status}</Tag>
                  </div>
                  <InfoRow title="Quantity" value={`${listing.quantity} ${listing.unit}`} />
                  <InfoRow title="Base price" value={listing.basePrice} />
                  <InfoRow
                    title="Current bid"
                    value={listing.currentHighestBid || listing.basePrice}
                  />
                  {user?.role === "BUYER" && (
                    <div className="bid-row">
                      <input
                        type="number"
                        placeholder="Your bid"
                        value={bidInputs[listing.id] || ""}
                        onChange={(event) => updateBid(listing.id, event.target.value)}
                      />
                      <button className="btn" onClick={() => handleBid(listing.id)}>
                        Place bid
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <h3>Locked listings</h3>
            <Tag tone="earth">Closed</Tag>
          </div>
          {lockedListings.length === 0 ? (
            <EmptyState
              title="No locked listings"
              body="When bidding closes, listings will appear here."
            />
          ) : (
            <div className="listing-grid">
              {lockedListings.map((listing) => (
                <div key={listing.id} className="listing-card">
                  <div className="listing-head">
                    <div>
                      <h4>{listing.crop}</h4>
                      <span>{listing.state}</span>
                    </div>
                    <Tag tone="earth">{listing.status}</Tag>
                  </div>
                  <InfoRow title="Quantity" value={`${listing.quantity} ${listing.unit}`} />
                  <InfoRow title="Winning bid" value={listing.currentHighestBid} />
                </div>
              ))}
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
