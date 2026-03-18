import axios from "axios";

let csrfToken = null;

const axiosWithCsrf = axios.create({
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

const ensureCsrfToken = async () => {
  if (csrfToken) {
    return csrfToken;
  }

  const response = await axios.get("/api/csrf", { withCredentials: true });
  csrfToken = response.data?.token || null;
  return csrfToken;
};

axiosWithCsrf.interceptors.request.use(
  async (config) => {
    const method = (config.method || "get").toLowerCase();
    if (["post", "put", "patch", "delete"].includes(method)) {
      const token = await ensureCsrfToken();
      if (token) {
        config.headers = config.headers || {};
        config.headers["X-XSRF-TOKEN"] = token;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosWithCsrf;
