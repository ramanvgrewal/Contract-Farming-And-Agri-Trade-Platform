# Agri-Contract & Trade Platform

This platform facilitates digital agri-contracts and trade between farmers and buyers.

## Deterministic 3-Bid Negotiation System

We have transitioned from real-time WebSocket negotiation to a deterministic REST-based 3-bid negotiation system. This change improves auditability, reduces system complexity, and ensures a structured negotiation process.

### Workflow

1.  **Contract Initiation**: A contract is created in the `NEGOTIATING` state.
2.  **Bidding**: Both parties can place up to 3 bids total for a contract.
    *   `POST /api/v1/contracts/{id}/bids`
3.  **Maximum 3 Bids**: The system enforces a maximum of 3 bids per contract.
4.  **Acceptance/Rejection**: At any point during negotiation, either party can accept or reject the contract.
    *   `POST /api/v1/contracts/{id}/accept`
    *   `POST /api/v1/contracts/{id}/reject`
5.  **Status Transitions**:
    *   `NEGOTIATING` -> `ACCEPTED`
    *   `NEGOTIATING` -> `REJECTED`
    *   `NEGOTIATING` -> `EXPIRED` (if not settled within time limit)

## AI-Powered Contract Risk Analysis (Local Ollama)

Every bid is automatically analyzed by our AI system using a locally hosted Ollama instance (no external APIs).

### How it works

1.  **Price Comparison**: The bid price is compared against a market reference price (e.g., the maximum price in the crop requirement).
2.  **Deviation Calculation**: The percentage deviation from the reference price is calculated deterministically in Java.
3.  **Local LLM Reasoning**: The system invokes `ollama run llama3` via a local CLI process to classify the risk Level (LOW, MEDIUM, HIGH) and provide a concise justification.
4.  **Automatic Expiration**: If a contract is not accepted by the 3rd bid, it automatically transitions to the `EXPIRED` state.
5.  **Auditability**: The AI-generated explanation is persisted with the bid, providing transparency for both parties.

## Why we removed Real-Time Negotiation

Real-time negotiation via WebSockets was removed to:
*   Ensure **deterministic** state transitions.
*   Provide a **clear audit trail** of the negotiation rounds.
*   Reduce **infrastructure overhead** and complexity associated with persistent WebSocket connections.
*   Align with the **structured 3-bid system** required for formal agricultural contracts.
