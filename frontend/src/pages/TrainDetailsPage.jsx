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

  const buildComposition = (class1Count, class2Count) => {
    const safeClass1 = Number.isFinite(class1Count) ? class1Count : 0;
    const safeClass2 = Number.isFinite(class2Count) ? class2Count : 0;
    const composition = [{ key: "loco", label: "Locomotive", short: "L", type: "locomotive" }];

    for (let i = 0; i < safeClass1; i += 1) {
      composition.push({
        key: `c1-${i + 1}`,
        label: `Class 1 Carriage ${i + 1}`,
        short: "C1",
        type: "class1",
      });
    }

    for (let i = 0; i < safeClass2; i += 1) {
      composition.push({
        key: `c2-${i + 1}`,
        label: `Class 2 Carriage ${i + 1}`,
        short: "C2",
        type: "class2",
      });
    }

    return composition;
  };

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

  const class1Carriages = train.class1Carriages ?? 0;
  const class2Carriages = train.class2Carriages ?? 0;
  const totalCarriages = train.totalCarriages ?? class1Carriages + class2Carriages;
  const composition = buildComposition(class1Carriages, class2Carriages);
  const routeStations = Array.isArray(train.routeStations) && train.routeStations.length > 0
    ? train.routeStations
    : [train.departureStationName, train.arrivalStationName];

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

            <hr />
            <h5 className="mb-3">Train Composition</h5>
            <p><strong>Total Carriages:</strong> {totalCarriages}</p>
            <p><strong>Class 1 Carriages:</strong> {class1Carriages}</p>
            <p><strong>Class 2 Carriages:</strong> {class2Carriages}</p>

            <div className="cfr-train-composition-strip" aria-label="Train composition image">
              {composition.map((part) => (
                <div
                  key={part.key}
                  className={`cfr-train-part cfr-train-part-${part.type}`}
                  title={part.label}
                >
                  <span>{part.short}</span>
                </div>
              ))}
            </div>

            <small className="text-muted d-block mt-2">
              Visual order: locomotive first, followed by Class 1 then Class 2 carriages.
            </small>

            <hr />
            <h5 className="mb-3">Stations On Route</h5>
            <ol className="cfr-route-timeline">
              {routeStations.map((stationName, index) => (
                <li key={`${stationName}-${index}`} className="cfr-route-stop">
                  <span className="cfr-route-dot" aria-hidden="true"></span>
                  <span>{stationName}</span>
                </li>
              ))}
            </ol>

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
