import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import TrainSearchPage from "./pages/TrainSearchPage";
import AuthPage from "./pages/AuthPage";
import MyReservationsPage from "./pages/MyReservationsPage";
import ConfirmationPage from "./pages/ConfirmationPage";
import "./styles/app-theme.css";

function App() {
  return (
    <Router>

      <Routes>

        {/* Auth Routes */}
        <Route path="/login" element={<AuthPage isLogin={true} />} />
        <Route path="/register" element={<AuthPage isLogin={false} />} />

        {/* Confirmation Routes */}
        <Route 
          path="/confirm-email" 
          element={<ConfirmationPage type="email" />} 
        />
        <Route 
          path="/confirm-reservation" 
          element={<ConfirmationPage type="reservation" />} 
        />

        {/* Main Routes */}
        <Route path="/search" element={<TrainSearchPage />} />
        <Route path="/my-reservations" element={<MyReservationsPage />} />

        {/* Default Route */}
        <Route path="/" element={<Navigate to="/search" replace />} />

      </Routes>

    </Router>
  );
}

export default App;