import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import TopTaskbar from "../components/TopTaskbar";

const TrainSearchPage = () => {
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [trains, setTrains] = useState([]);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [numberOfSeats, setNumberOfSeats] = useState(1);
  const [isBooking, setIsBooking] = useState(false);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const [isPaying, setIsPaying] = useState(false);
  const [reservationId, setReservationId] = useState(null);
  const [backendInfo, setBackendInfo] = useState("Connecting...");
  const [paymentForm, setPaymentForm] = useState({
    cardHolder: "",
    cardNumber: "",
    expiry: "",
    cvv: "",
  });
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
    }
  }, [navigate]);

  useEffect(() => {
    const fetchBackendInfo = async () => {
      try {
        const response = await axios.get("/api/info", {
          timeout: 5000,
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
    };

    fetchBackendInfo();
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    setError("");
    setTrains([]);
    setSelectedTrain(null);
    setIsLoading(true);

    if (!from || !to) {
      setError("Please select both departure and arrival stations");
      setIsLoading(false);
      return;
    }

    try {
      const response = await axios.get(`/api/trains/search`, {
        params: { from, to },
      });
      setTrains(response.data || []);
      if (response.data.length === 0) {
        setError("No trains found for this route");
      }
    } catch (err) {
      setError("Error searching for trains. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleBook = async (train) => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
      return;
    }

    setSelectedTrain(train);
    setNumberOfSeats(1);
    setIsBooking(true);
  };

  const submitBooking = async () => {
    if (!selectedTrain) return;
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.post("/api/reservations/book", {
        userId,
        trainId: selectedTrain.id,
        numberOfSeats: parseInt(numberOfSeats),
      }, {
        withCredentials: true,
      });

      if (response.data.success) {
        setReservationId(response.data.reservationId);
        setSelectedTrain(null);
        setIsBooking(false);
        setIsPaying(true);
      }
    } catch (err) {
      setError(
        err.response?.data?.error || "Error booking train. Please try again."
      );
    }
  };

  const confirmMockPayment = async () => {
    if (!reservationId) {
      setError("Missing reservation for payment.");
      return;
    }

    if (!paymentForm.cardHolder || !paymentForm.cardNumber || !paymentForm.expiry || !paymentForm.cvv) {
      setError("Please complete all payment fields.");
      return;
    }

    try {
      const response = await axios.post("/api/reservations/pay", {
        userId: localStorage.getItem("userId"),
        reservationId,
        paymentMethod: "MOCK_CARD",
      }, {
        withCredentials: true,
      });

      if (response.data.success) {
        setIsPaying(false);
        setBookingSuccess(true);
        setPaymentForm({ cardHolder: "", cardNumber: "", expiry: "", cvv: "" });
        setTimeout(() => {
          setBookingSuccess(false);
          navigate("/my-reservations");
        }, 2800);
      }
    } catch (err) {
      setError(err.response?.data?.error || "Payment failed. Please try again.");
    }
  };

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="cfr-shell container-fluid">
      {/* Header */}
      <div className="cfr-hero p-4 mb-4">
        <div className="row align-items-center">
          <div className="col-md-8">
            <h1 className="mb-1">CFR Train Booking</h1>
            <p className="mb-0">
            Welcome, {localStorage.getItem("username")}
            </p>
            <p className="mb-0 mt-1" style={{ fontSize: "0.95rem", opacity: 0.95 }}>
              Connected backend: {backendInfo}
            </p>
          </div>
          <div className="col-md-4 text-md-end mt-3 mt-md-0"></div>
        </div>
      </div>

      {/* Search Form */}
      <div className="card cfr-card mb-4">
        <div className="card-body">
          <div className="d-flex align-items-center justify-content-between mb-2">
            <h5 className="card-title mb-0">Search Trains</h5>
            <span className="cfr-muted-chip">Live route finder</span>
          </div>
          <form onSubmit={handleSearch}>
            <div className="row">
              <div className="col-md-4 mb-3">
                <label htmlFor="from" className="form-label">
                  From Station
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="from"
                  placeholder="e.g., Cluj"
                  value={from}
                  onChange={(e) => setFrom(e.target.value)}
                />
              </div>
              <div className="col-md-4 mb-3">
                <label htmlFor="to" className="form-label">
                  To Station
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="to"
                  placeholder="e.g., Bucuresti"
                  value={to}
                  onChange={(e) => setTo(e.target.value)}
                />
              </div>
              <div className="col-md-4 d-flex align-items-end">
                <button
                  type="submit"
                  className="btn cfr-primary text-white w-100"
                  disabled={isLoading}
                >
                  {isLoading ? "Searching..." : "Search"}
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="alert alert-danger alert-dismissible fade show">
          {error}
          <button
            type="button"
            className="btn-close"
            onClick={() => setError("")}
          ></button>
        </div>
      )}

      {/* Booking Success Message */}
      {bookingSuccess && (
        <div className="alert alert-success alert-dismissible fade show">
          Payment confirmed. Your booking is confirmed and an email has been sent.
        </div>
      )}

      {/* Trains List */}
      {trains.length > 0 && (
        <div className="card cfr-card">
          <div className="card-body">
            <h5 className="card-title">
              Available Trains ({trains.length})
            </h5>
            <div className="table-responsive">
              <table className="table table-hover cfr-table">
                <thead>
                  <tr>
                    <th>Train Number</th>
                    <th>Departure</th>
                    <th>Arrival</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {trains.map((train) => (
                    <tr key={train.id}>
                      <td className="fw-bold">{train.trainNumber}</td>
                      <td>{train.departureTime}</td>
                      <td>{train.arrivalTime}</td>
                      <td>
                        <button
                          className="btn btn-sm cfr-accent me-2"
                          onClick={() => handleBook(train)}
                        >
                          Book Now
                        </button>
                        <button
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => navigate(`/train/${train.id}`)}
                        >
                          Details
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Booking Modal */}
      {isBooking && selectedTrain && (
        <div
          className="modal d-block"
          style={{
            backgroundColor: "rgba(0, 0, 0, 0.5)",
          }}
        >
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Book Train</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setIsBooking(false)}
                ></button>
              </div>
              <div className="modal-body">
                <p>
                  <strong>Train:</strong> {selectedTrain.trainNumber}
                </p>
                <p>
                  <strong>Departure:</strong> {selectedTrain.departureTime}
                </p>
                <p>
                  <strong>Arrival:</strong> {selectedTrain.arrivalTime}
                </p>
                <div className="mb-3">
                  <label htmlFor="seats" className="form-label">
                    Number of Seats
                  </label>
                  <input
                    type="number"
                    className="form-control"
                    id="seats"
                    min="1"
                    max="10"
                    value={numberOfSeats}
                    onChange={(e) => setNumberOfSeats(e.target.value)}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsBooking(false)}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn cfr-primary text-white"
                  onClick={submitBooking}
                >
                  Confirm Booking
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {isPaying && (
        <div className="modal d-block cfr-modal">
          <div className="modal-dialog">
            <div className="modal-content cfr-card">
              <div className="modal-header">
                <h5 className="modal-title">Mock Payment</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setIsPaying(false)}
                ></button>
              </div>
              <div className="modal-body">
                <p className="text-muted mb-3">Demo flow: enter any values and confirm payment.</p>
                <div className="mb-3">
                  <label className="form-label">Card Holder</label>
                  <input
                    type="text"
                    className="form-control"
                    value={paymentForm.cardHolder}
                    onChange={(e) => setPaymentForm({ ...paymentForm, cardHolder: e.target.value })}
                    placeholder="Name Surname"
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Card Number</label>
                  <input
                    type="text"
                    className="form-control"
                    value={paymentForm.cardNumber}
                    onChange={(e) => setPaymentForm({ ...paymentForm, cardNumber: e.target.value })}
                    placeholder="4242 4242 4242 4242"
                  />
                </div>
                <div className="row">
                  <div className="col-6 mb-3">
                    <label className="form-label">Expiry</label>
                    <input
                      type="text"
                      className="form-control"
                      value={paymentForm.expiry}
                      onChange={(e) => setPaymentForm({ ...paymentForm, expiry: e.target.value })}
                      placeholder="MM/YY"
                    />
                  </div>
                  <div className="col-6 mb-3">
                    <label className="form-label">CVV</label>
                    <input
                      type="password"
                      className="form-control"
                      value={paymentForm.cvv}
                      onChange={(e) => setPaymentForm({ ...paymentForm, cvv: e.target.value })}
                      placeholder="123"
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-outline-secondary" onClick={() => setIsPaying(false)}>
                  Close
                </button>
                <button type="button" className="btn cfr-primary text-white" onClick={confirmMockPayment}>
                  Confirm Payment
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
      </div>
    </div>
  );
};

export default TrainSearchPage;
