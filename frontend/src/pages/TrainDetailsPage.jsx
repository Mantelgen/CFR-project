import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import TopTaskbar from "../components/TopTaskbar";

const TrainDetailsPage = () => {
  const { trainId } = useParams();
  const navigate = useNavigate();
  const [train, setTrain] = useState(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchTrain = async () => {
      try {
        const response = await axios.get(`/api/trains/${trainId}`);
        setTrain(response.data);
      } catch (err) {
        setError("Could not load train details.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchTrain();
  }, [trainId]);

  if (isLoading) {
    return (
      <div className="cfr-page-bg">
        <TopTaskbar />
        <div className="container mt-5 text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    );
  }

  if (error || !train) {
    return (
      <div className="cfr-page-bg">
        <TopTaskbar />
        <div className="container mt-5 text-center">
          <div className="alert alert-danger">{error || "Train not found."}</div>
          <button className="btn btn-primary" onClick={() => navigate(-1)}>
            Go Back
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="cfr-page-bg">
      <TopTaskbar />
      <div className="container mt-5">
        <div className="card cfr-card shadow-lg">
          <div className="card-body p-5">
            <h2 className="mb-4">Train {train.trainNumber}</h2>
            <p><strong>From:</strong> {train.departureStationName}</p>
            <p><strong>To:</strong> {train.arrivalStationName}</p>
            <p><strong>Departure Time:</strong> {train.departureTime}</p>
            <p><strong>Arrival Time:</strong> {train.arrivalTime}</p>
            {/* Add more details as needed */}
            <button className="btn btn-secondary mt-3" onClick={() => navigate(-1)}>
              Back to Search
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TrainDetailsPage;
