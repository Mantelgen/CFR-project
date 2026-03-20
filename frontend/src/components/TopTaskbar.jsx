import React, { useState } from "react";
import axios from "axios";
import { Link, useLocation, useNavigate } from "react-router-dom";

const TopTaskbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [logoSrc, setLogoSrc] = useState(`/cfr-logo.png`);

  const isAuthenticated = Boolean(localStorage.getItem("userId"));
  const username = localStorage.getItem("username") || "Guest";
  const isAuthRoute = ["/login", "/register"].includes(location.pathname);
  const isActive = (path) =>
    path === "/" ? location.pathname === "/" : location.pathname.startsWith(path);

  const handleLogout = async () => {
    try {
      await axios.post("/api/auth/logout", {}, { withCredentials: true, timeout: 8000 });
    } catch (err) {
      // Proceed with local cleanup even if server logout fails.
    } finally {
      localStorage.clear();
      navigate("/login");
    }
  };

  return (
    <header className="cfr-taskbar-wrap">
      <nav className="cfr-taskbar navbar navbar-expand-lg">
        <div className="container-fluid px-3 px-md-4">
          <button
            className="cfr-brand btn"
            onClick={() => navigate("/")}
            type="button"
          >
              <img
                src={logoSrc}
                alt="CFR Calatori"
                className="cfr-brand-logo"
                style={{ background: '#fff', borderRadius: 8, border: '1px solid #e0e0e0', padding: 2 }}
                onError={() => setLogoSrc(`/logo192.png`)}
              />
            <span className="cfr-brand-text">
              <strong>CFR Calatori</strong>
              <small>Digital Rail Center</small>
            </span>
          </button>

          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#cfrTaskbarNav"
            aria-controls="cfrTaskbarNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse" id="cfrTaskbarNav">
            <div className="navbar-nav ms-auto align-items-center gap-2">
              <Link to="/" className={`cfr-nav-link ${isActive("/") ? "active" : ""}`}>
                Dashboard
              </Link>
              <Link to="/search" className={`cfr-nav-link ${isActive("/search") ? "active" : ""}`}>
                Search Trains
              </Link>
              <Link
                to="/my-reservations"
                className={`cfr-nav-link ${isActive("/my-reservations") ? "active" : ""}`}
              >
                My Reservations
              </Link>
              <Link
                to="/complaints"
                className={`cfr-nav-link ${isActive("/complaints") ? "active" : ""}`}
              >
                Complaints
              </Link>

              {isAuthenticated ? (
                <>
                  <span className="cfr-user-chip">Signed in as {username}</span>
                  <button className="btn cfr-accent" onClick={handleLogout} type="button">
                    Logout
                  </button>
                </>
              ) : (
                <>
                  {!isAuthRoute && (
                    <button
                      className="btn btn-light"
                      onClick={() => navigate("/login")}
                      type="button"
                    >
                      Login
                    </button>
                  )}
                  <button
                    className="btn cfr-primary text-white"
                    onClick={() => navigate(isAuthRoute ? "/" : "/register")}
                    type="button"
                  >
                    {isAuthRoute ? "Explore" : "Create Account"}
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>
    </header>
  );
};

export default TopTaskbar;
