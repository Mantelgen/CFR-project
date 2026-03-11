import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_BASE_URL = "/api/trains";

function App() {
  const [trains, setTrains] = useState([]);
  const [serverInfo, setServerInfo] = useState('Loading...');
  const [form, setForm] = useState({ trainNumber: '', departureCity: '', arrivalCity: '' });

  useEffect(() => { 
    fetchTrains();
    fetchServerInfo();
  }, []);

  const fetchServerInfo = async () => {
    try {
      const response = await axios.get('/api/info');
      setServerInfo(response.data);
    } catch (error) {
      console.error("Error fetching server info:", error);
      setServerInfo('Error loading server info');
    }
  };

  const fetchTrains = async () => {
    try {
      const response = await axios.get(API_BASE_URL);
      setTrains(response.data);
    } catch (error) { 
      console.error("Error fetching trains:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(API_BASE_URL, form);
      setForm({ trainNumber: '', departureCity: '', arrivalCity: '' });
      fetchTrains();
    } catch (error) {
      console.error("Error creating train:", error);
      alert("Failed to add train");
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`${API_BASE_URL}/${id}`);
      fetchTrains();
    } catch (error) {
      console.error("Error deleting train:", error);
      alert("Failed to delete train");
    }
  };

  return (
    <div className="container" style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>🚂 CFR Train Management System</h1>
      
      {/* Server Info Display - REQUIRED BY PROJECT SPEC */}
      <div style={{ 
        backgroundColor: '#e3f2fd', 
        padding: '10px', 
        borderRadius: '5px', 
        marginBottom: '20px',
        border: '1px solid #2196f3'
      }}>
        <strong>📡 {serverInfo}</strong>
      </div>

      <h2>Add New Train</h2>
      <form onSubmit={handleSubmit} style={{ marginBottom: '30px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
        <input 
          type="text" 
          placeholder="Train Number" 
          value={form.trainNumber} 
          onChange={e => setForm({...form, trainNumber: e.target.value})} 
          required 
          style={{ padding: '8px', minWidth: '150px' }}
        />
        <input 
          type="text" 
          placeholder="Departure City" 
          value={form.departureCity} 
          onChange={e => setForm({...form, departureCity: e.target.value})} 
          style={{ padding: '8px', minWidth: '150px' }}
        />
        <input 
          type="text" 
          placeholder="Arrival City" 
          value={form.arrivalCity} 
          onChange={e => setForm({...form, arrivalCity: e.target.value})} 
          style={{ padding: '8px', minWidth: '150px' }}
        />
        <button type="submit" style={{ 
          padding: '8px 20px', 
          backgroundColor: '#4caf50', 
          color: 'white', 
          border: 'none', 
          borderRadius: '4px', 
          cursor: 'pointer' 
        }}>
          ➕ Add Train
        </button>
      </form>

      <h2>Train List ({trains.length} trains)</h2>
      <table border="1" cellPadding="10" style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead style={{ backgroundColor: '#f5f5f5' }}>
          <tr>
            <th>Train Number</th>
            <th>Departure City</th>
            <th>Arrival City</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {trains.length === 0 ? (
            <tr>
              <td colSpan="4" style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
                No trains found. Add one above!
              </td>
            </tr>
          ) : (
            trains.map(train => (
              <tr key={train.id}>
                <td>{train.trainNumber}</td>
                <td>{train.departureCity || '-'}</td>
                <td>{train.arrivalCity || '-'}</td>
                <td>
                  <button 
                    onClick={() => handleDelete(train.id)}
                    style={{ 
                      padding: '5px 15px', 
                      backgroundColor: '#f44336', 
                      color: 'white', 
                      border: 'none', 
                      borderRadius: '4px', 
                      cursor: 'pointer' 
                    }}
                  >
                    🗑️ Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default App;