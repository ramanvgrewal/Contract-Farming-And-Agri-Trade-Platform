import { NavLink } from "react-router-dom";

export default function Landing() {
  return (
    <div className="landing">
      <section className="hero">
        <div className="hero-content">
          <p className="eyebrow">SavvyFARM Contract Platform</p>
          <h1>
            Farm-to-buyer contracts that respect the field, protect margins, and
            move with the season.
          </h1>
          <p className="lede">
            Create price-aware contracts, invite farmers to commit, and use AI
            pricing snapshots to keep every agreement transparent.
          </p>
          <div className="hero-actions">
            <NavLink className="btn" to="/register">
              Start as a buyer
            </NavLink>
            <NavLink className="btn ghost" to="/register">
              Join as a farmer
            </NavLink>
          </div>
          <div className="hero-stats">
            <div>
              <strong>AI-driven</strong>
              <span>market snapshots for every crop</span>
            </div>
            <div>
              <strong>Secure</strong>
              <span>token-backed contract flow</span>
            </div>
            <div>
              <strong>Seasonal</strong>
              <span>harvest windows modeled upfront</span>
            </div>
          </div>
        </div>
        <div className="hero-panel">
          <div className="hero-card">
            <h3>Buyer workflow</h3>
            <ul>
              <li>Create a contract with AI price ranges</li>
              <li>Open it for farmer commitments</li>
              <li>Track filled quantity and lock dates</li>
            </ul>
          </div>
          <div className="hero-card farmer">
            <h3>Farmer workflow</h3>
            <ul>
              <li>Review open contracts and fair price bands</li>
              <li>Commit partial or full quantity</li>
              <li>Lock in harvest schedule with clarity</li>
            </ul>
          </div>
        </div>
      </section>

      <section className="feature-grid">
        <div className="feature">
          <h3>Price Explanation</h3>
          <p>
            Get a plain-language explanation alongside every AI snapshot, so
            farmers and buyers understand the why.
          </p>
        </div>
        <div className="feature">
          <h3>Marketplace Pulse</h3>
          <p>
            Browse active listings, place bids, and keep a record of every offer
            without guesswork.
          </p>
        </div>
        <div className="feature">
          <h3>Location Smart</h3>
          <p>
            Enable or relax location requirements depending on crop logistics.
          </p>
        </div>
      </section>
    </div>
  );
}
