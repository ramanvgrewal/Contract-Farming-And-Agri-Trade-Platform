import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchContractById, fetchFarmerContract } from "../api/contracts.js";
import { useAuth } from "../context/AuthContext.jsx";
import { InfoRow, Tag } from "../components/UI.jsx";

export default function ContractDetail() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const [contract, setContract] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadContract() {
      setError("");
      try {
        const data =
          user?.role === "BUYER"
            ? await fetchContractById(token, id)
            : await fetchFarmerContract(token, id);
        setContract(data);
      } catch (err) {
        setError(err.message || "Unable to load contract");
      }
    }
    if (user) loadContract();
  }, [token, id, user]);

  if (error) {
    return <div className="alert wide">{error}</div>;
  }

  if (!contract) {
    return <p className="muted">Loading contract...</p>;
  }

  return (
    <div className="contract-detail">
      <div className="card">
        <div className="card-header">
          <h3>{contract.cropName} contract</h3>
          <Tag tone="gold">{contract.status}</Tag>
        </div>
        <div className="info-grid">
          <InfoRow title="Buyer" value={contract.buyerId} />
          <InfoRow title="Crop" value={contract.cropName} />
          <InfoRow title="Variety" value={contract.cropVariety || "-"} />
          <InfoRow title="State" value={contract.state || "-"} />
          <InfoRow title="Required qty" value={contract.requiredQuantity} />
          <InfoRow title="Filled qty" value={contract.filledQuantity} />
          <InfoRow title="Price type" value={contract.priceType} />
          <InfoRow title="Offered min" value={contract.offeredPriceMin || "-"} />
          <InfoRow title="Offered max" value={contract.offeredPriceMax || "-"} />
          <InfoRow title="Location required" value={contract.locationRequired ? "Yes" : "No"} />
          <InfoRow title="Cluster enabled" value={contract.clusterEnabled ? "Yes" : "No"} />
        </div>
        {contract.priceSnapshot && (
          <div className="snapshot-panel">
            <h4>Price snapshot</h4>
            <InfoRow
              title="Fair range"
              value={`${contract.priceSnapshot.fairMinPrice || 0} - ${contract.priceSnapshot.fairMaxPrice || 0} ${contract.priceSnapshot.unit || ""}`}
            />
            <InfoRow title="Confidence" value={contract.priceSnapshot.confidence} />
          </div>
        )}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Farmer commitments</h3>
          <Tag tone="mint">{contract.commitments?.length || 0}</Tag>
        </div>
        {contract.commitments?.length ? (
          <div className="commitment-list">
            {contract.commitments.map((commitment) => (
              <div key={commitment.id} className="commitment-card">
                <InfoRow title="Farmer" value={commitment.farmerId} />
                <InfoRow title="Quantity" value={commitment.committedQuantity} />
                <InfoRow title="Status" value={commitment.status} />
              </div>
            ))}
          </div>
        ) : (
          <p className="muted">No commitments yet.</p>
        )}
      </div>
    </div>
  );
}
