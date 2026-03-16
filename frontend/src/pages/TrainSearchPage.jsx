import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";

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
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
    }
  }, [navigate]);

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

    try {
      const response = await axios.post("/api/reservations/book", {
        trainId: selectedTrain.id,
        numberOfSeats: parseInt(numberOfSeats),
      });

      if (response.data.success) {
        setBookingSuccess(true);
        setSelectedTrain(null);
        setIsBooking(false);
        setTimeout(() => {
          setBookingSuccess(false);
          navigate("/my-reservations");
        }, 3000);
      }
    } catch (err) {
      setError(
        err.response?.data?.error || "Error booking train. Please try again."
      );
    }
  };

  return (
    <div className="container-fluid mt-4">
      {/* Header */}
      <div className="row mb-4">
        <div className="col">
          <h1>CFR Train Booking</h1>
          <p className="text-muted">
            Welcome, {localStorage.getItem("username")}
          </p>
        </div>
        <div className="col text-end">
          <button
            className="btn btn-info me-2"
            onClick={() => navigate("/my-reservations")}
          >
            My Reservations
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => {
              localStorage.clear();
              navigate("/login");
            }}
          >
            Logout
          </button>
        </div>
      </div>

      {/* Search Form */}
      <div className="card mb-4">
        <div className="card-body">
          <h5 className="card-title">Search Trains</h5>
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
                  className="btn btn-primary w-100"
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
          Reservation created successfully! Redirecting to your reservations...
        </div>
      )}

      {/* Trains List */}
      {trains.length > 0 && (
        <div className="card">
          <div className="card-body">
            <h5 className="card-title">
              Available Trains ({trains.length})
            </h5>
            <div className="table-responsive">
              <table className="table table-hover">
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
                          className="btn btn-sm btn-success"
                          onClick={() => handleBook(train)}
                        >
                          Book Now
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
                  className="btn btn-primary"
                  onClick={submitBooking}
                >
                  Confirm Booking
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TrainSearchPage;
