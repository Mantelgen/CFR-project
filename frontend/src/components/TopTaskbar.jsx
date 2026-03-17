import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

const TopTaskbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [logoSrc, setLogoSrc] = useState(`${process.env.PUBLIC_URL}/cfr-logo.png`);

  const isAuthenticated = Boolean(localStorage.getItem("userId"));
  const username = localStorage.getItem("username") || "Guest";
  const isAuthRoute = ["/login", "/register"].includes(location.pathname);
  const isActive = (path) =>
    path === "/" ? location.pathname === "/" : location.pathname.startsWith(path);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
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
              onError={() => setLogoSrc(`${process.env.PUBLIC_URL}/cfr-bg.svg`)}
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
