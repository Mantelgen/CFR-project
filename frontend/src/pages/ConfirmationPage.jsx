import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { useSearchParams, useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import TopTaskbar from "../components/TopTaskbar";

const ConfirmationPage = ({ type = "email" }) => {
  const [searchParams] = useSearchParams();
  const [message, setMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  const confirmAction = useCallback(async (token) => {
    try {
      let endpoint;
      let successMsg;

      if (type === "email") {
        endpoint = `/api/auth/confirm-email?token=${token}`;
        successMsg = "Email confirmed successfully! You can now login.";
      } else {
        endpoint = `/api/reservations/confirm?token=${token}`;
        successMsg =
          "Reservation confirmed successfully! Check your email for details.";
      }

      const response = await axios.post(endpoint);

      if (response.data.success) {
        setMessage(successMsg);
        setIsSuccess(true);
        setTimeout(() => {
          if (type === "email") {
            navigate("/login");
          } else {
            navigate("/my-reservations");
          }
        }, 3000);
      }
    } catch (err) {
      setMessage(
        err.response?.data?.error ||
          "Error confirming. Please try again or contact support."
      );
      setIsSuccess(false);
    } finally {
      setIsLoading(false);
    }
  }, [navigate, type]);

  useEffect(() => {
    const token = searchParams.get("token");

    if (!token) {
      setMessage("Invalid or missing confirmation token");
      setIsSuccess(false);
      setIsLoading(false);
      return;
    }

    confirmAction(token);
  }, [confirmAction, searchParams]);

  if (isLoading) {
    return (
      <div className="cfr-page-bg">
        <TopTaskbar />
        <div className="container mt-5">
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-3">Processing confirmation...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="container mt-5">
        <div className="row justify-content-center">
          <div className="col-md-8 col-lg-6">
            <div className="card cfr-card shadow-lg">
              <div className="card-body p-5 text-center">
                {isSuccess ? (
                  <>
                    <h1 className="text-success mb-3">Success</h1>
                    <h2>Confirmation Successful</h2>
                    <p className="mt-3">{message}</p>
                    <p className="text-muted">
                      Redirecting automatically in 3 seconds...
                    </p>
                  </>
                ) : (
                  <>
                    <h1 className="text-danger mb-3">Failed</h1>
                    <h2>Confirmation Failed</h2>
                    <p className="mt-3 text-danger">{message}</p>
                    <button
                      className="btn btn-primary mt-3"
                      onClick={() => navigate("/")}
                    >
                      Go Home
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfirmationPage;
