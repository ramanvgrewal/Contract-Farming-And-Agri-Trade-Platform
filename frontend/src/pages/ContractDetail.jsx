import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchContractById, fetchFarmerContract } from "../api/contracts.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useLanguage } from "../context/LanguageContext.jsx";
import { InfoRow, Tag } from "../components/UI.jsx";
import { formatUnit } from "../utils/format.js";

export default function ContractDetail() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const { t, lang } = useLanguage();
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
    return <p className="muted">{t("contract.loading")}</p>;
  }

  return (
    <div className="contract-detail">
      <div className="card">
        <div className="card-header">
          <h3>{contract.cropName} {t("contract.title")}</h3>
          <Tag tone="gold">{contract.status}</Tag>
        </div>
        <div className="info-grid">
          <InfoRow title="Buyer" value={contract.buyerId} />
          <InfoRow title={t("label.crop")} value={contract.cropName} />
          <InfoRow title={t("label.variety")} value={contract.cropVariety || "-"} />
          <InfoRow title={t("label.state")} value={contract.state || "-"} />
          <InfoRow title={t("label.requiredQty")} value={contract.requiredQuantity} />
          <InfoRow title={t("label.filledQty")} value={contract.filledQuantity} />
          <InfoRow title={t("label.priceType")} value={contract.priceType} />
          <InfoRow title={t("label.priceMin")} value={contract.offeredPriceMin || "-"} />
          <InfoRow title={t("label.priceMax")} value={contract.offeredPriceMax || "-"} />
          <InfoRow title="Location required" value={contract.locationRequired ? "Yes" : "No"} />
          <InfoRow title="Cluster enabled" value={contract.clusterEnabled ? "Yes" : "No"} />
        </div>
        {contract.priceSnapshot && (
          <div className="snapshot-panel">
            <h4>Price snapshot</h4>
            <InfoRow
              title={t("label.fairRange")}
              value={`${contract.priceSnapshot.fairMinPrice || 0} - ${contract.priceSnapshot.fairMaxPrice || 0} ${formatUnit(contract.priceSnapshot.unit, lang)}`}
            />
            <InfoRow title={t("label.confidence")} value={contract.priceSnapshot.confidence} />
          </div>
        )}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>{t("contract.commitments")}</h3>
          <Tag tone="mint">{contract.commitments?.length || 0}</Tag>
        </div>
        {contract.commitments?.length ? (
          <div className="commitment-list">
            {contract.commitments.map((commitment) => (
              <div key={commitment.id} className="commitment-card">
                <InfoRow title="Farmer" value={commitment.farmerId} />
                <InfoRow title={t("label.quantity")} value={commitment.committedQuantity} />
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
