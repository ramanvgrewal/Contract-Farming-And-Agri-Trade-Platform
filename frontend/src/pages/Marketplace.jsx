import { useEffect, useState } from "react";
import {
  createListing,
  fetchActiveListings,
  placeBid
} from "../api/marketplace.js";
import { fetchMarketplaceSnapshot } from "../api/pricing.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";
import { EmptyState, InfoRow, Tag, Spinner } from "../components/UI.jsx";
import { formatUnit } from "../utils/format.js";

export default function Marketplace() {
  const { token, user } = useAuth();
  const { t, lang } = useLanguage();
  const [activeListings, setActiveListings] = useState([]);
  const [closedListings, setClosedListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
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
        const active = await fetchActiveListings(token);
        const listings = active || [];
        const now = Date.now();
        const isClosed = (listing) => {
          if (listing.status === "CLOSED" || listing.status === "CANCELLED") return true;
          if (listing.status === "LOCKED" && listing.biddingEndTime) {
            return new Date(listing.biddingEndTime).getTime() <= now;
          }
          return false;
        };

        setActiveListings(listings.filter((listing) => !isClosed(listing)));
        setClosedListings(listings.filter((listing) => isClosed(listing)));
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
    setFieldErrors((prev) => ({ ...prev, [name]: "" }));
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
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        setError(err.message || "Unable to fetch price snapshot");
      }
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
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        setError(err.message || "Unable to create listing");
      }
    }
  }

  async function handleBid(listingId) {
    const bidAmount = bidInputs[listingId];
    if (!bidAmount) return;
    setError("");
    try {
      const response = await placeBid(token, listingId, {
        bidderId: user.id,
        bidAmount: Number(bidAmount)
      });
      setActiveListings((prev) =>
        prev.map((listing) =>
          listing.id === listingId
            ? {
              ...listing,
              currentHighestBid: response.currentHighestBidAmount,
              highestBidderId: response.highestBidderId
            }
            : listing
        )
      );
    } catch (err) {
      if (err.errors) {
        // Fallback for bidding inline errors if backend supports it
        setError("Invalid bid: " + JSON.stringify(err.errors));
      } else {
        setError(err.message || "Unable to place bid");
      }
    }
  }

  function updateBid(listingId, value) {
    setBidInputs((prev) => ({ ...prev, [listingId]: value }));
  }

  return (
    <div className="marketplace">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">{t("market.eyebrow")}</p>
          <h2>{t("market.title")}</h2>
          <p>{t("market.subtitle")}</p>
        </div>
        {user?.role === "FARMER" && (
          <div className="card compact">
            <h4>{t("market.post.title")}</h4>
            <form className="grid-form" onSubmit={handleCreate}>
              <label>
                {t("label.crop")}
                <input name="crop" value={form.crop} onChange={updateForm} />
                {fieldErrors.crop && <div className="field-error">{fieldErrors.crop}</div>}
              </label>
              <label>
                {t("label.state")}
                <input name="state" value={form.state} onChange={updateForm} />
                {fieldErrors.state && <div className="field-error">{fieldErrors.state}</div>}
              </label>
              <label>
                {t("label.variety")}
                <input name="variety" value={form.variety} onChange={updateForm} />
                {fieldErrors.variety && <div className="field-error">{fieldErrors.variety}</div>}
              </label>
              <label>
                {t("label.quantity")}
                <input name="quantity" type="number" value={form.quantity} onChange={updateForm} />
                {fieldErrors.quantity && <div className="field-error">{fieldErrors.quantity}</div>}
              </label>
              <label>
                {t("label.unit")}
                <input name="unit" value={form.unit} onChange={updateForm} />
                {fieldErrors.unit && <div className="field-error">{fieldErrors.unit}</div>}
              </label>
              <div className="form-actions full">
                <button type="button" className="btn ghost" onClick={handleSnapshot}>
                  {t("market.button.snapshot")}
                </button>
                <button className="btn">{t("market.button.create")}</button>
              </div>
            </form>
            {snapshot && (
              <div className="snapshot-mini">
                {t("label.fairRange")}: {snapshot.fairMinPrice} - {snapshot.fairMaxPrice}{" "}
                {formatUnit(snapshot.unit, lang)}
              </div>
            )}
          </div>
        )}
      </section>

      {error && <div className="alert wide">{error}</div>}

      <section className="panel-grid">
        <div className="card">
          <div className="card-header">
            <h3>{t("market.active")}</h3>
            <Tag tone="gold">Open</Tag>
          </div>
          {loading ? (
            <Spinner text="Loading listings..." />
          ) : activeListings.length === 0 ? (
            <EmptyState
              title={t("market.empty.active.title")}
              body={t("market.empty.active.body")}
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
                  <InfoRow title={t("label.quantity")} value={`${listing.quantity} ${listing.unit}`} />
                  <InfoRow title={t("label.basePrice")} value={listing.basePrice} />
                  <InfoRow title={t("label.currentBid")} value={listing.currentHighestBid || listing.basePrice} />
                  {user?.role === "BUYER" && (
                    <div className="bid-row">
                      <input
                        type="number"
                        placeholder="Your bid"
                        value={bidInputs[listing.id] || ""}
                        onChange={(event) => updateBid(listing.id, event.target.value)}
                      />
                      <button className="btn" onClick={() => handleBid(listing.id)}>
                        {t("market.bid.place")}
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
            <h3>{t("market.closed")}</h3>
            <Tag tone="earth">Closed</Tag>
          </div>
          {closedListings.length === 0 ? (
            <EmptyState
              title={t("market.empty.closed.title")}
              body={t("market.empty.closed.body")}
            />
          ) : (
            <div className="listing-grid">
              {closedListings.map((listing) => (
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
