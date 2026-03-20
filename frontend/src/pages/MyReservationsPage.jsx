import React, { useState, useEffect } from "react";
import axiosWithCsrf from "../axiosWithCsrf";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import TopTaskbar from "../components/TopTaskbar";

const MyReservationsPage = () => {
  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
      return;
    }

    fetchReservations();
  }, [navigate]);

  const fetchReservations = async () => {
    try {
      const response = await axiosWithCsrf.get("/api/reservations/my-reservations", {
        params: { userId: localStorage.getItem("userId") },
      });
      setReservations(response.data || []);
    } catch (err) {
      setError("Error fetching reservations. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (window.confirm("Are you sure you want to cancel this reservation?")) {
      setCancellingId(id);
      try {
        await axiosWithCsrf.delete(`/api/reservations/${id}`, {
          params: { userId: localStorage.getItem("userId") },
        });
        setReservations(
          reservations.filter((reservation) => reservation.id !== id)
        );
      } catch (err) {
        setError("Error cancelling reservation. Please try again.");
      } finally {
        setCancellingId(null);
      }
    }
  };

  if (isLoading) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="cfr-shell container-fluid">
      {/* Header */}
      <div className="cfr-hero p-4 mb-4">
        <div className="row align-items-center">
          <div className="col-md-8">
            <h1 className="mb-1">My Reservations</h1>
            <p className="mb-0">Track payment and booking confirmations</p>
          </div>
          <div className="col-md-4 text-md-end mt-3 mt-md-0"></div>
        </div>
      </div>

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

      {reservations.length === 0 ? (
        <div className="alert alert-info">
          <h5>No Reservations Yet</h5>
          <p>You haven't made any reservations. Start by searching for trains!</p>
          <button
            className="btn btn-primary"
            onClick={() => navigate("/search")}
          >
            Search Trains
          </button>
        </div>
      ) : (
        <div className="card cfr-card">
          <div className="card-body">
            <div className="table-responsive">
              <table className="table table-hover cfr-table">
                <thead>
                  <tr>
                    <th>Train Number</th>
                    <th>Seats</th>
                    <th>Seat Numbers</th>
                    <th>Status</th>
                    <th>Confirmation</th>
                    <th>Booked On</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {reservations.map((reservation) => (
                    <tr key={reservation.id}>
                      <td className="fw-bold">
                        {reservation.train?.trainNumber || "N/A"}
                      </td>
                      <td>{reservation.numberOfSeats}</td>
                      <td>{reservation.selectedSeatNumbers || "Auto-assigned"}</td>
                      <td>
                        <span
                          className={`badge bg-${
                            reservation.status === "CONFIRMED"
                              ? "success"
                              : reservation.status === "AWAITING_PAYMENT"
                              ? "info"
                              : reservation.status === "PENDING"
                              ? "warning"
                              : "danger"
                          }`}
                        >
                          {reservation.status}
                        </span>
                      </td>
                      <td>
                        <span
                          className={`badge bg-${
                            reservation.isConfirmed ? "success" : "warning"
                          }`}
                        >
                          {reservation.isConfirmed
                            ? "Confirmed"
                            : "Pending Confirmation"}
                        </span>
                      </td>
                      <td>
                        {new Date(
                          reservation.reservationDate
                        ).toLocaleDateString()}
                      </td>
                      <td>
                        {reservation.status !== "CANCELLED" && (
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() => handleCancel(reservation.id)}
                            disabled={cancellingId === reservation.id}
                          >
                            {cancellingId === reservation.id
                              ? "Cancelling..."
                              : "Cancel"}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
      </div>
    </div>
  );
};

export default MyReservationsPage;
