import { toast } from "react-toastify";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

function buildQuery(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") return;
    if (typeof value === "object") {
      Object.entries(value).forEach(([subKey, subValue]) => {
        if (subValue !== undefined && subValue !== null && subValue !== "") {
          query.append(subKey, subValue);
        }
      });
    } else {
      query.append(key, value);
    }
  });
  const qs = query.toString();
  return qs ? `?${qs}` : "";
}

async function request(path, { method = "GET", body, token, query } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers.Authorization = `Bearer ${token}`;

  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}${buildQuery(query)}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined
    });
  } catch (err) {
    toast.error("Unable to connect to the server. Please check your internet connection.");
    throw new Error("Network error");
  }

  let payload = null;
  const text = await response.text();
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = text;
    }
  }

  if (!response.ok) {
    // Determine the error message
    const message = payload?.message || payload?.error || "Request failed";

    // Create an error object
    const error = new Error(message);

    // Attach validation errors if present so forms can use them
    if (payload?.errors) {
      error.errors = payload.errors;
    }

    // Handle global toast and actions based on status code
    if (response.status === 401) {
      toast.error("Session expired. Please log in again.");
      localStorage.removeItem("savvyfarm.auth");
      window.location.href = "/login";
    } else if (response.status === 403) {
      toast.error("You don't have permission to perform this action.");
    } else if (response.status === 404) {
      toast.error("The requested resource was not found.");
    } else if (response.status >= 500) {
      toast.error("Something went wrong on our end. Please try again later.");
    } else if (response.status === 400 && !payload?.errors) {
      // Show bad request only if we are not handling field level validation downstream
      toast.error(message || "Bad Request");
    }

    throw error;
  }

  return payload;
}

export { API_BASE_URL, request };
