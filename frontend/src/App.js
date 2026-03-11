import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_BASE_URL = "/api/trains"; // Proxy via Nginx

function App() {
  const [trains, setTrains] = useState([]);
  const [form, setForm] = useState({ trainNumber: '', departureCity: '', arrivalCity: '' });

  useEffect(() => { fetchTrains(); }, []);

  const fetchTrains = async () => {
    try {
      const response = await axios.get(API_BASE_URL);
      setTrains(response.data);
    } catch (error) { console.error("Error fetching trains:", error); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await axios.post(API_BASE_URL, form);
    setForm({ trainNumber: '', departureCity: '', arrivalCity: '' });
    fetchTrains();
  };

  const handleDelete = async (id) => {
    await axios.delete(`${API_BASE_URL}/${id}`);
    fetchTrains();
  };

  return (
    <div className="container" style={{ padding: '20px' }}>
      <h2>CFR Train Management</h2>
      <form onSubmit={handleSubmit} style={{ marginBottom: '20px' }}>
        <input type="text" placeholder="Train Number" value={form.trainNumber} 
               onChange={e => setForm({...form, trainNumber: e.target.value})} required />
        <input type="text" placeholder="Departure" value={form.departureCity} 
               onChange={e => setForm({...form, departureCity: e.target.value})} />
        <input type="text" placeholder="Arrival" value={form.arrivalCity} 
               onChange={e => setForm({...form, arrivalCity: e.target.value})} />
        <button type="submit">Add Train</button>
      </form>

      <table border="1" cellPadding="10">
        <thead>
          <tr><th>Number</th><th>Departure</th><th>Arrival</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {trains.map(train => (
            <tr key={train.id}>
              <td>{train.trainNumber}</td>
              <td>{train.departureCity}</td>
              <td>{train.arrivalCity}</td>
              <td><button onClick={() => handleDelete(train.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;