import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import TrainSearchPage from "./pages/TrainSearchPage";

function App() {
  return (
    <Router>

      <Routes>

        {/* Main page */}
        <Route path="/" element={<TrainSearchPage />} />

      </Routes>

    </Router>
  );
}

export default App;