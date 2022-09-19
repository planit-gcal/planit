import React, { useEffect, useState } from "react";
import logo from "./logo.svg";
import "./App.css";

function App() {
  const [response, setResponse] = useState<any>("no response yet");

  useEffect(() => {
    const url = `${process.env.REACT_APP_API_BASE_URL}/plan-it/people`;

    // a bunch of terrible test code
    fetch(url, {})
      .then((response) => {
        if (response.status === 204) {
          return "empty response";
        }

        return response.json();
      })
      .then((data) => {
        setResponse(data);
      });
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>{JSON.stringify(response)}</p>
      </header>
    </div>
  );
}

export default App;
