import { request } from "./client.js";

export function fetchMarketplaceSnapshot(token, payload) {
  return request("/api/pricing/marketplace", {
    token,
    query: payload
  });
}

export function fetchContractSnapshot(token, payload) {
  return request("/api/pricing/contract", {
    token,
    query: payload
  });
}

export function fetchContractExplanation(token, payload) {
  return request("/api/pricing/contract/explain", {
    token,
    query: payload
  });
}
