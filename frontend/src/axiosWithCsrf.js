// axiosWithCsrf.js
// Axios instance that automatically attaches CSRF token for POST/DELETE requests
import axios from "axios";

let csrfToken = null;

// Fetch CSRF token from backend
export async function fetchCsrfToken() {
  try {
    const response = await axios.get("/api/csrf", { withCredentials: true });
    // Try header first, fallback to response.data
    csrfToken = response.headers["x-csrf-token"] || response.data?.token || response.data?.csrfToken || null;
    return csrfToken;
  } catch (err) {
    csrfToken = null;
    return null;
  }
}

// Axios instance
const axiosWithCsrf = axios.create({ withCredentials: true });

// Request interceptor to add CSRF token
axiosWithCsrf.interceptors.request.use(
  async (config) => {
    if (["post", "put", "delete", "patch"].includes(config.method)) {
      if (!csrfToken) {
        await fetchCsrfToken();
      }
      if (csrfToken) {
        config.headers["X-CSRF-TOKEN"] = csrfToken;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosWithCsrf;
