import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import TopTaskbar from "../components/TopTaskbar";

const MainDashboardPage = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [stats, setStats] = useState({
    backendInfo: "Connecting...",
    totalTrains: 0,
    totalReservations: 0,
    confirmedReservations: 0,
    pendingReservations: 0,
  });

  const loadDashboard = async () => {
      const nextStats = {
        backendInfo: "Unavailable",
        totalTrains: 0,
        totalReservations: 0,
        confirmedReservations: 0,
        pendingReservations: 0,
      };

      try {
        const infoResponse = await axios.get("/api/info", {
          timeout: 6000,
          params: { _ts: Date.now(), _rnd: Math.random().toString(36).slice(2) },
          headers: {
            "Cache-Control": "no-cache",
            Pragma: "no-cache",
          },
        });
        if (typeof infoResponse.data === "string") {
          nextStats.backendInfo = infoResponse.data;
        } else {
          const host = infoResponse.data?.serverHost || "unknown-host";
          const ip = infoResponse.data?.serverIp || "unknown-ip";
          nextStats.backendInfo = `${host} (${ip})`;
        }
      } catch (error) {
        nextStats.backendInfo = "Backend info unavailable";
      }

      try {
        const trainResponse = await axios.get("/api/trains", { timeout: 8000 });
        nextStats.totalTrains = Array.isArray(trainResponse.data) ? trainResponse.data.length : 0;
      } catch (error) {
        nextStats.totalTrains = 0;
      }

      const userId = localStorage.getItem("userId");
      if (userId) {
        try {
          const reservationResponse = await axios.get("/api/reservations/my-reservations", {
            params: { userId },
            timeout: 8000,
          });
          const reservations = Array.isArray(reservationResponse.data) ? reservationResponse.data : [];
          nextStats.totalReservations = reservations.length;
          nextStats.confirmedReservations = reservations.filter((r) => r.status === "CONFIRMED").length;
          nextStats.pendingReservations = reservations.filter(
            (r) => r.status === "PENDING" || r.status === "AWAITING_PAYMENT"
          ).length;
        } catch (error) {
          nextStats.totalReservations = 0;
        }
      }

      setStats(nextStats);
      setIsLoading(false);
    };


  useEffect(() => {
    loadDashboard();
  }, []);

  // Manual refresh handler removed. System Feed now updates only on full page reload.

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <main className="cfr-shell container-fluid">
        <section className="cfr-landing-hero p-4 p-md-5 mb-4">
          <div className="row align-items-center">
            <div className="col-lg-7">
              <span className="cfr-muted-chip mb-3 d-inline-block">Romanian Rail Platform</span>
              <h1 className="display-5 fw-bold mb-3">Plan faster rail trips with a live CFR dashboard</h1>
              <p className="lead mb-4">
                Track service availability, check your reservation status, and book in a modern journey flow.
              </p>
              <div className="d-flex flex-wrap gap-2">
                <button className="btn cfr-primary text-white px-4" onClick={() => navigate("/search")} type="button">
                  Start Search
                </button>
                <button className="btn btn-light px-4" onClick={() => navigate("/my-reservations")} type="button">
                  View Reservations
                </button>
              </div>
            </div>
            <div className="col-lg-5 mt-4 mt-lg-0">
              <div className="cfr-status-panel">
                <div className="d-flex justify-content-between align-items-center mb-2">
                  <h5 className="mb-0">System Feed</h5>
                </div>
                <p className="mb-1">{stats.backendInfo}</p>
                <small className="text-white-50">
                  {isLoading ? "Refreshing dashboard metrics..." : "Dashboard updated just now."}
                </small>
              </div>
            </div>
          </div>
        </section>

        <section className="row g-3 g-md-4 cfr-fade-up">
          <div className="col-12 col-md-6 col-xl-3">
            <article className="card cfr-card cfr-kpi-card h-100">
              <div className="card-body">
                <p className="cfr-kpi-label">Registered Trains</p>
                <h2>{isLoading ? "..." : stats.totalTrains}</h2>
              </div>
            </article>
          </div>
          <div className="col-12 col-md-6 col-xl-3">
            <article className="card cfr-card cfr-kpi-card h-100">
              <div className="card-body">
                <p className="cfr-kpi-label">My Reservations</p>
                <h2>{isLoading ? "..." : stats.totalReservations}</h2>
              </div>
            </article>
          </div>
          <div className="col-12 col-md-6 col-xl-3">
            <article className="card cfr-card cfr-kpi-card h-100">
              <div className="card-body">
                <p className="cfr-kpi-label">Confirmed Trips</p>
                <h2>{isLoading ? "..." : stats.confirmedReservations}</h2>
              </div>
            </article>
          </div>
          <div className="col-12 col-md-6 col-xl-3">
            <article className="card cfr-card cfr-kpi-card h-100">
              <div className="card-body">
                <p className="cfr-kpi-label">Pending Actions</p>
                <h2>{isLoading ? "..." : stats.pendingReservations}</h2>
              </div>
            </article>
          </div>
        </section>
      </main>
    </div>
  );
};

export default MainDashboardPage;
