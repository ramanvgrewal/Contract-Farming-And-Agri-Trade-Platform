import { request } from "./client.js";

export function createContract(token, payload) {
  return request("/api/buyer/contracts", {
    method: "POST",
    body: payload,
    token
  });
}

export function openContract(token, contractId) {
  return request(`/api/buyer/contracts/${contractId}/open`, {
    method: "POST",
    token
  });
}

export function fetchBuyerContracts(token) {
  return request("/api/buyer/contracts", { token });
}

export function fetchContractById(token, contractId) {
  return request(`/api/buyer/contracts/${contractId}`, { token });
}

export function fetchOpenContracts(token) {
  return request("/api/farmer/contracts/open", { token });
}

export function joinContract(token, contractId, payload) {
  return request(`/api/farmer/contracts/${contractId}/join`, {
    method: "POST",
    body: payload,
    token
  });
}

export function fetchFarmerContract(token, contractId) {
  return request(`/api/farmer/contracts/${contractId}`, { token });
}
