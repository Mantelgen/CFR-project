import { useState } from "react";

function TrainSearchPage() {

    const [from, setFrom] = useState("");
    const [to, setTo] = useState("");
    const [trains, setTrains] = useState([]);

    const search = async () => {

        const response = await fetch(`/api/trains/search?from=${from}&to=${to}`);

        const data = await response.json();

        setTrains(data);

    };

    return (
        <div>

            <h2>Search Trains</h2>

            <input
                placeholder="From"
                value={from}
                onChange={(e) => setFrom(e.target.value)}
            />

            <input
                placeholder="To"
                value={to}
                onChange={(e) => setTo(e.target.value)}
            />

            <button onClick={search}>
                Search
            </button>

            <ul>

                {trains.map(train => (

                    <li key={train.id}>

                        Train {train.trainNumber}

                        {train.departureTime}

                        →

                        {train.arrivalTime}

                    </li>

                ))}

            </ul>

        </div>
    );

}

export default TrainSearchPage;