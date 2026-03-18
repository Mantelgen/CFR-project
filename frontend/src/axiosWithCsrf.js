import axios from "axios";

const axiosWithCsrf = axios.create({
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

const refreshCsrfCookie = async () => {
  await axios.get("/api/csrf", { withCredentials: true });
};

axiosWithCsrf.interceptors.request.use(
  async (config) => {
    const method = (config.method || "get").toLowerCase();
    if (["post", "put", "patch", "delete"].includes(method)) {
      await refreshCsrfCookie();
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosWithCsrf;
