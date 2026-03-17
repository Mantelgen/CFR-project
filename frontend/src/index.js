import axios from "axios";
// Always send cookies (JSESSIONID) with requests
axios.defaults.withCredentials = true;

// CSRF token setup
let csrfToken = null;

// Fetch CSRF token from backend (Spring Security exposes it at /api/csrf or as a cookie)
const fetchCsrfToken = async () => {
  try {
    const resp = await axios.get("/api/csrf", { withCredentials: true });
    // Spring Boot 3+ returns { headerName, parameterName, token }
    csrfToken = resp.data.token;
    axios.defaults.headers.post[resp.data.headerName] = csrfToken;
    axios.defaults.headers.put[resp.data.headerName] = csrfToken;
    axios.defaults.headers.delete[resp.data.headerName] = csrfToken;
  } catch (e) {
    // fallback: try to read from cookie (XSRF-TOKEN)
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    if (match) {
      csrfToken = decodeURIComponent(match[1]);
      axios.defaults.headers.post["X-XSRF-TOKEN"] = csrfToken;
      axios.defaults.headers.put["X-XSRF-TOKEN"] = csrfToken;
      axios.defaults.headers.delete["X-XSRF-TOKEN"] = csrfToken;
    }
  }
};

fetchCsrfToken();
import React from 'react';
import ReactDOM from 'react-dom/client';
import 'bootstrap/dist/css/bootstrap.min.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
