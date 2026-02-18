import { request } from "./client.js";

export function login(credentials) {
  return request("/api/auth/login", {
    method: "POST",
    body: credentials
  });
}

export function register(payload) {
  return request("/api/auth/register", {
    method: "POST",
    body: payload
  });
}
