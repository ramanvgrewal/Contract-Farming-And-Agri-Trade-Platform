import { request } from "./client.js";

export function createListing(token, payload) {
  return request("/api/marketplace/listings", {
    method: "POST",
    token,
    query: payload
  });
}

export function cancelListing(token, listingId, farmerId) {
  return request(`/api/marketplace/listings/${listingId}/cancel`, {
    method: "PUT",
    token,
    query: { farmerId }
  });
}

export function placeBid(token, listingId, payload) {
  return request(`/api/marketplace/listings/${listingId}/bids`, {
    method: "POST",
    token,
    query: payload
  });
}

export function fetchActiveListings(token) {
  return request("/api/marketplace/listings/active", { token });
}

export function fetchLockedListings(token) {
  return request("/api/marketplace/listings/locked", { token });
}

export function fetchListing(token, listingId) {
  return request(`/api/marketplace/listings/${listingId}`, { token });
}

export function fetchListingBids(token, listingId) {
  return request(`/api/marketplace/listings/${listingId}/bids`, { token });
}
