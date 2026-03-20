import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import TopTaskbar from "../components/TopTaskbar";

const AuthPage = ({ isLogin = true }) => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setIsLoading(true);

    try {
      const endpoint = isLogin ? "/api/auth/login" : "/api/auth/register";
      const data = isLogin
        ? { username: formData.username, password: formData.password }
        : formData;

      const response = await axios.post(endpoint, data, { timeout: 30000, withCredentials: true });

      if (response.data.success) {
        setSuccess(response.data.message);
        if (isLogin) {
          localStorage.setItem("userId", response.data.userId);
          localStorage.setItem("username", response.data.username);
          localStorage.setItem("email", response.data.email);
          setTimeout(() => navigate("/search"), 1500);
        } else {
          setFormData({
            username: "",
            email: "",
            password: "",
            firstName: "",
            lastName: "",
          });
          setTimeout(() => navigate("/login"), 2000);
        }
      }
    } catch (err) {
      if (err.code === "ECONNABORTED") {
        setError("Request timed out while waiting for email service. Please verify SMTP/Postfix connectivity and try again.");
      } else {
        setError(
          err.response?.data?.error || "An error occurred. Please try again."
        );
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="container cfr-auth-wrap">
        <div className="row justify-content-center">
          <div className="col-md-8 col-lg-6">
            <div className="card cfr-card shadow-lg">
              <div className="card-body p-5">
                <h1 className="text-center mb-4">
                  {isLogin ? "Login" : "Register"}
                </h1>

                {error && (
                  <div className="alert alert-danger alert-dismissible fade show">
                    {error}
                  </div>
                )}

                {success && (
                  <div className="alert alert-success alert-dismissible fade show">
                    {success}
                  </div>
                )}

                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label htmlFor="username" className="form-label">
                      Username
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      id="username"
                      name="username"
                      value={formData.username}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  {!isLogin && (
                    <>
                      <div className="mb-3">
                        <label htmlFor="email" className="form-label">
                          Email
                        </label>
                        <input
                          type="email"
                          className="form-control"
                          id="email"
                          name="email"
                          value={formData.email}
                          onChange={handleChange}
                          required
                        />
                      </div>

                      <div className="row">
                        <div className="col-md-6 mb-3">
                          <label htmlFor="firstName" className="form-label">
                            First Name
                          </label>
                          <input
                            type="text"
                            className="form-control"
                            id="firstName"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                          />
                        </div>
                        <div className="col-md-6 mb-3">
                          <label htmlFor="lastName" className="form-label">
                            Last Name
                          </label>
                          <input
                            type="text"
                            className="form-control"
                            id="lastName"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                          />
                        </div>
                      </div>
                    </>
                  )}

                  <div className="mb-3">
                    <label htmlFor="password" className="form-label">
                      Password
                    </label>
                    <input
                      type="password"
                      className="form-control"
                      id="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary w-100"
                    disabled={isLoading}
                  >
                    {isLoading
                      ? "Loading..."
                      : isLogin
                      ? "Login"
                      : "Register"}
                  </button>
                </form>

                <div className="text-center mt-4">
                  {isLogin ? (
                    <p>
                      Don't have an account?{" "}
                      <Link to="/register">Register here</Link>
                    </p>
                  ) : (
                    <p>
                      Already have an account?{" "}
                      <Link to="/login">Login here</Link>
                    </p>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
