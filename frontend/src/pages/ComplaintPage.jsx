import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopTaskbar from "../components/TopTaskbar";
import axiosWithCsrf from "../axiosWithCsrf";

const ComplaintPage = () => {
  const navigate = useNavigate();
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [reservationId, setReservationId] = useState("");
  const [status, setStatus] = useState({ type: "", text: "" });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
    }
  }, [navigate]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus({ type: "", text: "" });

    if (!subject.trim() || !message.trim()) {
      setStatus({ type: "danger", text: "Subject and message are required." });
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        subject: subject.trim(),
        message: message.trim(),
      };

      const parsedReservationId = parseInt(reservationId, 10);
      if (Number.isInteger(parsedReservationId) && parsedReservationId > 0) {
        payload.reservationId = parsedReservationId;
      }

      const response = await axiosWithCsrf.post("/api/complaints", payload);
      if (response.data?.success) {
        setStatus({ type: "success", text: "Complaint sent. A confirmation email was sent to your address." });
        setSubject("");
        setMessage("");
        setReservationId("");
      }
    } catch (error) {
      setStatus({ type: "danger", text: error.response?.data?.error || "Could not send complaint." });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="cfr-shell container-fluid">
        <div className="cfr-hero p-4 mb-4">
          <h1 className="mb-1">Submit a Complaint</h1>
          <p className="mb-0">We send your issue directly to CFR support via the cfr.local mail service.</p>
        </div>

        <div className="card cfr-card">
          <div className="card-body p-4">
            {status.text && (
              <div className={`alert alert-${status.type}`}>{status.text}</div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Subject</label>
                <input
                  type="text"
                  className="form-control"
                  value={subject}
                  onChange={(event) => setSubject(event.target.value)}
                  placeholder="e.g. Seat issue on IR1745"
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Reservation ID (optional)</label>
                <input
                  type="number"
                  className="form-control"
                  min="1"
                  value={reservationId}
                  onChange={(event) => setReservationId(event.target.value)}
                  placeholder="e.g. 42"
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Message</label>
                <textarea
                  className="form-control"
                  rows="6"
                  value={message}
                  onChange={(event) => setMessage(event.target.value)}
                  placeholder="Describe your issue"
                />
              </div>

              <button type="submit" className="btn cfr-primary text-white" disabled={isSubmitting}>
                {isSubmitting ? "Sending..." : "Send Complaint"}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ComplaintPage;
