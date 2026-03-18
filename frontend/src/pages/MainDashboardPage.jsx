import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import TopTaskbar from "../components/TopTaskbar";

const MainDashboardPage = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [backendInfo, setBackendInfo] = useState("Connecting...");
  const [quickFrom, setQuickFrom] = useState("");
  const [quickTo, setQuickTo] = useState("");
  const [stationSuggestions, setStationSuggestions] = useState([]);
  const [stats, setStats] = useState({
    totalTrains: 0,
    totalReservations: 0,
    confirmedReservations: 0,
    pendingReservations: 0,
  });

  const loadDashboard = async () => {
    setIsLoading(true);
    // Fetch backend info identically to TrainSearchPage
    try {
      const response = await axios.get("/api/info", {
        timeout: 6000,
        params: { _ts: Date.now() },
        headers: {
          "Cache-Control": "no-cache",
          Pragma: "no-cache",
        },
      });
      if (typeof response.data === "string") {
        setBackendInfo(response.data || "Unavailable");
      } else {
        const host = response.data?.serverHost || "unknown-host";
        const ip = response.data?.serverIp || "unknown-ip";
        setBackendInfo(`${host} (${ip})`);
      }
    } catch (err) {
      setBackendInfo("Backend info unavailable");
    }

    // Fetch other dashboard stats
    const nextStats = {
      totalTrains: 0,
      totalReservations: 0,
      confirmedReservations: 0,
      pendingReservations: 0,
    };
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

  useEffect(() => {
    const fetchStationSuggestions = async () => {
      try {
        const response = await axios.get("/api/stations", { timeout: 6000 });
        const stations = Array.isArray(response.data) ? response.data : [];
        setStationSuggestions(stations);
      } catch (err) {
        setStationSuggestions([]);
      }
    };

    fetchStationSuggestions();
  }, []);


  // Manual refresh handler: allows user to refresh dashboard data
  const handleRefresh = () => {
    setIsLoading(true);
    loadDashboard();
  };

  const handleQuickSearch = () => {
    navigate("/search", { state: { from: quickFrom, to: quickTo } });
  };

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <main className="cfr-shell container-fluid">
        <section className="cfr-home-hero mb-4">
          <div className="cfr-home-overlay"></div>
          <div className="row g-4 align-items-stretch position-relative">
            <div className="col-xl-6">
              <div className="cfr-home-copy">
                <span className="cfr-muted-chip mb-3 d-inline-block">Romanian Rail Platform</span>
                <h1 className="display-5 fw-bold mb-3">Cumpara bilete de tren online</h1>
                <p className="lead mb-4">
                  Gaseste rapid curse interne, verifica disponibilitatea si rezerva in cativa pasi.
                </p>
                <div className="d-flex flex-wrap gap-2">
                  <button className="btn cfr-cta-orange px-4" onClick={() => navigate("/search")} type="button">
                    Trafic intern
                  </button>
                  <button className="btn cfr-cta-orange-outline px-4" onClick={() => navigate("/search")} type="button">
                    Trafic international
                  </button>
                </div>
              </div>
            </div>
            <div className="col-xl-6">
              <div className="row g-3">
                <div className="col-12 col-lg-6">
                  <div className="card cfr-booking-card h-100">
                    <div className="card-body">
                      <h5 className="cfr-booking-title">Mers Tren Trafic Intern</h5>
                      <label className="form-label mt-3">De la</label>
                      <input
                        className="form-control"
                        list="dashboard-station-suggestions"
                        value={quickFrom}
                        onChange={(e) => setQuickFrom(e.target.value)}
                        placeholder="Introdu statia de plecare"
                      />
                      <label className="form-label mt-3">Pana la</label>
                      <input
                        className="form-control"
                        list="dashboard-station-suggestions"
                        value={quickTo}
                        onChange={(e) => setQuickTo(e.target.value)}
                        placeholder="Introdu statia de sosire"
                      />
                      <button className="btn cfr-primary text-white w-100 mt-4" onClick={handleQuickSearch} type="button">
                        Cauta
                      </button>
                      <datalist id="dashboard-station-suggestions">
                        {stationSuggestions.map((stationName) => (
                          <option key={stationName} value={stationName} />
                        ))}
                      </datalist>
                    </div>
                  </div>
                </div>
                <div className="col-12 col-lg-6">
                  <div className="card cfr-promo-card h-100">
                    <div className="card-body d-flex flex-column justify-content-between">
                      <div>
                        <p className="cfr-promo-badge mb-2">Oferta Speciala</p>
                        <h5 className="mb-3">Calatoreste inteligent cu reduceri la rezervari online</h5>
                        <p className="mb-0">Activeaza cautarea rapida si confirma plata direct din platforma.</p>
                      </div>
                      <button className="btn btn-light mt-3" onClick={() => navigate("/search")} type="button">
                        Cumpara acum
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="cfr-status-panel p-3 p-md-4 mb-4">
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5 className="mb-0">System Feed</h5>
            <button
              className="btn btn-sm btn-outline-light ms-2"
              style={{ fontSize: "0.9rem" }}
              onClick={handleRefresh}
              disabled={isLoading}
              title="Refresh dashboard"
            >
              {isLoading ? (
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              ) : (
                <span>&#x21bb;</span>
              )}
            </button>
          </div>
          <p className="mb-1">{backendInfo}</p>
          <small className="text-white-50">
            {isLoading ? "Refreshing dashboard metrics..." : "Dashboard updated just now."}
          </small>
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
